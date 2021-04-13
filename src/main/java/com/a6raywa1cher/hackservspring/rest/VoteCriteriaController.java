package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.model.VoteCriteria;
import com.a6raywa1cher.hackservspring.rest.exc.TrackNotExistsException;
import com.a6raywa1cher.hackservspring.rest.exc.VoteCriteriaNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateVoteCriteriaRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutVoteCriteriaInfoRequest;
import com.a6raywa1cher.hackservspring.service.TrackService;
import com.a6raywa1cher.hackservspring.service.VoteCriteriaService;
import com.a6raywa1cher.hackservspring.service.dto.VoteCriteriaInfo;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Optional;

@RestController
@RequestMapping("/criteria")
@Transactional(rollbackOn = Exception.class)
public class VoteCriteriaController {
	VoteCriteriaService criteriaService;
	TrackService trackService;

	public VoteCriteriaController(VoteCriteriaService criteriaService, TrackService trackService) {
		this.criteriaService = criteriaService;
		this.trackService = trackService;
	}

	@GetMapping("/{criteriaid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.DetailedInternal.class)
	public ResponseEntity<VoteCriteria> getCriteria(@PathVariable long criteriaid) throws VoteCriteriaNotExistsException {
		Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
		if (optionalVoteCriteria.isEmpty()) {
			throw new VoteCriteriaNotExistsException();
		}
		VoteCriteria criteria = optionalVoteCriteria.get();

		return ResponseEntity.ok(criteria);
	}

	@PutMapping(path = "/{criteriaid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.DetailedInternal.class)
	public ResponseEntity<VoteCriteria> editCriteriaInfo(@PathVariable Long criteriaid, @RequestBody PutVoteCriteriaInfoRequest request) throws VoteCriteriaNotExistsException {
		Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
		if (optionalVoteCriteria.isEmpty()) {
			throw new VoteCriteriaNotExistsException();
		}
		VoteCriteriaInfo info = new VoteCriteriaInfo();
		BeanUtils.copyProperties(request, info);

		VoteCriteria criteria = criteriaService.editCriteriaInfo(optionalVoteCriteria.get(), info);

		return ResponseEntity.ok(criteria);
	}

	@PostMapping(path = "/create")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.DetailedInternal.class)
	public ResponseEntity<VoteCriteria> createCriteria(@RequestBody CreateVoteCriteriaRequest request) throws TrackNotExistsException {
		Optional<Track> optionalTrack = trackService.getById((request.getTrackId()));
		if (optionalTrack.isEmpty()) {
			throw new TrackNotExistsException();
		}
		VoteCriteria criteria = criteriaService.create(request.getName(), request.getMaxValue(), optionalTrack.get());
		return ResponseEntity.ok(criteria);
	}

	@DeleteMapping(path = "/{criteriaid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.DetailedInternal.class)
	public ResponseEntity<VoteCriteria> deleteCriteria(@PathVariable Long criteriaid) throws VoteCriteriaNotExistsException {
		Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
		if (optionalVoteCriteria.isEmpty()) {
			throw new VoteCriteriaNotExistsException();
		}
		VoteCriteria criteria = optionalVoteCriteria.get();
		criteriaService.deleteCriteria(criteria);
		return ResponseEntity.ok().build();
	}
}
