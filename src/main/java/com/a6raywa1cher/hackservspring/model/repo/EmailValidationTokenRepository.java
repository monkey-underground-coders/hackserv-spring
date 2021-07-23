package com.a6raywa1cher.hackservspring.model.repo;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailValidationTokenRepository extends PagingAndSortingRepository<EmailValidationToken, UUID> {

}
