package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.a6raywa1cher.hackservspring.utils.jackson.JsonViewOrId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
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
public class Track {
	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Long id;

	@Column(nullable = false, unique = true)
	@JsonView(Views.Public.class)
	private String trackName;

	@OneToMany(orphanRemoval = true, mappedBy = "track")
	@JsonView(Views.Public.class)
	@ToString.Exclude
	private List<VoteCriteria> criteriaList;

	@OneToMany(mappedBy = "track")
	@JsonViewOrId(Views.Detailed.class)
	@ToString.Exclude
	private List<Team> teams;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Track track = (Track) o;
		return Objects.equals(id, track.id);
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
