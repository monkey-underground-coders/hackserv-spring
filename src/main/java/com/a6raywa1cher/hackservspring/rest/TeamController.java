package com.a6raywa1cher.hackservspring.rest;


import com.a6raywa1cher.hackservspring.model.Team;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.rest.exc.TeamNotExistsException;
import com.a6raywa1cher.hackservspring.rest.exc.UserNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateTeamRequest;
import com.a6raywa1cher.hackservspring.service.TeamService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    public ResponseEntity<Team> createTeam(@RequestBody CreateTeamRequest request) throws UserNotExistsException {
        Optional<User> optionalCaptain = userService.getById(request.getCaptainId());
        if (optionalCaptain.isEmpty()) {
            throw new UserNotExistsException();
        }
        User captain = optionalCaptain.get();
        Team team = teamService.createTeam(request.getName(), captain);

        return ResponseEntity.ok(team);
    }

    @GetMapping("/{uid:[0-9]+}")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.Internal.class)
    public ResponseEntity<Team> getTeam(@PathVariable long uid) throws TeamNotExistsException {
        Optional<Team> optionalTeam = teamService.getById(uid);
        if (optionalTeam.isEmpty()) {
            throw new TeamNotExistsException();
        }
        Team team = optionalTeam.get();

        return ResponseEntity.ok(team);
    }

    @PutMapping("/{uid:[0-9]+}")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    @PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
    @JsonView(Views.Internal.class)
    public ResponseEntity<Team> putTeam(, @PathVariable long uid)
}
