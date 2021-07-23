package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.repo.EmailValidationTokenRepository;
import com.a6raywa1cher.hackservspring.model.repo.UserRepository;
import com.a6raywa1cher.hackservspring.service.EmailValidationService;
import com.a6raywa1cher.hackservspring.utils.ResourceReader;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.URI;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class EmailValidationServiceImpl implements EmailValidationService {
	private final EmailValidationTokenRepository tokenRepository;
	private final UserRepository userRepository;
	private final JavaMailSender emailSender;
	private final SecureRandom secureRandom = new SecureRandom();

	@Value("classpath:emails/EmailValidationTemplate.html")
	private Resource mailHtml;

	@Value("${spring.mail.username:}")
	private String from;

	@Value("${app.min-email-req}")
	private Duration minEmailReq;

	@Value("${app.max-email-duration}")
	private Duration maxEmailDuration;

	@Value("${app.email-validation-base-link}")
	private URI emailValidationBaseLink;

	@Autowired
	public EmailValidationServiceImpl(EmailValidationTokenRepository tokenRepository, UserRepository userRepository, JavaMailSender emailSender) {
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
		this.emailSender = emailSender;
	}


	@Override
	public Optional<EmailValidationToken> getById(UUID id) {
		return tokenRepository.findById(id);
	}

	@Override
	public void createToken(User user) {
		EmailValidationToken token = new EmailValidationToken();
		int tokenInt = 100000 + secureRandom.nextInt(900000);
		token.setToken(tokenInt);
		token.setCreatedAt(ZonedDateTime.now());
		EmailValidationToken saved = tokenRepository.save(token);
		if (user.getEmailValidationToken() != null) {
			tokenRepository.delete(user.getEmailValidationToken());
		}
		user.setEmailValidationToken(saved);
		userRepository.save(user);
	}

	@Override
	public void sendMassage(User user) throws MessagingException {
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		String template = ResourceReader.asString(mailHtml);
		EmailValidationToken validationToken = user.getEmailValidationToken();
		StringSubstitutor sub = new StringSubstitutor(Map.of(
			"token", validationToken.getToken(),
			"link", UriComponentsBuilder.fromUri(emailValidationBaseLink)
				.queryParam("id", validationToken.getId().toString())
				.queryParam("user", user.getId())
				.build().toString()
		));
		String editedMessage = sub.replace(template);
		helper.setText(editedMessage, true);
		helper.setTo(user.getEmail());
		helper.setSubject("Открой");
		if (isNotBlank(from)) helper.setFrom(from);
		emailSender.send(mimeMessage);
	}

	@Override
	public boolean checkToken(User user, int token) {
		return user.getEmailValidationToken().getToken() == token;
	}

	@Override
	public boolean checkToken(User user, UUID id) {
		return Objects.equals(user.getEmailValidationToken().getId(), id);
	}

	@Override
	public boolean isLastSendWasRecently(User user) {
		if (user.getEmailValidationToken() == null) {
			return false;
		}
		ZonedDateTime createdAt = user.getEmailValidationToken().getCreatedAt();
		Duration duration = Duration.between(createdAt, ZonedDateTime.now());
		return minEmailReq.compareTo(duration) > 0;
	}

	@Override
	public boolean isTokenEnable(User user) {
		ZonedDateTime createdAt = user.getEmailValidationToken().getCreatedAt();
		Duration duration = Duration.between(createdAt, ZonedDateTime.now());
		return maxEmailDuration.compareTo(duration) > 0;
	}

	@Override
	public void delete(User user) {
		EmailValidationToken token = user.getEmailValidationToken();
		tokenRepository.delete(token);
		user.setEmailValidationToken(null);
		userRepository.save(user);
	}
}
