package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class CreateTeamRequest {

    @NotBlank
    private String name;

    @NotNull
    private long captainId;

}
