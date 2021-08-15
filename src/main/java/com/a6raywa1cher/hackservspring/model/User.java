package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"google_id", "email"}),
		@UniqueConstraint(columnNames = {"vk_id", "email"}),
		@UniqueConstraint(columnNames = {"github_id", "email"}),
})
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
@ToString(exclude = {"votings"})
public class User {
	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Long id;

	@Column(length = 5000)
	@JsonView(Views.DetailedInternal.class)
	private String resume;

	@Column
	@JsonView(Views.Internal.class)
	private String documentResumePath;

	@Column(name = "google_id", unique = true)
	@JsonView(Views.Internal.class)
	private String googleId;

	@Column(name = "vk_id", unique = true)
	@JsonView(Views.Internal.class)
	private String vkId;

	@Column(name = "github_id", unique = true)
	@JsonView(Views.Internal.class)
	private String githubId;

	@Column(name = "email", nullable = false, length = 1024, unique = true)
	@JsonView(Views.Internal.class)
	private String email;

	@Column(length = 1024)
	@JsonIgnore
	private String password;

	@Column
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Public.class)
	private UserRole userRole;

	@Column
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Public.class)
	private UserState userState;

	@OneToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	private EmailValidationToken emailValidationToken;

	@Column
	@JsonView(Views.Internal.class)
	private boolean emailValidated;

	@Column
	@JsonView(Views.Internal.class)
	private String firstName;

	@Column
	@JsonView(Views.Internal.class)
	private String middleName;

	@Column
	@JsonView(Views.Internal.class)
	private String lastName;

	@ManyToOne
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private Team team;

	@ManyToOne
	@JsonView(Views.Public.class)
	@JsonIdentityReference(alwaysAsId = true)
	private Team request;

	@OneToMany(mappedBy = "judge")
	@JsonIgnore
	private List<Vote> votings;

	@Column
	@JsonView(Views.Internal.class)
	private String telegram;

	@Column
	@JsonView(Views.Internal.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDate dateOfBirth;

	@Column
	@JsonView(Views.Internal.class)
	private String workPlace;

	@Column(length = 5000)
	@JsonView(Views.Internal.class)
	private String otherInfo;

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

	@JsonView(Views.Internal.class)
	public String getFullName() {
		List<String> segments = new ArrayList<>(3);
		if (lastName != null) segments.add(lastName);
		if (firstName != null) segments.add(firstName);
		if (middleName != null && !middleName.equals("")) segments.add(middleName);
		if (segments.size() == 0) {
			return "";
		}
		return String.join(" ", segments);
	}
}
