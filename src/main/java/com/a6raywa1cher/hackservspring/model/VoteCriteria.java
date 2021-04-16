package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
@ToString(exclude = {"voteList"})
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
	private List<Vote> voteList;
}
