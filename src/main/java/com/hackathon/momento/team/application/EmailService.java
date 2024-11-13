package com.hackathon.momento.team.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailService {

    private static final String FROM_EMAIL = "kduoh99@gmail.com";
    private static final String TEAM_NAME = "TeamUp";

    private final JavaMailSender mailSender;

    public void sendMessage(String to, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            String subject = "[TeamUp] 요청하신 팀 빌딩이 완료되었습니다, 팀 정보를 확인해주세요.";
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(FROM_EMAIL, TEAM_NAME);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            mailSender.send(message);
            log.info("Email sent to: {}", to);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Email send failed to {}", to, e);
        }
    }
}
