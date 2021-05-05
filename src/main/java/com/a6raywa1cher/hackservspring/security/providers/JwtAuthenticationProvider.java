package com.a6raywa1cher.hackservspring.security.providers;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.security.authentication.CustomAuthentication;
import com.a6raywa1cher.hackservspring.security.authentication.JwtAuthentication;
import com.a6raywa1cher.hackservspring.security.component.GrantedAuthorityService;
import com.a6raywa1cher.hackservspring.security.jwt.JwtToken;
import com.a6raywa1cher.hackservspring.security.jwt.service.BlockedRefreshTokensService;
import com.a6raywa1cher.hackservspring.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

public class JwtAuthenticationProvider implements AuthenticationProvider {
	private final UserService userService;
	private final BlockedRefreshTokensService service;
	private final GrantedAuthorityService grantedAuthorityService;

	public JwtAuthenticationProvider(UserService userService, BlockedRefreshTokensService service,
	                                 GrantedAuthorityService grantedAuthorityService) {
		this.userService = userService;
		this.service = service;
		this.grantedAuthorityService = grantedAuthorityService;
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
		Collection<GrantedAuthority> authorities = grantedAuthorityService.getAuthorities(user);
		return new CustomAuthentication(authorities, jwtToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthentication.class.isAssignableFrom(authentication);
	}
}
