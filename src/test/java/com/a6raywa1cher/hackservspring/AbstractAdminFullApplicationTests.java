package com.a6raywa1cher.hackservspring;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import(HackServTestConfiguration.class)
public abstract class AbstractAdminFullApplicationTests extends AbstractFullApplicationTests {
	public static final String USER_EMAIL = "admin@admin.io";
	public static final String USER_PASSWORD = "admin";

	@BeforeEach
	public void before() throws Exception {
		register(USER_EMAIL, USER_PASSWORD);
	}
}
