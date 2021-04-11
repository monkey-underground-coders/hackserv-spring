package com.a6raywa1cher.hackservspring.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VoteCriteriaInfo {
	@NotBlank
	private String name;

	@NotBlank
	private int maxValue;

	private String description;


}
