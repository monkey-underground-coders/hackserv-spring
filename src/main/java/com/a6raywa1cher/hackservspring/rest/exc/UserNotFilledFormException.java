package com.a6raywa1cher.hackservspring.rest.exc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "User has no state Filled form")
public class UserNotFilledFormException extends RuntimeException {
}
