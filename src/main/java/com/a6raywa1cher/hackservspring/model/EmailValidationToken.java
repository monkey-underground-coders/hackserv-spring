package com.a6raywa1cher.hackservspring.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class EmailValidationToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private int token;

}
