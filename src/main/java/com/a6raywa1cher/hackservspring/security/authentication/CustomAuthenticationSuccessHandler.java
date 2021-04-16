package com.a6raywa1cher.hackservspring.security.authentication;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.VendorId;
import com.a6raywa1cher.hackservspring.model.repo.UserRepository;
import com.a6raywa1cher.hackservspring.security.jwt.JwtRefreshPair;
import com.a6raywa1cher.hackservspring.security.jwt.service.JwtRefreshPairService;
import com.a6raywa1cher.hackservspring.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final UserService userService;
	private final UserRepository userRepository;
	private final JwtRefreshPairService jwtTokenService;
	private final String oAuthRedirectUrl;

	public CustomAuthenticationSuccessHandler(UserService userService, UserRepository userRepository,
	                                          JwtRefreshPairService jwtTokenService,
	                                          @Value("${app.redirect}") String oAuthRedirectUrl) {
		this.userService = userService;
		this.userRepository = userRepository;
		this.jwtTokenService = jwtTokenService;
		this.oAuthRedirectUrl = oAuthRedirectUrl;
	}

	private VendorId getVendorId(OAuth2AuthenticationToken authentication) {
		return VendorId.valueOf(authentication.getAuthorizedClientRegistrationId().toUpperCase());
	}

	private User getUserOrRegister(OAuth2AuthenticationToken authentication) {
		OAuth2User oAuth2User = authentication.getPrincipal();
		VendorId vendor = getVendorId(authentication);
		String email = oAuth2User.getAttribute("email");
		String id = oAuth2User.getAttribute("sub");
		Optional<User> optionalUser = userService.getByVendorId(vendor, id);
		return optionalUser.orElseGet(() -> userService.create(UserRole.USER, vendor, id, email));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		if (authentication instanceof OAuth2AuthenticationToken) { // register user
			OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
			getUserOrRegister(token);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String s = super.determineTargetUrl(request, response, authentication);
		if (authentication instanceof OAuth2AuthenticationToken) { // register user
			OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
			OAuth2User oAuth2User = token.getPrincipal();
			User user = getUserOrRegister(token);
			JwtRefreshPair jwt = jwtTokenService.issue(user, getVendorId(token), oAuth2User.getAttribute("sub"));
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(oAuthRedirectUrl)
					.queryParam("jwt", jwt.getAccessToken())
					.queryParam("rt", jwt.getRefreshToken())
					.queryParam("jwt_exp", jwt.getAccessTokenExpiringAt().toString())
					.queryParam("rt_exp", jwt.getRefreshTokenExpiringAt().toString());
			return builder.toUriString();
		}
		return s;
	}
}
