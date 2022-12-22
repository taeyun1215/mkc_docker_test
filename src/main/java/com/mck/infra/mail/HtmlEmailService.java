package com.mck.infra.mail;

import com.mck.domain.useremail.UserEmail;
import com.mck.domain.useremail.UserEmailRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;


// 실제로 인증 메일 발송
@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService{

    private final JavaMailSender javaMailSender;

    private final UserEmailRepo userEmailRepo;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        UserEmail userEmail = UserEmail.builder().email(emailMessage.getTo()).code(emailMessage.getCode()).build();

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), true);
            javaMailSender.send(message);
            log.info("send email: {}", emailMessage.getMessage());
        } catch (MessagingException e) {
            log.error("failed to send email : ", e);
        }

        try {
            userEmailRepo.save(userEmail);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public boolean checkCertifyEmail(UserEmail obj) {
        Optional<UserEmail> user = userEmailRepo.findByEmail(obj.getEmail());
        if (user.isEmpty()) {
            return false;
        }
        UserEmail userEmail = user.get();
        if (!userEmail.getCode().equals(obj.getCode())) {
            return false;
        }
        return true;
    }
}
