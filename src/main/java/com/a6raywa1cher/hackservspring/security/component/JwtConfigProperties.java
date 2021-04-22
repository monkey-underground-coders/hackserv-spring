package com.a6raywa1cher.hackservspring.security.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "jwt")
@Validated
@Data
public class JwtConfigProperties {
	@PositiveOrZero
	private int maxRefreshTokensPerUser;

	@NotBlank
	private String secret;

	@NotNull
	private Duration accessDuration;

	@NotNull
	private Duration refreshDuration;
}
