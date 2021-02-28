package com.a6raywa1cher.hackservspring.service.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfo {
    private String fullName;

    private String telegram;

    private LocalDate dateOfBirth;

    private String workPlace;

    private String otherInfo;

    private String resume;
}
