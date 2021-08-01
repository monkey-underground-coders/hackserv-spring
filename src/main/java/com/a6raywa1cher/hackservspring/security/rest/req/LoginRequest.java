package com.a6raywa1cher.hackservspring.security.rest.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 3, max = 128)
	private String password;
}
