package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;
import com.a6raywa1cher.hackservspring.model.User;

import javax.mail.MessagingException;
import java.util.Optional;

public interface EmailValidationService {
    Optional<EmailValidationToken> getById(Long id);

    void createToken(User user);

    void sendMassage(User user) throws MessagingException;

    boolean checkToken(User user, int token);
}
