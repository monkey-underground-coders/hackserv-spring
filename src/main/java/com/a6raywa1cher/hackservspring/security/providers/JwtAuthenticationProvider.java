package com.a6raywa1cher.hackservspring.security.providers;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.security.authentication.CustomAuthentication;
import com.a6raywa1cher.hackservspring.security.authentication.JwtAuthentication;
import com.a6raywa1cher.hackservspring.security.component.UserEnabledChecker;
import com.a6raywa1cher.hackservspring.security.jwt.JwtToken;
import com.a6raywa1cher.hackservspring.security.jwt.service.BlockedRefreshTokensService;
import com.a6raywa1cher.hackservspring.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
	private final UserService userService;
	private final BlockedRefreshTokensService service;
	private final UserEnabledChecker userEnabledChecker;

	public JwtAuthenticationProvider(UserService userService, BlockedRefreshTokensService service, UserEnabledChecker userEnabledChecker) {
		this.userService = userService;
		this.service = service;
		this.userEnabledChecker = userEnabledChecker;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!supports(authentication.getClass())) {
			return null;
		}
		JwtAuthentication customAuthentication = (JwtAuthentication) authentication;
		JwtToken jwtToken = customAuthentication.getCredentials();
		if (jwtToken == null) {
			customAuthentication.setAuthenticated(false);
			throw new BadCredentialsException("JwtToken not provided");
		}
		if (!service.isValid(jwtToken.getRefreshId())) {
			throw new CredentialsExpiredException("Refresh-token was revoked");
		}
		Long userId = jwtToken.getUid();
		Optional<User> byId = userService.getById(userId);
		if (byId.isEmpty()) {
			customAuthentication.setAuthenticated(false);
			throw new UsernameNotFoundException(String.format("User %d doesn't exists", userId));
		}
		User user = byId.get();
		UserRole userRole = user.getUserRole();
		Set<GrantedAuthority> authoritySet = userRole.access.stream()
				.map(role -> new SimpleGrantedAuthority("ACCESS_" + role.name()))
				.collect(Collectors.toSet());
		authoritySet.add(new SimpleGrantedAuthority("ROLE_USER"));
		authoritySet.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
		if (userEnabledChecker.check(user))
			authoritySet.add(new SimpleGrantedAuthority("ENABLED"));
		return new CustomAuthentication(authoritySet, jwtToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthentication.class.isAssignableFrom(authentication);
	}
}
