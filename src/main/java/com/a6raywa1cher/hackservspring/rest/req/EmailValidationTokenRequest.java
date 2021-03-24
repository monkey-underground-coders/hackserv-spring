package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Positive;

@Data
public class EmailValidationTokenRequest {

    @Positive
    @Digits(integer = 6, fraction = 0)
    private int token;
}
