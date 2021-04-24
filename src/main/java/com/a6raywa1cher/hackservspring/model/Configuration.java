package com.a6raywa1cher.hackservspring.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Configuration {

	@Id
	private String key;

	@Column
	private String value;
}
