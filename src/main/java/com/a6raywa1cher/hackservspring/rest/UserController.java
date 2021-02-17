package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.rest.exc.EmailAlreadyExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateUserRequest;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;


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
}
