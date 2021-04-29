package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.model.HackState;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PutHackStateRequest {
	@NotNull
	private HackState hackState;
}
