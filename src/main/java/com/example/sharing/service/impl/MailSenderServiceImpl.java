package org.example.sharing.service.impl;

import org.example.sharing.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String hostMail;

    private final static String SUBJECT = "Flat-renta notification";

    @Override
    public void sendMessage(String mail, String text) {
        SimpleMailMessage messageSender = new SimpleMailMessage();
        messageSender.setFrom(hostMail);
        messageSender.setTo(mail);
        messageSender.setSubject(SUBJECT);
        messageSender.setText(text);
        mailSender.send(messageSender);
    }
}
