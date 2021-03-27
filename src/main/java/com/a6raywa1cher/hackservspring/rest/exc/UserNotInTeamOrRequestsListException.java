package com.a6raywa1cher.hackservspring.rest.exc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "User not in team or requests list")
public class UserNotInTeamOrRequestsListException extends RuntimeException {
}
