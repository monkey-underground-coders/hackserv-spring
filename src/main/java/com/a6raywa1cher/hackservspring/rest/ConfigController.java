package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.rest.req.PutHackStateRequest;
import com.a6raywa1cher.hackservspring.rest.res.GetConfigResponse;
import com.a6raywa1cher.hackservspring.service.HackStateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Duration;

@RestController
public class ConfigController {

	private final HackStateService hackStateService;

	@Value("${spring.servlet.multipart.max-file-size}")
	private DataSize maxFileSize;

	@Value("${app.min-email-req}")
	private Duration minEmailReq;

	@Value("${app.max-email-duration}")
	private Duration maxEmailDuration;

	@Autowired
	public ConfigController(HackStateService hackStateService) {
		this.hackStateService = hackStateService;
	}

	@GetMapping("/conf")
	@SecurityRequirements // erase jwt login
	public GetConfigResponse getConfig() {
		GetConfigResponse response = new GetConfigResponse();

		response.setMaxFileSize(maxFileSize.toBytes() + "B");
		response.setMinEmailReq(minEmailReq.toSeconds());
		response.setMaxEmailDuration(maxEmailDuration.toSeconds());
		response.setCurrentHackState(hackStateService.get().toString());

		return response;
	}

	@PutMapping
	public GetConfigResponse SetHackState(@RequestBody @Valid PutHackStateRequest request) {
		hackStateService.set(request.getHackState());
		return this.getConfig();
	}
}
