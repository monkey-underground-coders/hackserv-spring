package com.a6raywa1cher.hackservspring.rest.req;

import com.a6raywa1cher.hackservspring.utils.jackson.HtmlEscape;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PutTeamInfoRequest {

    @NotBlank
    @Size(max = 250)
    @HtmlEscape
    private String name;

    @NotNull
    private long trackId;

}
