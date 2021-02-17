package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.rest.exc.EmailAlreadyExistsException;
import com.a6raywa1cher.hackservspring.rest.exc.UserNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateUserRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutUserInfoRequest;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.service.dto.UserInfo;
import com.a6raywa1cher.hackservspring.utils.LocalHtmlUtils;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping("/user")
@Transactional(rollbackOn = Exception.class)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

        userInfo.setFullName(LocalHtmlUtils.htmlEscape(request.getFullName(), 250));
        userInfo.setTelegram(LocalHtmlUtils.htmlEscape(request.getTelegram(), 250));
        userInfo.setDateOfBirth(request.getDateOfBirth());
        userInfo.setWorkPlace(LocalHtmlUtils.htmlEscape(request.getWorkPlace(), 250));
        userInfo.setOtherInfo(LocalHtmlUtils.htmlEscape(request.getOtherInfo(), 250));

        User user = userService.editUserInfo(optionalUser.get(), userInfo);

        return ResponseEntity.ok(user);
    }
}
