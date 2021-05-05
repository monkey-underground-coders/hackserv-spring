package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.model.UserState;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserStateRequest {
	@NotNull
	private UserState userState;
}
