package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.rest.exc.EmailAlreadyExistsException;
import com.a6raywa1cher.hackservspring.rest.exc.TokenIsWrongException;
import com.a6raywa1cher.hackservspring.rest.exc.UserNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateUserRequest;
import com.a6raywa1cher.hackservspring.rest.req.EmailValidationTokenRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutUserInfoRequest;
import com.a6raywa1cher.hackservspring.service.EmailValidationService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.service.dto.UserInfo;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping("/user")
@Transactional(rollbackOn = Exception.class)
public class UserController {

    private final UserService userService;
    private final EmailValidationService emailValidationService;

    public UserController(UserService userService, EmailValidationService emailValidationService) {
        this.userService = userService;
        this.emailValidationService = emailValidationService;
    }

    @PostMapping("/create")
    @JsonView(Views.Internal.class)
    public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest request) throws EmailAlreadyExistsException {
        if (userService.getByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        User user = userService.create(UserRole.USER, request.getEmail(), request.getPassword());
        emailValidationService.createToken(user);
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


    @PostMapping("/{uid:[0-9]+}/email/req")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.Internal.class)
    public ResponseEntity<EmailValidationToken> sendEmailValidationToken(@PathVariable long uid) throws MessagingException, UserNotExistsException {

        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();
        emailValidationService.sendMassage(user);

        return ResponseEntity.ok(user.getEmailValidationToken());
    }


    @PostMapping("/{uid:[0-9]+}/email/validate")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.Internal.class)
    public ResponseEntity<Void> validate(@RequestBody @Valid EmailValidationTokenRequest request, @PathVariable long uid) throws UserNotExistsException, TokenIsWrongException {
        Optional<User> optionalUser = userService.getById(uid);
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();

        if (!emailValidationService.checkToken(user, request.getToken())) {
            throw new TokenIsWrongException();
        }
        return ResponseEntity.ok().build();
    }
}
