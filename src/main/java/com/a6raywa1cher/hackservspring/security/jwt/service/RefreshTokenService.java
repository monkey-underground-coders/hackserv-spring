package com.a6raywa1cher.hackservspring.security.jwt.service;


import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.security.jpa.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
	RefreshToken issue(User user);

	Optional<RefreshToken> getByToken(String token);

	void invalidate(RefreshToken refreshToken);

	void invalidateAll(User user);
}
