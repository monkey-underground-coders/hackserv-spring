package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateTrackRequest {

    @NotBlank
    private String trackName;
}
