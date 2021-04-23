package com.a6raywa1cher.hackservspring;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import(HackServTestConfiguration.class)
public abstract class AbstractUserFullApplicationTests extends AbstractFullApplicationTests {
	public static final String ADMIN_EMAIL = "jo@kq.et";
	public static final String ADMIN_PASSWORD = "password";

	@BeforeEach
	public void before() throws Exception {
		register(ADMIN_EMAIL, ADMIN_PASSWORD);
	}
}
