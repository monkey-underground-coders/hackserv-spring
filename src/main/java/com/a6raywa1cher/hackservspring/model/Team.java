package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@JsonIdentityInfo(
	generator = ObjectIdGenerators.PropertyGenerator.class,
	property = "id")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Team {
	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Long id;

	@Column(nullable = false, unique = true)
	@JsonView(Views.Public.class)
	private String name;

	@OneToMany(mappedBy = "team")
	@JsonIdentityReference(alwaysAsId = true)
	@JsonView(Views.Internal.class)
	@ToString.Exclude
	private List<User> members;

	@OneToMany(mappedBy = "request")
	@JsonIdentityReference(alwaysAsId = true)
	@JsonView(Views.Internal.class)
	@ToString.Exclude
	private List<User> requests;

	@OneToOne(optional = false)
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private User captain;

	@ManyToOne
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private Track track;

	@Column
	@JsonView(Views.Public.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private ZonedDateTime createdAt;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Team team = (Team) o;
		return Objects.equals(id, team.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
