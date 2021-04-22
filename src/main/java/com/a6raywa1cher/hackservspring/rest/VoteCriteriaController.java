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
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/criteria")
@Transactional(rollbackOn = Exception.class)
public class VoteCriteriaController {
	private final VoteCriteriaService criteriaService;
	private final TrackService trackService;

	public VoteCriteriaController(VoteCriteriaService criteriaService, TrackService trackService) {
		this.criteriaService = criteriaService;
		this.trackService = trackService;
	}

	@GetMapping("/{criteriaid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Public.class)
	public VoteCriteria getCriteria(@PathVariable long criteriaid) throws VoteCriteriaNotExistsException {
		Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
		if (optionalVoteCriteria.isEmpty()) {
			throw new VoteCriteriaNotExistsException();
		}

		return optionalVoteCriteria.get();
	}

	@PutMapping(path = "/{criteriaid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.DetailedInternal.class)
	public VoteCriteria editCriteria(@PathVariable long criteriaid, @RequestBody @Valid PutVoteCriteriaInfoRequest request) throws VoteCriteriaNotExistsException {
		Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
		if (optionalVoteCriteria.isEmpty()) {
			throw new VoteCriteriaNotExistsException();
		}
		VoteCriteriaInfo info = new VoteCriteriaInfo();
		BeanUtils.copyProperties(request, info);

		return criteriaService.editCriteriaInfo(optionalVoteCriteria.get(), info);
	}

	@PostMapping(path = "/create")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.DetailedInternal.class)
	public VoteCriteria createCriteria(@RequestBody @Valid CreateVoteCriteriaRequest request) throws TrackNotExistsException {
		Optional<Track> optionalTrack = trackService.getById((request.getTrackId()));
		if (optionalTrack.isEmpty()) {
			throw new TrackNotExistsException();
		}
		return criteriaService.create(request.getName(), request.getMaxValue(), optionalTrack.get());
	}

	@DeleteMapping(path = "/{criteriaid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	public void deleteCriteria(@PathVariable long criteriaid) throws VoteCriteriaNotExistsException {
		Optional<VoteCriteria> optionalVoteCriteria = criteriaService.getById(criteriaid);
		if (optionalVoteCriteria.isEmpty()) {
			throw new VoteCriteriaNotExistsException();
		}
		VoteCriteria criteria = optionalVoteCriteria.get();
		criteriaService.deleteCriteria(criteria);
	}
}
