package com.a6raywa1cher.hackservspring.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class MemberTeam {
	private Long id;

	private String name;

	private TeammateUser captain;

	private List<TeammateUser> members;

	private List<TeammateUser> requests;

	private ReferenceTrack track;

	private ZonedDateTime createdAt;
}
