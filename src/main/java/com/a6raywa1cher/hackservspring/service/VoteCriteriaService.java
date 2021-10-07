package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.dto.VoteCriteriaInfo;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.VoteCriteria;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface VoteCriteriaService {
	VoteCriteria create(String criteriaName, int maxValue, Track track);

	Optional<VoteCriteria> getById(Long id);

	Stream<VoteCriteria> getById(Collection<Long> ids);

	VoteCriteria editCriteriaInfo(VoteCriteria criteria, VoteCriteriaInfo info);

	void deleteCriteria(VoteCriteria criteria);
}
