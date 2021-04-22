package com.a6raywa1cher.hackservspring.security.rest;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.security.jpa.RefreshToken;
import com.a6raywa1cher.hackservspring.security.jwt.JwtRefreshPair;
import com.a6raywa1cher.hackservspring.security.jwt.JwtToken;
import com.a6raywa1cher.hackservspring.security.jwt.service.JwtRefreshPairService;
import com.a6raywa1cher.hackservspring.security.jwt.service.JwtTokenService;
import com.a6raywa1cher.hackservspring.security.jwt.service.RefreshTokenService;
import com.a6raywa1cher.hackservspring.security.rest.exc.SameUserTokensProvidedException;
import com.a6raywa1cher.hackservspring.security.rest.req.GetNewJwtTokenRequest;
import com.a6raywa1cher.hackservspring.security.rest.req.InvalidateTokenRequest;
import com.a6raywa1cher.hackservspring.security.rest.req.LinkSocialAccountsRequest;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.utils.AuthenticationResolver;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final RefreshTokenService refreshTokenService;

	private final AuthenticationResolver authenticationResolver;

	private final JwtRefreshPairService jwtRefreshPairService;

	private final JwtTokenService jwtTokenService;

	private final UserService userService;

	public AuthController(AuthenticationResolver authenticationResolver, RefreshTokenService refreshTokenService,
	                      JwtRefreshPairService jwtRefreshPairService, JwtTokenService jwtTokenService,
	                      UserService userService) {
		this.authenticationResolver = authenticationResolver;
		this.refreshTokenService = refreshTokenService;
		this.jwtRefreshPairService = jwtRefreshPairService;
		this.jwtTokenService = jwtTokenService;
		this.userService = userService;
	}

	@GetMapping("/user")
	@JsonView(Views.DetailedInternal.class)
	public ResponseEntity<User> getCurrentUser(@Parameter(hidden = true) User user) {
		return ResponseEntity.ok(user);
	}

	@PostMapping("/convert")
	@SecurityRequirements({@SecurityRequirement(name = "basic")})
	public ResponseEntity<JwtRefreshPair> convertToJwt(HttpServletRequest request, Authentication authentication) {
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			User user = authenticationResolver.getUser();
			JwtRefreshPair pair = jwtRefreshPairService.issue(user);
			SecurityContextHolder.clearContext();
			request.getSession().invalidate();
			return ResponseEntity.ok(pair);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/get_access")
	@SecurityRequirements // erase jwt login
	public ResponseEntity<JwtRefreshPair> getNewJwtToken(@RequestBody @Valid GetNewJwtTokenRequest request) {
		Optional<RefreshToken> optional = refreshTokenService.getByToken(request.getRefreshToken());
		if (optional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		refreshTokenService.invalidate(optional.get());
		User user = optional.get().getUser();
		return ResponseEntity.ok(jwtRefreshPairService.issue(user));
	}

	@DeleteMapping("/invalidate")
	public ResponseEntity<Void> invalidateToken(@RequestBody @Valid InvalidateTokenRequest request) {
		User user = authenticationResolver.getUser();
		Optional<RefreshToken> optional = refreshTokenService.getByToken(request.getRefreshToken());
		if (optional.isPresent()) {
			RefreshToken refreshToken = optional.get();
			if (user.equals(refreshToken.getUser())) {
				refreshTokenService.invalidate(refreshToken);
			}
		}
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/invalidate_all")
	public ResponseEntity<Void> invalidateAllTokens() {
		User user = authenticationResolver.getUser();
		refreshTokenService.invalidateAll(user);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/link_social")
	@Transactional
	public ResponseEntity<JwtRefreshPair> linkSocialAccounts(@RequestBody LinkSocialAccountsRequest request) {
		Optional<User> primaryUser = refreshTokenService.getByToken(request.getPrimaryRefreshToken())
			.flatMap(rt -> Optional.of(rt.getUser()));
		Optional<JwtToken> optionalJwtToken = jwtTokenService.decode(request.getSecondaryAccessToken());
		Optional<User> secondaryUser = optionalJwtToken.flatMap(jt -> userService.getById(jt.getUid()));
		if (primaryUser.isEmpty() || secondaryUser.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		if (primaryUser.get().equals(secondaryUser.get())) {
			throw new SameUserTokensProvidedException();
		}
		JwtToken jwtToken = optionalJwtToken.get();
		if (jwtToken.getVendorId() == null) {
			return ResponseEntity.badRequest().build();
		}
		userService.deleteUser(secondaryUser.get());
		userService.setVendorSub(primaryUser.get(), jwtToken.getVendorId(), jwtToken.getVendorSub());
		return ResponseEntity.ok(jwtRefreshPairService.issue(primaryUser.get(), jwtToken.getVendorId(), jwtToken.getVendorSub()));
	}
}
