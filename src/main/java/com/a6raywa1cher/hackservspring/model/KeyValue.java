package com.a6raywa1cher.hackservspring.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class KeyValue {

	@Id
	private String key;

	@Column(nullable = false)
	private String value;
}
