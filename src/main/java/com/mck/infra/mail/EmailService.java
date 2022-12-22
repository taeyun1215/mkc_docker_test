package com.mck.infra.mail;

import com.mck.domain.useremail.UserEmail;

public interface EmailService {

    // Email 전송
    void sendEmail(EmailMessage emailMessage);

    // Email 인증 확인
    boolean checkCertifyEmail(UserEmail userEmail);
}