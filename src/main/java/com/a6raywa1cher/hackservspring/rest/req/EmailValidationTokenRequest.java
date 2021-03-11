package com.a6raywa1cher.hackservspring.rest.req;

import lombok.Data;

import javax.validation.constraints.Digits;

@Data
public class EmailValidationTokenRequest {

    @Digits(integer = 6, fraction = 0)
    private int token;
}
