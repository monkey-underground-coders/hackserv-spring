package com.a6raywa1cher.hackservspring.rest;


import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.rest.exc.*;
import com.a6raywa1cher.hackservspring.rest.req.CreateTeamRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutTeamInfoRequest;
import com.a6raywa1cher.hackservspring.rest.req.UserIdRequest;
import com.a6raywa1cher.hackservspring.service.TeamService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.service.dto.TeamInfo;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Optional;


@RestController
@RequestMapping("/team")
@Transactional(rollbackOn = Exception.class)
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    public TeamController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    @PostMapping("/create")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @JsonView(Views.Internal.class)
    public ResponseEntity<Team> createTeam(@RequestBody CreateTeamRequest request) throws UserNotExistsException, UserAlreadyInTeam {
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
    @JsonView(Views.Internal.class)
    public ResponseEntity<Team> getTeam(@PathVariable long teamid) throws TeamNotExistsException {
        Optional<Team> optionalTeam = teamService.getById(teamid);
        if (optionalTeam.isEmpty()) {
            throw new TeamNotExistsException();
        }
        Team team = optionalTeam.get();

        return ResponseEntity.ok(team);
    }

    @PutMapping("/{teamid:[0-9]+}")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkTeamCaptainWithCurrentUser(#teamid)")
    @JsonView(Views.Internal.class)
    public ResponseEntity<Team> editTeamInfo(@RequestBody PutTeamInfoRequest request, @PathVariable long teamid) throws TeamNotExistsException {
        Optional<Team> optionalTeam = teamService.getById(teamid);
        if (optionalTeam.isEmpty()) {
            throw new TeamNotExistsException();
        }

        TeamInfo teamInfo = new TeamInfo();
        BeanUtils.copyProperties(request, teamInfo);

        Team team = teamService.editTeam(optionalTeam.get(), teamInfo);

        return ResponseEntity.ok(team);
    }


    @DeleteMapping("/{teamid:[0-9]+}")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkTeamCaptainWithCurrentUser(#teamid)")
    @JsonView(Views.Internal.class)
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
    @JsonView(Views.Internal.class)
    public ResponseEntity<Team> requestInTeam(@RequestBody UserIdRequest request, @PathVariable long teamid) throws UserNotExistsException, TeamNotExistsException, UserAlreadyInTeam, UserAlreadyMadeRequest {
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
    @PreAuthorize("@mvcAccessChecker.checkTeamCaptainWithCurrentUser(#teamid)")
    public ResponseEntity<Team> acceptUser(@RequestBody UserIdRequest request, @PathVariable long teamid) {
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
        if (!teamService.isUserInRequestList(team, user)) {
            throw new UserNotInRequestListException();
        }

        Team advancedTeam = teamService.acceptInTeam(team, user);

        return ResponseEntity.ok(advancedTeam);
    }


    @PostMapping("/{teamid:[0-9]+}/change_captain")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @JsonView(Views.Internal.class)
    @PreAuthorize("@mvcAccessChecker.checkTeamCaptainWithCurrentUser(#teamid)")
    public ResponseEntity<Team> changeCaptain(@RequestBody UserIdRequest request, @PathVariable long teamid) {
        Optional<User> optionalUser = userService.getById(request.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotExistsException();
        }
        User user = optionalUser.get();
        Optional<Team> optionalTeam = teamService.getById(teamid);
        if (optionalTeam.isEmpty()) {
            throw new TeamNotExistsException();
        }

        if (user.getTeam().getId() != teamid) {
            throw new UserNotInTeamException();
        }

        Team team = teamService.changeCaptain(optionalTeam.get(), user);
        return ResponseEntity.ok(team);
    }

}
