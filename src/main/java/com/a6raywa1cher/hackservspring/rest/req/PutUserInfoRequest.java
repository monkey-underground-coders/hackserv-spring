package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.utils.jackson.HtmlEscape;
import lombok.Data;

import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class PutUserInfoRequest {
	@HtmlEscape
	private String firstName;

	@HtmlEscape
	private String middleName;

	@HtmlEscape
	private String lastName;

	@Pattern(regexp = "^@(?=\\w{5,64}\\b)[a-zA-Z0-9]+(?:_[a-zA-Z0-9]+)*$")
	@Size(max = 250)
	private String telegram;

	@Past
	private LocalDate dateOfBirth;

	@HtmlEscape
	private String workPlace;

	@Size(max = 5000)
	@HtmlEscape
	private String resume;

	@Size(max = 5000)
	@HtmlEscape
	private String otherInfo;
}
