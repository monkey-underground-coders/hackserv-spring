package com.a6raywa1cher.hackservspring.security.component;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GrantedAuthorityServiceImpl implements GrantedAuthorityService {
	private final UserEnabledChecker userEnabledChecker;

	@Autowired
	public GrantedAuthorityServiceImpl(UserEnabledChecker userEnabledChecker) {
		this.userEnabledChecker = userEnabledChecker;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities(User user) {
		UserRole userRole = user.getUserRole();
		Set<GrantedAuthority> authoritySet = userRole.access.stream()
			.map(role -> new SimpleGrantedAuthority("ACCESS_" + role.name()))
			.collect(Collectors.toSet());
		authoritySet.add(new SimpleGrantedAuthority("ROLE_USER"));
		authoritySet.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
		if (userEnabledChecker.check(user))
			authoritySet.add(new SimpleGrantedAuthority("ENABLED"));
		return authoritySet;
	}
}
