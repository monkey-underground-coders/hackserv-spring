package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.User;

import java.util.Optional;

public interface TeamService {
    Team createTeam(String name, User captain);

    Team createTeam(String name, User captain, Track track);

    Optional<Team> getById(long id);
}
