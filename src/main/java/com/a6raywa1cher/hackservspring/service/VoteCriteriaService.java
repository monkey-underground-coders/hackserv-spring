package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.Vote;
import com.a6raywa1cher.hackservspring.model.VoteCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface VoteCriteriaService {
    VoteCriteria create(String criteriaName, int maxValue);
    Optional<VoteCriteria> getById(Long id);
    Stream<VoteCriteria> getById(Collection<Long> ids);
    VoteCriteria editCriteria(VoteCriteria criteria, String criteriaName, String description, int maxValue, Track track, List<Vote> voteList);
    void deleteCriteria(VoteCriteria criteria);
}
