package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
public class CreateVoteCriteriaRequest {
	@NotBlank
	@Size(max = 250)
	private String name;

	@PositiveOrZero
	private int maxValue;

	@PositiveOrZero
	private long trackId;
}
