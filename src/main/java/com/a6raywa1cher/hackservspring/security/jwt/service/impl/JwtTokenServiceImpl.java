package com.a6raywa1cher.hackservspring.security.jwt.service.impl;

import com.a6raywa1cher.hackservspring.model.VendorId;
import com.a6raywa1cher.hackservspring.security.jwt.JwtToken;
import com.a6raywa1cher.hackservspring.security.jwt.service.JwtTokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
	private final static String ISSUER_NAME = "hackserv-spring";
	private final static String REFRESH_TOKEN_ID_CLAIM = "rti";
	private final static String VENDOR_SUB = "vsub";
	private final static String VENDOR_ID = "vid";
	@Value("${jwt.secret}")
	private String secret;
	private Algorithm algorithm;
	private JWTVerifier jwtVerifier;
	@Value("${jwt.access-duration}")
	private Duration duration;

	@PostConstruct
	public void init() {
		algorithm = Algorithm.HMAC512(secret);
		jwtVerifier = JWT.require(algorithm)
				.withIssuer(ISSUER_NAME)
				.build();
	}

	@Override
	public JwtToken issue(Long userId, Long refreshId) {
		return issue(userId, refreshId, null, null);
	}

	@Override
	public JwtToken issue(Long userId, Long refreshId, VendorId vendorId, String vendorSub) {
		ZonedDateTime expiringAt = nowPlusDuration();
		String token = JWT.create()
				.withIssuer(ISSUER_NAME)
				.withSubject(Long.toString(userId))
				.withExpiresAt(Date.from(expiringAt.toInstant()))
				.withClaim(REFRESH_TOKEN_ID_CLAIM, refreshId)
				.withClaim(VENDOR_ID, vendorId != null ? vendorId.toString() : null)
				.withClaim(VENDOR_SUB, vendorSub)
				.sign(algorithm);
		return JwtToken.builder()
				.token(token)
				.uid(userId)
				.expiringAt(expiringAt.toLocalDateTime())
				.build();
	}

	private ZonedDateTime nowPlusDuration() {
		return ZonedDateTime.now().plus(duration);
	}

	@Override
	public Optional<JwtToken> decode(String token) {
		try {
			DecodedJWT decodedJWT = jwtVerifier.verify(token);
			JwtToken.JwtTokenBuilder builder = JwtToken.builder()
					.token(token)
					.uid(Long.parseLong(decodedJWT.getSubject()))
					.expiringAt(decodedJWT.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
					.refreshId(decodedJWT.getClaim(REFRESH_TOKEN_ID_CLAIM).asLong());
			if (!decodedJWT.getClaim(VENDOR_ID).isNull()) {
				builder = builder
						.vendorId(VendorId.valueOf(decodedJWT.getClaim(VENDOR_ID).asString()))
						.vendorSub(decodedJWT.getClaim(VENDOR_SUB).asString());
			}
			return Optional.of(builder.build());
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
