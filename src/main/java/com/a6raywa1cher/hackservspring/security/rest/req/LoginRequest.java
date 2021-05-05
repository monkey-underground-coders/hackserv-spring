package com.a6raywa1cher.hackservspring.security.rest.req;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginRequest {
	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 3, max = 128)
	private String password;
}
