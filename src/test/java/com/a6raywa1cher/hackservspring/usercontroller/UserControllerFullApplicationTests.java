package com.a6raywa1cher.hackservspring.usercontroller;

import com.a6raywa1cher.hackservspring.AbstractUserFullApplicationTests;
import com.a6raywa1cher.hackservspring.rest.req.EmailValidationTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static com.a6raywa1cher.hackservspring.TestUtils.find;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class UserControllerFullApplicationTests extends AbstractUserFullApplicationTests {
	@Autowired
	JavaMailSender mockMailSender;

	@Captor
	ArgumentCaptor<MimeMessage> argCaptor;

	@Test
	public void validateUser() throws Exception {
		authUser()
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(ADMIN_EMAIL))
			.andExpect(jsonPath("$.emailValidated").value(false));

		verify(mockMailSender).send(argCaptor.capture());

		MimeMessage value = argCaptor.getValue();
		String token = find(value.getContent().toString(), "(\\d{6})");
		Assertions.assertNotNull(token);

		userEmailValidate(new EmailValidationTokenRequest(Integer.parseInt(token)))
			.andExpect(status().isOk());

		authUser()
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.emailValidated").value(true));
	}
}
