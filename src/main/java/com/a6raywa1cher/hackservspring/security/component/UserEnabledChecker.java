package com.a6raywa1cher.hackservspring.security.component;

import com.a6raywa1cher.hackservspring.model.User;

public interface UserEnabledChecker {
	boolean check(User user);
}
