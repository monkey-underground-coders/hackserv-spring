package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.utils.jackson.HtmlEscape;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateTrackRequest {

    @NotBlank
    @HtmlEscape
    private String trackName;
}
