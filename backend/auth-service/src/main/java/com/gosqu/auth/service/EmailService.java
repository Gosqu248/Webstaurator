package com.gosqu.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void send2FACode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Webstaurator – Kod weryfikacyjny");
        message.setText("Twój kod weryfikacyjny: " + code + "\n\nKod wygasa za 5 minut.");
        mailSender.send(message);
    }

    public void sendPasswordReset(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Webstaurator – Reset hasła");
        message.setText("Zresetuj hasło: " + frontendUrl + "/reset-password?token=" + token
                + "\n\nLink wygasa za 15 minut.");
        mailSender.send(message);
    }
}
