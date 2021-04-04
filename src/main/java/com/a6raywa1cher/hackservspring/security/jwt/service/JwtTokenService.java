package com.a6raywa1cher.hackservspring.security.jwt.service;


import com.a6raywa1cher.hackservspring.model.VendorId;
import com.a6raywa1cher.hackservspring.security.jwt.JwtToken;

import java.util.Optional;

public interface JwtTokenService {
	JwtToken issue(Long userId, Long refreshId);

	JwtToken issue(Long userId, Long refreshId, VendorId vendorId, String vendorSub);

	Optional<JwtToken> decode(String token);
}
