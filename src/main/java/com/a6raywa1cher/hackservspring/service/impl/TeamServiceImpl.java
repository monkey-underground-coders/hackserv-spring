package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.repo.TeamRepository;
import com.a6raywa1cher.hackservspring.service.TeamService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository repository;

    public TeamServiceImpl(TeamRepository repository) {
        this.repository = repository;
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
        team.setCreatedAt(ZonedDateTime.now());

        return repository.save(team);
    }

    @Override
    public Optional<Team> getById(long id) {
        return repository.findById(id);
    }

}
