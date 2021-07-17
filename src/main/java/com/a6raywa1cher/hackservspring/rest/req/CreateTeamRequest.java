package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
public class CreateTeamRequest {
	@NotBlank
	@Size(max = 250)
	private String name;

    @NotNull
    private long captainId;
}
