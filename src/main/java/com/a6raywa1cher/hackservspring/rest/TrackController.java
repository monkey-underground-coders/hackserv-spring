package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.model.Track;
import com.a6raywa1cher.hackservspring.rest.exc.TrackNotExistsException;
import com.a6raywa1cher.hackservspring.rest.req.CreateTrackRequest;
import com.a6raywa1cher.hackservspring.rest.req.PutTrackRequest;
import com.a6raywa1cher.hackservspring.service.TrackService;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/track")
@Transactional(rollbackOn = Exception.class)
public class TrackController {
	private final TrackService trackService;

	public TrackController(TrackService trackService) {
		this.trackService = trackService;
	}

	@GetMapping("/{trackid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Public.class)
	public ResponseEntity<Track> getTrack(@PathVariable Long trackid) throws TrackNotExistsException {
		Optional<Track> optionalTrack = trackService.getById(trackid);
		if (optionalTrack.isEmpty()) {
			throw new TrackNotExistsException();
		}
		Track track = optionalTrack.get();

		return ResponseEntity.ok(track);
	}

	@PostMapping("/create")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Internal.class)
	public ResponseEntity<Track> createTrack(@RequestBody @Valid CreateTrackRequest request) {
		Track track = trackService.create(request.getTrackName());
		return ResponseEntity.ok(track);
	}

	@PutMapping("/{trackid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Internal.class)
	public ResponseEntity<Track> editTrack(@RequestBody @Valid PutTrackRequest request, @PathVariable long trackid) throws TrackNotExistsException {
		Optional<Track> optionalTrack = trackService.getById(trackid);
		if (optionalTrack.isEmpty()) {
			throw new TrackNotExistsException();
		}
		Track track = optionalTrack.get();
		trackService.editTrack(track, request.getTrackName());
		return ResponseEntity.ok(track);
	}

	@DeleteMapping("/{trackid}")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	public ResponseEntity<Void> deleteTrack(@PathVariable Long trackid) throws TrackNotExistsException {
		Optional<Track> optionalTrack = trackService.getById(trackid);
		if (optionalTrack.isEmpty()) {
			throw new TrackNotExistsException();
		}
		Track track = optionalTrack.get();
		trackService.delete(track);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/")
	@Operation(security = @SecurityRequirement(name = "jwt"))
	@JsonView(Views.Public.class)
	public ResponseEntity<List<Track>> getAllTracks() {
		List<Track> tracks = trackService.getAllTracks().collect(Collectors.toList());
		return ResponseEntity.ok(tracks);
	}
}
