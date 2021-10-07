package com.a6raywa1cher.hackservspring.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PublicTeam {
	private Long id;

	private String name;

	private ZonedDateTime createdAt;
}
