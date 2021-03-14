package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestInTeamRequest {

    @NotNull
    private long userId;

}
