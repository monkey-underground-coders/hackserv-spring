package com.a6raywa1cher.hackservspring;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@TestConfiguration
public class HackServTestConfiguration {
	@Bean
	public JavaMailSender javaMailSender() throws MessagingException {
		JavaMailSenderImpl spy = Mockito.spy(new JavaMailSenderImpl());
		doNothing().when(spy).testConnection();
		doNothing().when(spy).send(any(MimeMessage.class));
		return spy;
	}
}
