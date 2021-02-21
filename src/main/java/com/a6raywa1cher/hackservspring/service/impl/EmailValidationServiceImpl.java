package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.EmailValidationToken;
import com.a6raywa1cher.hackservspring.model.repo.EmailValidationTokenRepository;
import com.a6raywa1cher.hackservspring.service.EmailValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Optional;

@Service
public class EmailValidationServiceImpl implements EmailValidationService {
    private final EmailValidationTokenRepository repository;
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public EmailValidationServiceImpl(EmailValidationTokenRepository repository, JavaMailSender emailSender) {
        this.repository = repository;
        this.emailSender = emailSender;
    }


    @Override
    public Optional<EmailValidationToken> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public EmailValidationToken createToken() {
        EmailValidationToken token = new EmailValidationToken();
        SecureRandom secureRandom = new SecureRandom();
        int tokenInt = 100000 + secureRandom.nextInt(900000);
        token.setToken(tokenInt);

        return repository.save(token);
    }

    @Override
    public void sendMassage(EmailValidationToken token) throws MessagingException {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String htmlMsg = "<h1>Ебучий аштиэмэль</h1>";
        //mimeMessage.setContent(htmlMsg, "text/html"); /** Use this or below line **/
        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo("guseff.daniil2011@yandex.ru");
        helper.setSubject("Открой");
        helper.setFrom(from);
        emailSender.send(mimeMessage);

        //SimpleMailMessage message = new SimpleMailMessage();
        //
        //message.setFrom(from);
        //message.setTo("guseff.daniil2011@yandex.ru");
        //message.setSubject("Сраное письмо");
        //message.setText("Наконец-то!");
        //
        //emailSender.send(message);
    }
}
