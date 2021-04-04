package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.VoteCriteria;
import com.a6raywa1cher.hackservspring.model.repo.TrackRepository;
import com.a6raywa1cher.hackservspring.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class TrackServiceImpl implements TrackService {
    private final TrackRepository repository;

    @Autowired
    public TrackServiceImpl(TrackRepository trackRepository){
        this.repository = trackRepository;
    }

    public Track create(String trackName){
        Track track = new Track();
        track.setTrackName(trackName);
        return repository.save(track);
    }

    @Override
    public Optional<Track> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Stream<Track> getById(Collection<Long> ids) {
        return StreamSupport.stream(repository.findAllById(ids).spliterator(), false);
    }

    @Override
    public Stream<Track> getAllTracks() {
        return StreamSupport.stream(repository.findAll().spliterator(), false);
    }

    @Override
    public Track editTrack(Track track, String trackName, List<VoteCriteria> criteriaList, List<Team> teams) {
        track.setTrackName(trackName);
        track.setCriteriaList(criteriaList);
        track.setTeams(teams);
        return repository.save(track);
    }

    @Override
    public void delete(Track track) {
        track.getTeams().forEach(team -> team.setTrack(null));
        repository.delete(track);
    }
}
