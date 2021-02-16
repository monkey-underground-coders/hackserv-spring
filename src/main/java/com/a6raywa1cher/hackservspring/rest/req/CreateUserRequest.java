package com.a6raywa1cher.hackservspring.rest.req;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateUserRequest {

    @Email
    @NotBlank
    private String email;

    @Size(min = 3, max = 128)
    private String password;

}
