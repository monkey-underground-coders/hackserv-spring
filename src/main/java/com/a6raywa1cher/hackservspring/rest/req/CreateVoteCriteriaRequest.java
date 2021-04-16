package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.utils.jackson.HtmlEscape;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
public class CreateVoteCriteriaRequest {
	@NotBlank
	@HtmlEscape
	private String name;

	@PositiveOrZero
	private int maxValue;

	@PositiveOrZero
	private long trackId;
}
