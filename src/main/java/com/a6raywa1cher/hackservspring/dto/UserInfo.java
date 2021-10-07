package com.a6raywa1cher.hackservspring.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfo {

	private String firstName;

	private String middleName;

	private String lastName;

	private String telegram;

	private LocalDate dateOfBirth;

	private String workPlace;

	private String otherInfo;

	private String resume;
}
