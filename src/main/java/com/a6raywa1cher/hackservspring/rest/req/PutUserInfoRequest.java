package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.utils.jackson.HtmlEscape;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class PutUserInfoRequest {
	@NotBlank
	@HtmlEscape
	private String firstName;

	@HtmlEscape
	private String middleName;

	@NotBlank
	@HtmlEscape
	private String lastName;

	@NotBlank
	@Pattern(regexp = "^@(?=\\w{5,64}\\b)[a-zA-Z0-9]+(?:_[a-zA-Z0-9]+)*$")
	@Size(max = 250)
	private String telegram;

	@NotNull
	@Past
	private LocalDate dateOfBirth;

	@NotBlank
	@HtmlEscape
	private String workPlace;

	@Size(max = 5000)
	@HtmlEscape
	private String resume;

	@Size(max = 5000)
	@HtmlEscape
	private String otherInfo;
}
