package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.repo.EmailValidationTokenRepository;
import com.a6raywa1cher.hackservspring.model.repo.UserRepository;
import com.a6raywa1cher.hackservspring.service.EmailValidationService;
import com.a6raywa1cher.hackservspring.utils.ResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class EmailValidationServiceImpl implements EmailValidationService {
    private final EmailValidationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender emailSender;

    @Value("classpath:emails/EmailValidationTemplate.html")
    private Resource mailHtml;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public EmailValidationServiceImpl(EmailValidationTokenRepository tokenRepository, UserRepository userRepository, JavaMailSender emailSender) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }


    @Override
    public Optional<EmailValidationToken> getById(Long id) {
        return tokenRepository.findById(id);
    }

    @Override
    public void createToken(User user) {
        EmailValidationToken token = new EmailValidationToken();
        SecureRandom secureRandom = new SecureRandom();
        int tokenInt = 100000 + secureRandom.nextInt(900000);
        token.setToken(tokenInt);
        token.setCreatedAt(ZonedDateTime.now());
        EmailValidationToken saved = tokenRepository.save(token);
        user.setEmailValidationToken(saved);
        userRepository.save(user);
    }

    @Override
    public void sendMassage(User user) throws MessagingException {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String editedMessage = String.format(ResourceReader.asString(mailHtml), user.getEmailValidationToken().getToken());
        helper.setText(editedMessage, true);
        helper.setTo(user.getEmail());
        helper.setSubject("Открой");
        helper.setFrom(from);
        emailSender.send(mimeMessage);
    }

    @Override
    public boolean checkToken(User user, int token) {
        return user.getEmailValidationToken().getToken() == token;
    }
}
