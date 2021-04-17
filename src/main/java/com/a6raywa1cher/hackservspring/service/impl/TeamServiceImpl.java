package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.repo.TeamRepository;
import com.a6raywa1cher.hackservspring.model.repo.UserRepository;
import com.a6raywa1cher.hackservspring.service.TeamService;
import com.a6raywa1cher.hackservspring.service.dto.TeamInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;


    public TeamServiceImpl(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
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

        captain.setTeam(team);
        userRepository.save(captain);
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
        team.setTrack(teamInfo.getTrack());
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

        user.setTeam(team);
        userRepository.save(user);

        return teamRepository.save(team);
    }

    @Override
    public Team changeCaptain(Team team, User user) {
        team.setCaptain(user);
        return teamRepository.save(team);
    }

    @Override
    public Page<Team> getPage(String filter, Pageable pageable) {
        if (filter == null) filter = "";
        return teamRepository.findAllByNameContainsIgnoreCase(filter, pageable);
    }

    @Override
    public boolean isUserInRequestList(Team team, User user) {
        for (User requests : team.getRequests()) {
            if (requests.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUserCaptain(Team team, User user) {
        return team.getCaptain().getId().equals(user.getId());
    }

    @Override
    public boolean isUserInTeam(Team team, User user) {
        return team.getId().equals(user.getTeam().getId());
    }

    @Override
    public void deleteRequest(Team team, User user) {
        List<User> advancedRequests = team.getRequests();
        advancedRequests.remove(user);
        team.setRequests(advancedRequests);
        teamRepository.save(team);
    }

    @Override
    public void deleteMember(Team team, User user) {
        user.setTeam(null);
        userRepository.save(user);
        List<User> advancedMembers = team.getMembers();
        advancedMembers.remove(user);
        team.setMembers(advancedMembers);
        if (team.getMembers().size() == 0) {
            this.deleteTeam(team);
            return;
        }
        if (this.isUserCaptain(team, user)) {
            changeCaptain(team, team.getMembers().get(0));
        }
        teamRepository.save(team);
    }

    @Override
    public void deleteTeam(Team team) {
        for (User user : team.getMembers()) {
            user.setTeam(null);
            userRepository.save(user);
        }
        teamRepository.delete(team);
    }


}
