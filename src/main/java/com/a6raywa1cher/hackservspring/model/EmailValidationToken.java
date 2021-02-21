package com.a6raywa1cher.hackservspring.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class EmailValidationToken {

    private int token;
    @Id
    private Long id;

}
