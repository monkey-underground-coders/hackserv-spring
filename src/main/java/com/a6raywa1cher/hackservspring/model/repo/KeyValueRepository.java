package com.a6raywa1cher.hackservspring.model.repo;

import com.a6raywa1cher.hackservspring.model.KeyValue;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyValueRepository extends PagingAndSortingRepository<KeyValue, String> {
}
