package com.a6raywa1cher.hackservspring.security.component;

import com.a6raywa1cher.hackservspring.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface GrantedAuthorityService {
	Collection<GrantedAuthority> getAuthorities(User user);
}
