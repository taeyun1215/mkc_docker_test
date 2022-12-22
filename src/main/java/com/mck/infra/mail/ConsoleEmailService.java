package com.mck.infra.mail;

import com.mck.domain.useremail.UserEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// local profile로 실행시 실제로 메일이 전송되지 않고 콘솔로 찍힘
@Slf4j
@Profile("local")
@Component
public class ConsoleEmailService implements EmailService{
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("send email : {}", emailMessage.getMessage());
    }

    @Override
    public boolean checkCertifyEmail(UserEmail userEmail) {
        return false;
    }
}
