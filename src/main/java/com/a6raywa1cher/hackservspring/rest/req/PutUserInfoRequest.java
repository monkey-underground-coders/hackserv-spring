package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class PutUserInfoRequest {
	@Size(max = 250)
	private String firstName;

	@Size(max = 250)
	private String middleName;

	@Size(max = 250)
	private String lastName;

	@Pattern(regexp = "^@(?=\\w{5,64}\\b)[a-zA-Z0-9]+(?:_[a-zA-Z0-9]+)*$")
	@Size(max = 250)
	private String telegram;

	@Past
	private LocalDate dateOfBirth;

	@Size(max = 250)
	private String workPlace;

	@Size(max = 5000)
	private String resume;

	@Size(max = 5000)
	private String otherInfo;
}
