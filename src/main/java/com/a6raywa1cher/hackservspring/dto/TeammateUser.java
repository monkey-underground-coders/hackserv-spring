package com.a6raywa1cher.hackservspring.dto;

import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.UserState;
import lombok.Data;

@Data
public class TeammateUser {
	private Long id;

	private UserRole userRole;

	private UserState userState;

	private String firstName;

	private String middleName;

	private String lastName;

	private Long team;

	private Long request;
}
