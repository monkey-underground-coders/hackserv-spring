package com.a6raywa1cher.hackservspring.model.repo;

import com.a6raywa1cher.hackservspring.model.Track;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends PagingAndSortingRepository<Track, Long> {
}
