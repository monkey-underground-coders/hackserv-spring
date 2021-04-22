package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.rest.exc.TrackNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateTrackRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutTrackRequest;
import com.a6raywa1cher.hackservspring.service.TrackService;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/track")
@Transactional(rollbackOn = Exception.class)
public class TrackController {
	private final TrackService trackService;

	public TrackController(TrackService trackService) {
		this.trackService = trackService;
	}

	@GetMapping("/{trackId}")
	@JsonView(Views.Public.class)
	public Track getTrack(@PathVariable long trackId) throws TrackNotExistsException {
		return trackService.getById(trackId).orElseThrow(TrackNotExistsException::new);
	}

	@PostMapping("/create")
	@JsonView(Views.Internal.class)
	public Track createTrack(@RequestBody @Valid CreateTrackRequest request) {
		return trackService.create(request.getTrackName());
	}

	@PutMapping("/{trackId}")
	@JsonView(Views.Internal.class)
	public Track editTrack(@RequestBody @Valid PutTrackRequest request, @PathVariable long trackId) throws TrackNotExistsException {
		Track track = trackService.getById(trackId).orElseThrow(TrackNotExistsException::new);
		return trackService.editTrack(track, request.getTrackName());
	}

	@DeleteMapping("/{trackId}")
	public void deleteTrack(@PathVariable long trackId) throws TrackNotExistsException {
		Track track = trackService.getById(trackId).orElseThrow(TrackNotExistsException::new);
		trackService.delete(track);
	}

	@GetMapping("/")
	@JsonView(Views.Public.class)
	public List<Track> getAllTracks() {
		return trackService.getAllTracks().collect(Collectors.toList());
	}
}
