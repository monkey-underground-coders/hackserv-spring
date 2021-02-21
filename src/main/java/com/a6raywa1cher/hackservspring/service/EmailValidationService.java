package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;

import javax.mail.MessagingException;
import java.util.Optional;

public interface EmailValidationService {
    Optional<EmailValidationToken> getById(Long id);

    EmailValidationToken createToken();

    void sendMassage(EmailValidationToken token) throws MessagingException;


}
