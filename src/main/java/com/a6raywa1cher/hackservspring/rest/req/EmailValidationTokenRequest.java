package com.a6raywa1cher.hackservspring.rest.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailValidationTokenRequest {
	@Positive
	@Digits(integer = 6, fraction = 0)
	private int token;
}
