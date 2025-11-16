package com.newsy.newsy.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine thymeleaf;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Value("${mail.from:no-reply@newsy.local}")
    private String mailFrom;

    @Value("${mail.from.name:${MAIL_FROM_NAME:Newsy}}")
    private String mailFromName;

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        String verifyUrl = appBaseUrl + "/api/subscriptions/verify?token=" + token;

        Context ctx = new Context(Locale.ENGLISH);
        ctx.setVariable("verifyUrl", verifyUrl);
        ctx.setVariable("email", to);

        String html = thymeleaf.process("verification-email", ctx);
        sendHtmlEmail(to, "Confirm your Newsy subscription", html);
    }

    public void sendNewsletter(String to, String subject, Object articles, String token) throws MessagingException {
        log.info("Sending newsletter email to {}", to);
        Context ctx = new Context(Locale.ENGLISH);
        ctx.setVariable("title", subject);
        ctx.setVariable("articles", articles);
        String unsubscribeUrl = appBaseUrl + "/api/subscriptions/unsubscribe?token=" + token;
        ctx.setVariable("unsubscribeUrl", unsubscribeUrl);
        String html = thymeleaf.process("newsletter", ctx);
        sendHtmlEmail(to, subject, html);
    }

    private void sendHtmlEmail(String to, String subject, String html) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);

        try {
            if (mailFromName != null && !mailFromName.isBlank()) {
                helper.setFrom(mailFrom, mailFromName);
            } else {
                helper.setFrom(mailFrom);
            }
        } catch (UnsupportedEncodingException e) {
            // fallback to email-only from
            helper.setFrom(mailFrom);
        }

        helper.setText(html, true);
        mailSender.send(msg);
    }
}
