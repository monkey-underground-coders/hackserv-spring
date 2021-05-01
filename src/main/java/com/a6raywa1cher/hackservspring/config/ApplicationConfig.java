package com.a6raywa1cher.hackservspring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

@Configuration
@EnableSpringDataWebSupport
@EnableScheduling
@Slf4j
public class ApplicationConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@ConditionalOnProperty(prefix = "app", name = "email-verification", havingValue = "false")
	@ConditionalOnMissingBean
	public JavaMailSender javaMailSender() {
		return new JavaMailSender() {
			private final JavaMailSender javaMailSender = new JavaMailSenderImpl();

			@Override
			public MimeMessage createMimeMessage() {
				return javaMailSender.createMimeMessage();
			}

			@Override
			public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
				return javaMailSender.createMimeMessage(contentStream);
			}

			@Override
			public void send(MimeMessage mimeMessage) throws MailException {

			}

			@Override
			public void send(MimeMessage... mimeMessages) throws MailException {

			}

			@Override
			public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {

			}

			@Override
			public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {

			}

			@Override
			public void send(SimpleMailMessage simpleMessage) throws MailException {

			}

			@Override
			public void send(SimpleMailMessage... simpleMessages) throws MailException {

			}
		};
	}
}
