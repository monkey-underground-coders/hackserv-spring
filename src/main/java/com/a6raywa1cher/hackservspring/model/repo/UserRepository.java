package com.a6raywa1cher.hackservspring.model.repo;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findFirstByUserRole(UserRole userRole);

    Optional<User> findByGoogleIdOrEmail(String googleId, String email);

    Optional<User> findByVkIdOrEmail(String vkId, String email);

    Optional<User> findByGithubIdOrEmail(String githubId, String email);

    List<User> findTop1ByUserRole(UserRole userRole, Pageable pageable);
}
