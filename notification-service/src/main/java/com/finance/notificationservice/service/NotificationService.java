package com.finance.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    private final JavaMailSender emailSender;

    @Autowired
    public NotificationService(JavaMailSender emailSender){
        this.emailSender = emailSender;
    }

    public void sendSimpleMessage(String to, String subject, String from, String text) {
        log.info("Send Mail Notification to User");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
