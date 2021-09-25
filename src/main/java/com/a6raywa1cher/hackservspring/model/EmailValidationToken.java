package com.a6raywa1cher.hackservspring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class EmailValidationToken {

	@Id
	private UUID id;

	@Column(nullable = false)
	private int token;
	@Column
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private ZonedDateTime createdAt = ZonedDateTime.now();

	protected EmailValidationToken() {

	}

	public EmailValidationToken(int token) {
		this.id = UUID.randomUUID();
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		EmailValidationToken that = (EmailValidationToken) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
