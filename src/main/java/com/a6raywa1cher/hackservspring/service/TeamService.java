package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.service.dto.TeamInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TeamService {
	Team createTeam(String name, User captain);

	Team createTeam(String name, User captain, Track track);

	Optional<Team> getById(long id);

	Optional<Team> getTeamRequestForUser(User user);

	Team editTeam(Team team, TeamInfo teamInfo);

	Team requestInTeam(Team team, User user);

	Team acceptInTeam(Team team, User user);

	Team changeCaptain(Team team, User user);

	Page<Team> getPage(String filter, Pageable pageable);

	boolean isUserInRequestList(Team team, User user);

	boolean isUserCaptain(Team team, User user);

	boolean isUserInTeam(Team team, User user);

	boolean isMembersLessThenMax(Team team);

	Team deleteRequest(Team team, User user);

	void deleteMember(Team team, User user);

	void deleteTeam(Team team);
}
