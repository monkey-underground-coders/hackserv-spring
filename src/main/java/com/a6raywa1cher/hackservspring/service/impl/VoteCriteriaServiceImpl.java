package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.VoteCriteria;
import com.a6raywa1cher.hackservspring.model.repo.VoteCriteriaRepository;
import com.a6raywa1cher.hackservspring.service.VoteCriteriaService;
import com.a6raywa1cher.hackservspring.service.dto.VoteCriteriaInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class VoteCriteriaServiceImpl implements VoteCriteriaService {
    private final VoteCriteriaRepository repository;

    public VoteCriteriaServiceImpl(VoteCriteriaRepository repository) {
        this.repository = repository;
    }

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
    public Stream<VoteCriteria> getAllCriteria() {
        return StreamSupport.stream(repository.findAll().spliterator(), false);
    }

    @Override
    public VoteCriteria editCriteria(VoteCriteria criteria, String criteriaName, int maxValue) {
        criteria.setName(criteriaName);
        criteria.setMaxValue(maxValue);
        return repository.save(criteria);
    }

    @Override
    public VoteCriteria editCriteriaInfo(VoteCriteria criteria, VoteCriteriaInfo criteriaInfo){
        criteria.setTrack(criteriaInfo.getTrack());
        criteria.setVoteList(criteriaInfo.getVoteList());
        criteria.setDescription(criteriaInfo.getDescription());
        criteria.setVoteList(criteriaInfo.getVoteList());
        criteria.setName(criteriaInfo.getName());
        return repository.save(criteria);
    }

    @Override
    public void deleteCriteria(VoteCriteria criteria) {
        repository.delete(criteria);
    }
}
