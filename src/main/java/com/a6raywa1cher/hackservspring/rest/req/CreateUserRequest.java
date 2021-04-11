package com.a6raywa1cher.hackservspring.rest.req;


import com.a6raywa1cher.hackservspring.utils.jackson.HtmlEscape;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateUserRequest {

	@Email
    @NotBlank
    @HtmlEscape
    private String email;

    @Size(min = 3, max = 128)
    @HtmlEscape
    private String password;

}
