package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Long id;

	@Column(unique = true, length = 1024)
	@JsonView(Views.Internal.class)
	private String googleId;

	@Column(unique = true, length = 1024)
	@JsonView(Views.Internal.class)
	private String vkId;

	@Column(unique = true, nullable = false, length = 1024)
	@JsonView(Views.Internal.class)
	private String email;

	@Column
	@JsonIgnore
	private String password;

	@Column
	@JsonView(Views.Public.class)
	private UserRole userRole;


	@Column
	@JsonView(Views.Public.class)
	private String fullName;


	@Column
	@JsonView(Views.Internal.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private ZonedDateTime expiringAt;

	@Column
	@JsonView(Views.Public.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private ZonedDateTime createdAt;

	@Column
	@JsonView(Views.Internal.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private ZonedDateTime lastVisitAt;

	public User() {

	}

	@Transient
	@JsonView(Views.Public.class)
	public boolean isEnabled() {
		return expiringAt == null || ZonedDateTime.now().isBefore(expiringAt);
	}
}
