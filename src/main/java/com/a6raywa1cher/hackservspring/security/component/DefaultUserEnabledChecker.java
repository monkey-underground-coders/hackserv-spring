package com.a6raywa1cher.hackservspring.security.component;

import com.a6raywa1cher.hackservspring.model.User;


public class DefaultUserEnabledChecker implements UserEnabledChecker {
	@Override
	public boolean check(User user) {
		return true;
	}
}
