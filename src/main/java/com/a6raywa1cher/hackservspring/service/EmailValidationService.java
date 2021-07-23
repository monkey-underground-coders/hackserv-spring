package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;
import com.a6raywa1cher.hackservspring.model.User;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.UUID;

public interface EmailValidationService {
	Optional<EmailValidationToken> getById(UUID id);

	void createToken(User user);

	void sendMassage(User user) throws MessagingException;

	boolean checkToken(User user, int token);

	boolean checkToken(User user, UUID id);

	boolean isLastSendWasRecently(User user);

	boolean isTokenEnable(User user);

	void delete(User user);
}
