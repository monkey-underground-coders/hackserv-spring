package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class PutUserInfoRequest {

    @NotBlank
    @Size(max = 250)
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^@.*$")
    @Size(max = 250)
    private String telegram;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(max = 250)
    private String workPlace;

    @Size(max = 5000)
    private String otherInfo;
}
