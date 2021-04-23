package com.a6raywa1cher.hackservspring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HackServSpringApplicationTests extends AbstractFullApplicationTest {
	@Test
	void contextLoads() {
		Assertions.assertNotNull(this.mvc);
	}
}
