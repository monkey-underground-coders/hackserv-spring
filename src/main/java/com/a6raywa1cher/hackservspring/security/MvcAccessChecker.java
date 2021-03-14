package com.a6raywa1cher.hackservspring.security;

import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.service.TeamService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.utils.AuthenticationResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
@Slf4j
@Transactional
public class MvcAccessChecker {
    private final AuthenticationResolver resolver;
    private final UserService userService;
    private final TeamService teamService;

    public MvcAccessChecker(AuthenticationResolver resolver, UserService userService, TeamService teamService) {
        this.resolver = resolver;
        this.userService = userService;
        this.teamService = teamService;
    }

    public boolean checkUserInternalInfoAccess(Long id, User requester) {
        if (requester.getId().equals(id)) {
            return true;
        }
        return requester.getUserRole() == UserRole.ADMIN;
    }

    public boolean checkUserInternalInfoAccess(Long id) {
        return this.checkUserInternalInfoAccess(id, getCurrentUser());
    }

    public boolean checkTeamCaptainWithCurrentUser(Long teamId, User requester) {
        Optional<Team> optionalTeam = teamService.getById(teamId);
        if (optionalTeam.isEmpty()) {
            return true; // 404 error will be thrown by the controller
        }
        if (requester.getId().equals(optionalTeam.get().getCaptain().getId())) {
            return true;
        }
        return requester.getUserRole() == UserRole.ADMIN;
    }

    public boolean checkTeamCaptainWithCurrentUser(Long teamId) {
        return this.checkTeamCaptainWithCurrentUser(teamId, getCurrentUser());
    }

    // ----------------------------------------- checkUserPasswordChangeAccess -----------------------------------------

    public boolean checkUserPasswordChangeAccess(Long id, User requester) {
        Optional<User> optionalUser = userService.getById(id);
        if (optionalUser.isEmpty()) {
            return true; // 404 error will be thrown by the controller
        }
        if (requester.getId().equals(id)) {
            return true;
		}
		return requester.getUserRole() == UserRole.ADMIN;
	}

	public boolean checkUserPasswordChangeAccess(Long id) {
		return this.checkUserPasswordChangeAccess(id, getCurrentUser());
	}

	private User getCurrentUser() {
		return resolver.getUser();
	}
}
