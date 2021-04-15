package com.a6raywa1cher.hackservspring.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VoteCriteriaInfo {
	private String name;

	private int maxValue;

	private String description;
}
