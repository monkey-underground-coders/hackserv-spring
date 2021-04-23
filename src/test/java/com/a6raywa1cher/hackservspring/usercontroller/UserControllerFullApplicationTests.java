package com.a6raywa1cher.hackservspring.usercontroller;

import com.a6raywa1cher.hackservspring.AbstractFullApplicationTest;
import com.a6raywa1cher.hackservspring.rest.req.CreateUserRequest;
import com.a6raywa1cher.hackservspring.rest.req.EmailValidationTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static com.a6raywa1cher.hackservspring.TestUtils.find;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@Import(UserControllerFullApplicationTestsConfig.class)
@Slf4j
public class UserControllerFullApplicationTests extends AbstractFullApplicationTest {
	@Autowired
	JavaMailSender mockMailSender;

	@Captor
	ArgumentCaptor<MimeMessage> argCaptor;

	@Test
	public void createAndValidateUser() throws Exception {
		doNothing().when(mockMailSender).send(any(MimeMessage.class));

		String email = "jo@oj.ee";
		String password = "fish";

		userCreate(new CreateUserRequest(email, password))
			.andExpect(status().isOk());

		login(email, password);

		authUser()
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(email))
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
