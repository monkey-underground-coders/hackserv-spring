package com.a6raywa1cher.hackservspring.security.rest.exc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SameUserTokensProvidedException extends RuntimeException {
}
