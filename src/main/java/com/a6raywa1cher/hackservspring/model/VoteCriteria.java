package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@JsonIdentityInfo(
	generator = ObjectIdGenerators.PropertyGenerator.class,
	property = "id")
public class VoteCriteria {
	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Long id;

	@Column
	@JsonView(Views.Public.class)
	private String name;

	@Column(length = 5000)
	@JsonView(Views.Public.class)
	private String description;

	@Column(nullable = false)
	@JsonView(Views.Public.class)
	private int maxValue;

	@ManyToOne(optional = false)
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private Track track;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.REMOVE, mappedBy = "criteria")
	@JsonView(Views.Public.class)
	@JsonIgnore
	@ToString.Exclude
	private List<Vote> voteList;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		VoteCriteria that = (VoteCriteria) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
