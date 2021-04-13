package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
@ToString(exclude = {"criteriaList", "teams"})
public class Track {
	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Long id;

	@Column
	@JsonView(Views.Public.class)
	private String trackName;

	@OneToMany(orphanRemoval = true, mappedBy = "track")
	@JsonView(Views.Public.class)
	private List<VoteCriteria> criteriaList;

	@OneToMany(mappedBy = "track")
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private List<Team> teams;
}
