package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateVoteCriteriaRequest {

    @NotBlank
    private String name;

    @NotNull
    private int maxValue;
}
