package com.a6raywa1cher.hackservspring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@Data
public class EmailValidationToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private int token;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime createdAt;

}
