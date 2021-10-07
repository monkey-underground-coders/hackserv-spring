package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.dto.MemberTeam;
import com.a6raywa1cher.hackservspring.dto.PublicTeam;
import com.a6raywa1cher.hackservspring.dto.TeamInfo;
import com.a6raywa1cher.hackservspring.dto.mapper.MapStructMapper;
import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.UserState;
import com.a6raywa1cher.hackservspring.rest.exc.*;
import com.a6raywa1cher.hackservspring.rest.req.CreateTeamRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutTeamInfoRequest;
import com.a6raywa1cher.hackservspring.rest.req.UserIdRequest;
import com.a6raywa1cher.hackservspring.service.TeamService;
import com.a6raywa1cher.hackservspring.service.TrackService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	private final MapStructMapper mapStructMapper;

	public TeamController(TeamService teamService, UserService userService, TrackService trackService,
						  MapStructMapper mapStructMapper) {
		this.teamService = teamService;
		this.userService = userService;
		this.trackService = trackService;
		this.mapStructMapper = mapStructMapper;
	}

	@PostMapping("/")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#request.captainId)")
	public MemberTeam createTeam(@RequestBody @Valid CreateTeamRequest request) throws UserNotExistsException, UserAlreadyInTeam {
		User captain = userService.getById(request.getCaptainId()).orElseThrow(UserNotExistsException::new);

		if (captain.getTeam() != null) {
			throw new UserAlreadyInTeam();
		}
		if (teamService.getTeamRequestForUser(captain).isPresent()) {
			throw new UserAlreadyMadeRequest();
		}

		Team team = teamService.createTeam(request.getName(), captain);

		return mapStructMapper.toMemberTeam(team);
	}

	@GetMapping("/{teamId:[0-9]+}")
	public PublicTeam getTeam(@PathVariable long teamId) throws TeamNotExistsException {
		return teamService.getById(teamId)
			.map(mapStructMapper::toPublicTeam)
			.orElseThrow(TeamNotExistsException::new);
	}

	@GetMapping("/{teamId:[0-9]+}/internal")
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamId)")
	public MemberTeam getTeamInternal(@PathVariable long teamId) throws TeamNotExistsException {
		return teamService.getById(teamId)
			.map(mapStructMapper::toMemberTeam)
			.orElseThrow(TeamNotExistsException::new);
	}

	@GetMapping("/")
	@PageableAsQueryParam
	public Page<PublicTeam> getPage(@RequestParam(required = false) String with, @Parameter(hidden = true) Pageable pageable) {
		return teamService.getPage(with, pageable)
			.map(mapStructMapper::toPublicTeam);
	}

	@PutMapping("/{teamId:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamId)")
	public MemberTeam editTeamInfo(@RequestBody @Valid PutTeamInfoRequest request, @PathVariable long teamId) throws TeamNotExistsException {
		Team team = teamService.getById(teamId).orElseThrow(TeamNotExistsException::new);

		TeamInfo teamInfo = mapStructMapper.fromPutTeamInfoRequest(request);

		Team saved = teamService.editTeam(team, teamInfo);

		return mapStructMapper.toMemberTeam(saved);
	}

	@PostMapping("/{teamId:[0-9]+}/submit")
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamId)")
	@JsonView(Views.Internal.class)
	public Team submitTeam(@PathVariable long teamId) {
		Team team = teamService.getById(teamId).orElseThrow(TeamNotExistsException::new);

		if (team.getMembers().stream().noneMatch(u -> u.getUserState().equals(UserState.FILLED_FORM))) {
			throw new UserNotFilledFormException();
		}

		return teamService.submitTeamMembers(team);
	}

	@PostMapping("/{teamId:[0-9]+}/approve")
	@PreAuthorize("@mvcAccessChecker.checkUserIsAdmin()")
	@JsonView(Views.Internal.class)
	public Team approveTeam(@PathVariable long teamId) {
		Team team = teamService.getById(teamId).orElseThrow(TeamNotExistsException::new);

		return teamService.approveTeamMembers(team);
	}

	@DeleteMapping("/{teamId:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamId)")
	public void deleteTeam(@PathVariable long teamId) throws TeamNotExistsException {
		Team team = teamService.getById(teamId).orElseThrow(TeamNotExistsException::new);

		teamService.deleteTeam(team);
	}

	@PostMapping("/{teamId:[0-9]+}/req")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#request.userId)")
	@JsonView(Views.Public.class)
	public Team requestInTeam(@RequestBody @Valid UserIdRequest request, @PathVariable long teamId)
		throws UserNotExistsException, TeamNotExistsException, UserAlreadyInTeam, UserAlreadyMadeRequest {
		User user = userService.getById(request.getUserId()).orElseThrow(UserNotExistsException::new);

		if (user.getTeam() != null) {
			throw new UserAlreadyInTeam();
		}
		if (teamService.getTeamRequestForUser(user).isPresent()) {
			throw new UserAlreadyMadeRequest();
		}

		Team team = teamService.getById(teamId).orElseThrow(TeamNotExistsException::new);

		return teamService.requestInTeam(team, user);
	}

	@PostMapping("/{teamId:[0-9]+}/accept")
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamId)")
	@JsonView(Views.Internal.class)
	public Team acceptUser(@RequestBody @Valid UserIdRequest request, @PathVariable long teamId) {
		User user = userService.getById(request.getUserId()).orElseThrow(UserNotExistsException::new);
		Team team = teamService.getById(teamId).orElseThrow(TeamNotExistsException::new);

		if (!teamService.isMembersLessThenMax(team)) {
			throw new MaxMembersInTeamException();
		}
		if (!teamService.isUserInRequestList(team, user)) {
			throw new UserNotInRequestListException();
		}

		return teamService.acceptInTeam(team, user);
	}

	@PostMapping("/{teamId:[0-9]+}/change_captain")
	@JsonView(Views.Internal.class)
	@PreAuthorize("@mvcAccessChecker.checkUserIsOwnerOfTeam(#teamId)")
	public Team changeCaptain(@RequestBody @Valid UserIdRequest request, @PathVariable long teamId) {
		User user = userService.getById(request.getUserId()).orElseThrow(UserNotExistsException::new);
		Team team = teamService.getById(teamId).orElseThrow(TeamNotExistsException::new);

		if (!teamService.isUserInTeam(team, user)) {
			throw new UserNotInTeamException();
		}

		return teamService.changeCaptain(team, user);
	}

	@DeleteMapping("/{teamId:[0-9]+}/del_member")
	@PreAuthorize("@mvcAccessChecker.checkMemberOfTeamOrRequested(#teamId)")
	@JsonView(Views.DetailedInternal.class)
	public Team deleteMember(@RequestBody @Valid UserIdRequest request, @PathVariable long teamId, @Parameter(hidden = true) User requester) {
		User user = userService.getById(request.getUserId()).orElseThrow(UserNotExistsException::new);
		Team team = teamService.getById(teamId).orElseThrow(TeamNotExistsException::new);

		if (
			!requester.getId().equals(user.getId()) &&
				!teamService.isUserCaptain(team, requester) &&
				!requester.getUserRole().equals(UserRole.ADMIN)
		) {
			throw new NotCaptainTryingDeleteAnotherUserException();
		}

		if (teamService.isUserInTeam(team, user)) {
			teamService.deleteMember(team, user);
			Optional<Team> optionalTeam = teamService.getById(teamId);
			if (optionalTeam.isEmpty()) {
				return null;
			}
		} else if (teamService.isUserInRequestList(team, user)) {
			team = teamService.deleteRequest(team, user);
		} else {
			throw new UserNotInTeamOrRequestsListException();
		}

		return team;
	}
}
