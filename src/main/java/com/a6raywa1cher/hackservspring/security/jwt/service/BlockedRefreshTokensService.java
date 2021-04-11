package com.a6raywa1cher.hackservspring.security.jwt.service;

public interface BlockedRefreshTokensService {
	void invalidate(Long id);

	boolean isValid(Long id);
}
