package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.utils.jackson.HtmlEscape;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PutTeamInfoRequest {

    @NotBlank
    @HtmlEscape
    private String name;

    @NotNull
    private long trackId;

}
