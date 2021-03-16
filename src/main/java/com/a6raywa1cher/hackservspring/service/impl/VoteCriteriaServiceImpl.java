package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.Vote;
import com.a6raywa1cher.hackservspring.model.VoteCriteria;
import com.a6raywa1cher.hackservspring.model.repo.VoteCriteriaRepository;
import com.a6raywa1cher.hackservspring.service.VoteCriteriaService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class VoteCriteriaServiceImpl implements VoteCriteriaService {
    private VoteCriteriaRepository repository;

    @Override
    public VoteCriteria create(String criteriaName, int maxValue) {
        VoteCriteria criteria = new VoteCriteria();
        criteria.setName(criteriaName);
        criteria.setMaxValue(maxValue);
        return repository.save(criteria);
    }

    @Override
    public Optional<VoteCriteria> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Stream<VoteCriteria> getById(Collection<Long> ids) {
        return StreamSupport.stream(repository.findAllById(ids).spliterator(), false);
    }

    @Override
    public VoteCriteria editCriteria(VoteCriteria criteria, String criteriaName, String description, int maxValue, Track track, List<Vote> voteList) {
        criteria.setName(criteriaName);
        criteria.setDescription(description);
        criteria.setMaxValue(maxValue);
        criteria.setVoteList(voteList);
        criteria.setTrack(track);
        return repository.save(criteria);
    }

    @Override
    public void deleteCriteria(VoteCriteria criteria) {
        repository.delete(criteria);
    }
}
