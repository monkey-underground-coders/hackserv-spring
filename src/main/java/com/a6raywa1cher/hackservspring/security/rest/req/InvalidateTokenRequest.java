package com.a6raywa1cher.hackservspring.security.rest.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class InvalidateTokenRequest {
	@NotNull
	@Size(min = 36, max = 36)
	private String refreshToken;
}
