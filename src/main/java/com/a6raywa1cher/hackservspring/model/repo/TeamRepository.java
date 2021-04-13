package com.a6raywa1cher.hackservspring.model.repo;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends PagingAndSortingRepository<Team, Long> {

    @Query("from Team team where ?1 member of team.requests")
    Optional<Team> findTeamRequestForUser(User user);

    Page<Team> findAllByNameContainsIgnoreCase(String filter, Pageable pageable);
}
