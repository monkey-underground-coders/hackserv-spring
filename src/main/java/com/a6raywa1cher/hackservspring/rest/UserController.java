package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.rest.exc.EmailAlreadyExistsException;
import com.a6raywa1cher.hackservspring.rest.exc.UserNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateUserRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutUserInfoRequest;
import com.a6raywa1cher.hackservspring.service.DiscService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.service.dto.UserInfo;
import com.a6raywa1cher.hackservspring.utils.ServiceUtils;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;


@RestController
@RequestMapping("/user")
@Transactional(rollbackOn = Exception.class)
public class UserController {

    private final UserService userService;
    private final DiscService discService;

    public UserController(UserService userService, DiscService discService) {
        this.userService = userService;
        this.discService = discService;
    }

    @Operation(security = @SecurityRequirement(name = "jwt"))
    @GetMapping(value="/{uid}/cv/")
    public ResponseEntity<Resource> getResume (@PathVariable Long uid) throws UserNotExistsException {
        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()){
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();
        Resource file = discService.getResource(user.getDocumentResumePath());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + user.getFullName() + user.getDateOfBirth() +
                        file.getFilename().substring(file.getFilename().lastIndexOf('.'))).body(file);
    }

    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PostMapping(path="/{uid}/cv/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createResume(@PathVariable Long uid, @RequestParam("file") MultipartFile file) throws UserNotExistsException, IOException {
        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();
        if (user.getDocumentResumePath() != null){
            deleteResumeDocument(uid);
        }
        user = userService.setDocumentResumePath(user, discService.create(file));
        return ResponseEntity.ok(user);
    }

    @Operation(security = @SecurityRequirement(name = "jwt"))
    @DeleteMapping(value="/{uid}/cv/")
    public ResponseEntity<User> deleteResumeDocument(@PathVariable Long uid) throws UserNotExistsException {
        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();
        user = userService.deleteResume(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    @JsonView(Views.Internal.class)
    public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest request) throws EmailAlreadyExistsException {
        if (userService.getByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }
        User user = userService.create(UserRole.USER, request.getEmail(), request.getPassword());
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{uid:[0-9]+}")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.Internal.class)
    public ResponseEntity<User> editUserInfo(@RequestBody @Valid PutUserInfoRequest request, @PathVariable long uid) throws UserNotExistsException {
        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(request, userInfo);

        User user = userService.editUserInfo(optionalUser.get(), userInfo);

        return ResponseEntity.ok(user);
    }
}
