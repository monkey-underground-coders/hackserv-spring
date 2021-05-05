package com.a6raywa1cher.hackservspring.security;

import com.a6raywa1cher.hackservspring.security.component.DefaultUserEnabledChecker;
import com.a6raywa1cher.hackservspring.security.component.EmailBasedUserEnabledChecker;
import com.a6raywa1cher.hackservspring.security.component.UserEnabledChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {
	@Value("${app.email-verification:false}")
	boolean emailVerification;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserEnabledChecker userEnabledChecker() {
		if (emailVerification) {
			return new EmailBasedUserEnabledChecker();
		} else {
			return new DefaultUserEnabledChecker();
		}
	}
}
