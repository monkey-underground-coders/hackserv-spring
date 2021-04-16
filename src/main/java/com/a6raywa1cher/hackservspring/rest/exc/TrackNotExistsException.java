package com.a6raywa1cher.hackservspring.rest.exc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Track not exists")
public class TrackNotExistsException extends RuntimeException {
}
