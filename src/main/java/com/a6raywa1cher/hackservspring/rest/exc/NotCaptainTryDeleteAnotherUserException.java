package com.a6raywa1cher.hackservspring.rest.exc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Not captain try delete another user")
public class NotCaptainTryDeleteAnotherUserException extends RuntimeException {
}
