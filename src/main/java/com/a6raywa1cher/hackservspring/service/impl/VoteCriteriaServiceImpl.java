package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.dto.VoteCriteriaInfo;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.VoteCriteria;
import com.a6raywa1cher.hackservspring.model.repo.VoteCriteriaRepository;
import com.a6raywa1cher.hackservspring.service.VoteCriteriaService;
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
	public VoteCriteria create(String criteriaName, int maxValue, Track track) {
		VoteCriteria criteria = new VoteCriteria();
		criteria.setName(criteriaName);
		criteria.setMaxValue(maxValue);
		criteria.setTrack(track);
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
	public VoteCriteria editCriteriaInfo(VoteCriteria criteria, VoteCriteriaInfo criteriaInfo) {
		criteria.setDescription(criteriaInfo.getDescription());
		criteria.setMaxValue(criteriaInfo.getMaxValue());
		criteria.setName(criteriaInfo.getName());
		return repository.save(criteria);
	}

	@Override
	public void deleteCriteria(VoteCriteria criteria) {
		repository.delete(criteria);
	}
}
