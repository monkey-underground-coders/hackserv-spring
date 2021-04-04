package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.rest.exc.*;
import com.a6raywa1cher.hackservspring.rest.exc.EmailAlreadyExistsException;
import com.a6raywa1cher.hackservspring.rest.exc.FileSizeLimitExceededException;
import com.a6raywa1cher.hackservspring.rest.exc.UserNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateUserRequest;
import com.a6raywa1cher.hackservspring.rest.req.EmailValidationTokenRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutUserInfoRequest;
import com.a6raywa1cher.hackservspring.service.EmailValidationService;
import com.a6raywa1cher.hackservspring.service.DiscService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.service.dto.UserInfo;
import com.a6raywa1cher.hackservspring.utils.ServiceUtils;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
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
    private final EmailValidationService emailValidationService;
    private final DiscService discService;

    public UserController(UserService userService, DiscService discService) {
        this.userService = userService;
        this.emailValidationService = emailValidationService;
        this.discService = discService;
    }

    @Value("${spring.servlet.multipart.max-file-size}")
    DataSize maxFileSize;

    @GetMapping(value="/{uid}/cv/")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.DetailedInternal.class)
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

    @PostMapping(path="/{uid}/cv/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.DetailedInternal.class)
    public ResponseEntity<User> createResume(@PathVariable Long uid, @RequestParam("file") MultipartFile file) throws UserNotExistsException, IOException, FileSizeLimitExceededException {
        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        if (maxFileSize.toBytes() < file.getSize()){
            throw new FileSizeLimitExceededException();
        }
        User user = optionalUser.get();
        if (user.getDocumentResumePath() != null){
            deleteResumeDocument(uid);
        }
        user = userService.setDocumentResumePath(user, discService.create(file));
        return ResponseEntity.ok(user);
    }

    @DeleteMapping(value="/{uid}/cv/")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.DetailedInternal.class)
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
    @JsonView(Views.DetailedInternal.class)
    public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest request) throws EmailAlreadyExistsException, MessagingException {
        if (userService.getByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }
        User user = userService.create(UserRole.USER, request.getEmail(), request.getPassword());
        emailValidationService.createToken(user);
        emailValidationService.sendMassage(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{uid:[0-9]+}")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.DetailedInternal.class)
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


    @PostMapping("/{uid:[0-9]+}/email/req")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.Internal.class)
    public ResponseEntity<EmailValidationToken> sendEmailValidationToken(@PathVariable long uid) throws MessagingException, UserNotExistsException, TooManyValidationRequestsExсeption {

        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();
        if (emailValidationService.isLastSendWasRecently(user)) {
            throw new TooManyValidationRequestsExсeption();
        }
        emailValidationService.createToken(user);
        emailValidationService.sendMassage(user);

        return ResponseEntity.ok(user.getEmailValidationToken());
    }


    @PostMapping("/{uid:[0-9]+}/email/validate")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    public ResponseEntity<Void> validate(@RequestBody @Valid EmailValidationTokenRequest request, @PathVariable long uid) throws UserNotExistsException, TokenIsWrongException, TokenIsNotEnabledException {
        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();

        if (user.getEmailValidationToken() == null) {
            throw new TokenNotExistsException();
        }
        if (!emailValidationService.isTokenEnable(user)) {
            throw new TokenIsNotEnabledException();
        }
        if (!emailValidationService.checkToken(user, request.getToken())) {
            throw new TokenIsWrongException();
        }
        userService.editEmailValidated(user, true);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{uid:[0-9]+}/delete")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    public ResponseEntity<Void> delete(@PathVariable long uid) throws UserNotExistsException {
        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();

        userService.deleteUser(user);
        return ResponseEntity.ok().build();
    }

}
