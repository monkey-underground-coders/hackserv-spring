package com.a6raywa1cher.hackservspring.model;

import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
public class User {
	@Id
	@GeneratedValue
	@JsonView(Views.Public.class)
	private Long id;

	@Column(name = "google_id", unique = true)
	@JsonView(Views.Internal.class)
	private String googleId;

	@Column(name = "vk_id", unique = true)
	@JsonView(Views.Internal.class)
	private String vkId;

	@Column(name = "github_id", unique = true)
	@JsonView(Views.Internal.class)
	private String githubId;

	@Column(name = "email", nullable = false, length = 1024)
	@JsonView(Views.Internal.class)
	private String email;

	@Column(length = 1024)
	@JsonIgnore
	private String password;

	@Column
	@JsonView(Views.Public.class)
	private UserRole userRole;

	@Column
	@JsonView(Views.Internal.class)
	private String fullName;

	@ManyToOne
	@JsonView(Views.Public.class)
	private Team team;

	@OneToMany
	@JsonIgnore
	private List<Vote> votings;

	@Column
	@JsonView(Views.Internal.class)
	private String telegram;

	@Column
	@JsonView(Views.Internal.class)
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

	@Transient
	@JsonView(Views.Public.class)
	public boolean isEnabled() {
		return true;
	}
}
