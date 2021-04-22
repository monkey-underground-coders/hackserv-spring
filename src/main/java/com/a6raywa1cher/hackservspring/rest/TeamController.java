package com.a6raywa1cher.hackservspring.rest;


import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.rest.exc.*;
import com.a6raywa1cher.hackservspring.rest.req.CreateTeamRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutTeamInfoRequest;
import com.a6raywa1cher.hackservspring.rest.req.UserIdRequest;
import com.a6raywa1cher.hackservspring.service.TeamService;
import com.a6raywa1cher.hackservspring.service.TrackService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.service.dto.TeamInfo;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping("/team")
@Transactional(rollbackOn = Exception.class)
public class TeamController {

	private final TeamService teamService;
	private final UserService userService;
	private final TrackService trackService;

	public TeamController(TeamService teamService, UserService userService, TrackService trackService) {
		this.teamService = teamService;
		this.userService = userService;
		this.trackService = trackService;
	}

	@PostMapping("/create")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#request.captainId)")
	@JsonView(Views.Internal.class)
	public ResponseEntity<Team> createTeam(@RequestBody @Valid CreateTeamRequest request) throws UserNotExistsException, UserAlreadyInTeam {
		Optional<User> optionalCaptain = userService.getById(request.getCaptainId());
		if (optionalCaptain.isEmpty()) {
			throw new UserNotExistsException();
		}
		User captain = optionalCaptain.get();
		if (captain.getTeam() != null) {
			throw new UserAlreadyInTeam();
		}
		if (teamService.getTeamRequestForUser(captain).isPresent()) {
			throw new UserAlreadyMadeRequest();
		}
		Team team = teamService.createTeam(request.getName(), captain);

		return ResponseEntity.ok(team);
	}

	@GetMapping("/{teamid:[0-9]+}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Public.class)
	public ResponseEntity<Team> getTeam(@PathVariable long teamid) throws TeamNotExistsException {
		Optional<Team> optionalTeam = teamService.getById(teamid);
		if (optionalTeam.isEmpty()) {
			throw new TeamNotExistsException();
		}
		Team team = optionalTeam.get();

		return ResponseEntity.ok(team);
	}

	@GetMapping("/")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Public.class)
	@PageableAsQueryParam
	public ResponseEntity<Page<Team>> getPage(@RequestParam(required = false) String with, @Parameter(hidden = true) Pageable pageable) {
		return ResponseEntity.ok(teamService.getPage(with, pageable));
	}

	@PutMapping("/{teamid:[0-9]+}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamid)")
	@JsonView(Views.Internal.class)
	public ResponseEntity<Team> editTeamInfo(@RequestBody @Valid PutTeamInfoRequest request, @PathVariable long teamid) throws TeamNotExistsException {
		Optional<Team> optionalTeam = teamService.getById(teamid);
		if (optionalTeam.isEmpty()) {
			throw new TeamNotExistsException();
		}
		Optional<Track> optionalTrack = trackService.getById(request.getTrackId());
		if (optionalTrack.isEmpty()) {
			throw new TrackNotExistsException();
		}

		TeamInfo teamInfo = new TeamInfo();
		BeanUtils.copyProperties(request, teamInfo);
		teamInfo.setTrack(optionalTrack.get());

		Team team = teamService.editTeam(optionalTeam.get(), teamInfo);

		return ResponseEntity.ok(team);
	}


	@DeleteMapping("/{teamid:[0-9]+}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamid)")

	public ResponseEntity<Void> deleteTeam(@PathVariable long teamid) throws TeamNotExistsException {
		Optional<Team> optionalTeam = teamService.getById(teamid);
		if (optionalTeam.isEmpty()) {
			throw new TeamNotExistsException();
		}
		Team team = optionalTeam.get();
		teamService.deleteTeam(team);

		return ResponseEntity.ok().build();
	}


	@PostMapping("/{teamid:[0-9]+}/req")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#request.userId)")
	@JsonView(Views.Public.class)
	public ResponseEntity<Team> requestInTeam(@RequestBody @Valid UserIdRequest request, @PathVariable long teamid) throws UserNotExistsException, TeamNotExistsException, UserAlreadyInTeam, UserAlreadyMadeRequest {
		Optional<User> optionalUser = userService.getById(request.getUserId());
		if (optionalUser.isEmpty()) {
			throw new UserNotExistsException();
		}
		User user = optionalUser.get();
		if (user.getTeam() != null) {
			throw new UserAlreadyInTeam();
		}
		if (teamService.getTeamRequestForUser(user).isPresent()) {
			throw new UserAlreadyMadeRequest();
		}
		Optional<Team> optionalTeam = teamService.getById(teamid);
		if (optionalTeam.isEmpty()) {
			throw new TeamNotExistsException();
		}

		Team team = teamService.requestInTeam(optionalTeam.get(), user);

		return ResponseEntity.ok(team);
	}


	@PostMapping("/{teamid:[0-9]+}/accept")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Internal.class)
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamid)")
	public ResponseEntity<Team> acceptUser(@RequestBody @Valid UserIdRequest request, @PathVariable long teamid) {
		Optional<User> optionalUser = userService.getById(request.getUserId());
		if (optionalUser.isEmpty()) {
			throw new UserNotExistsException();
		}
		User user = optionalUser.get();
		Optional<Team> optionalTeam = teamService.getById(teamid);
		if (optionalTeam.isEmpty()) {
			throw new TeamNotExistsException();
		}
		Team team = optionalTeam.get();
		if (!teamService.isMembersLessThenMax(team)) {
			throw new MaxMembersInTeamException();
		}
		if (!teamService.isUserInRequestList(team, user)) {
			throw new UserNotInRequestListException();
		}

		Team advancedTeam = teamService.acceptInTeam(team, user);

		return ResponseEntity.ok(advancedTeam);
	}


	@PostMapping("/{teamid:[0-9]+}/change_captain")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Internal.class)
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamid)")
	public ResponseEntity<Team> changeCaptain(@RequestBody @Valid UserIdRequest request, @PathVariable long teamid) {
		Optional<User> optionalUser = userService.getById(request.getUserId());
		if (optionalUser.isEmpty()) {
			throw new UserNotExistsException();
		}
		User user = optionalUser.get();
		Optional<Team> optionalTeam = teamService.getById(teamid);
		if (optionalTeam.isEmpty()) {
			throw new TeamNotExistsException();
		}

		if (!teamService.isUserInTeam(optionalTeam.get(), user)) {
			throw new UserNotInTeamException();
		}

		Team team = teamService.changeCaptain(optionalTeam.get(), user);
		return ResponseEntity.ok(team);
	}

	@DeleteMapping("/{teamid:[0-9]+}/del_member")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@PreAuthorize("@mvcAccessChecker.checkMemberOfTeamOrRequested(#teamid)")
	public ResponseEntity<Team> deleteMember(@RequestBody @Valid UserIdRequest request, @PathVariable long teamid, @Parameter(hidden = true) User requester) {
		Optional<User> optionalUser = userService.getById(request.getUserId());
		if (optionalUser.isEmpty()) {
			throw new UserNotExistsException();
		}
		User user = optionalUser.get();
		Optional<Team> optionalTeam = teamService.getById(teamid);
		if (optionalTeam.isEmpty()) {
			throw new TeamNotExistsException();
		}
		Team team = optionalTeam.get();

		if (!requester.getId().equals(user.getId()) && !teamService.isUserCaptain(team, requester) && !requester.getUserRole().equals(UserRole.ADMIN)) {
			throw new NotCaptainTryingDeleteAnotherUserException();
		}

		if (teamService.isUserInTeam(team, user)) {
			teamService.deleteMember(team, user);
			optionalTeam = teamService.getById(teamid);
			if (optionalTeam.isEmpty()) {
				return ResponseEntity.ok().build();
			}
		} else if (teamService.isUserInRequestList(team, user)) {
			team = teamService.deleteRequest(team, user);
		} else {
			throw new UserNotInTeamOrRequestsListException();
		}

		return ResponseEntity.ok(team);
	}

}
