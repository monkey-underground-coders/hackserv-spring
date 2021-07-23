package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class EmailValidationTokenIdRequest {
	@NotNull
	private UUID id;
}
