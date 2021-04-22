package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.rest.res.GetConfigResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class ConfigController {
	@Value("${spring.servlet.multipart.max-file-size}")
	private DataSize maxFileSize;

	@Value("${app.min-email-req}")
	private Duration minEmailReq;

	@Value("${app.max-email-duration}")
	private Duration maxEmailDuration;

	@GetMapping("/conf")
	@SecurityRequirements // erase jwt login
	public GetConfigResponse getConfig() {
		GetConfigResponse response = new GetConfigResponse();

		response.setMaxFileSize(maxFileSize.toBytes() + "B");
		response.setMinEmailReq(minEmailReq.toSeconds());
		response.setMaxEmailDuration(maxEmailDuration.toSeconds());

		return response;
	}
}
