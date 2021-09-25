package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {"criteria_id", "judge_id", "team_id"})
})
@JsonIdentityInfo(
	generator = ObjectIdGenerators.PropertyGenerator.class,
	property = "id")
public class Vote {
	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Long id;

	@ManyToOne(optional = false)
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private VoteCriteria criteria;

	@ManyToOne(optional = false)
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private User judge;

	@ManyToOne(optional = false)
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private Team team;

	@Column(nullable = false)
	@JsonView(Views.Public.class)
	private int vote;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Vote vote = (Vote) o;
		return Objects.equals(id, vote.id);
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
