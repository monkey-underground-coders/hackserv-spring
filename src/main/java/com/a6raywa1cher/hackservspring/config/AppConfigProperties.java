package com.a6raywa1cher.hackservspring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "app")
@Validated
@Data
public class AppConfigProperties {
	private String uploadDir;

	@NotNull
	private String[] corsAllowedOrigins;

	@NotNull
	@Valid
	private FirstAdmin firstAdmin;

	private String version;

	private Boolean emailVerification;

	private Boolean oauthSupport;

	private String redirect;

	@NotNull
	private Duration minEmailReq;

	@NotNull
	private Duration maxEmailDuration;

	private int maxMembersInTeam;

	private String apiEndpoint;

	private URI emailValidationBaseLink;

	@Data
	public static final class FirstAdmin {
		@NotBlank
		private String email;

		@NotBlank
		private String password;
	}
}
