package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.repo.TeamRepository;
import com.a6raywa1cher.hackservspring.service.TeamService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.service.dto.TeamInfo;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final UserService userService;

    public TeamServiceImpl(TeamRepository teamRepository, UserService userService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    @Override
    public Team createTeam(String name, User captain) {
        return createTeam(name, captain, null);
    }

    @Override
    public Team createTeam(String name, User captain, Track track) {
        Team team = new Team();
        team.setName(name);
        team.setCaptain(captain);
        team.setTrack(track);
        List<User> members = new ArrayList<>();
        members.add(captain);
        team.setMembers(members);

        List<User> requests = new LinkedList<>();
        team.setRequests(requests);
        team.setCreatedAt(ZonedDateTime.now());

        userService.editTeam(captain, team);

        return teamRepository.save(team);
    }

    @Override
    public Optional<Team> getById(long id) {
        return teamRepository.findById(id);
    }

    @Override
    public Optional<Team> getTeamRequestForUser(User user) {
        return teamRepository.findTeamRequestForUser(user);
    }

    @Override
    public Team editTeam(Team team, TeamInfo teamInfo) {
        team.setName(teamInfo.getName());
        return teamRepository.save(team);
    }

    @Override
    public Team requestInTeam(Team team, User user) {
        List<User> requests = team.getRequests();
        requests.add(user);
        team.setRequests(requests);
        return teamRepository.save(team);
    }

    @Override
    public Team acceptInTeam(Team team, User user) {
        List<User> advancedRequests = team.getRequests();
        advancedRequests.remove(user);
        team.setRequests(advancedRequests);

        List<User> advancedMembers = team.getMembers();
        advancedMembers.add(user);
        team.setMembers(advancedMembers);

        return teamRepository.save(team);
    }

    @Override
    public Boolean isUserInRequestList(Team team, User user) {
        for (User requests : team.getRequests()) {
            if (requests.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteTeam(Team team) {
        for (User user : team.getMembers()) {
            userService.editTeam(user, null);
        }
        teamRepository.delete(team);
    }


}
