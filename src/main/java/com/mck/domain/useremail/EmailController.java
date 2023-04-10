package com.mck.domain.useremail;

import com.mck.domain.user.User;
import com.mck.domain.user.UserService;
import com.mck.global.utils.ErrorObject;
import com.mck.infra.mail.EmailMessage;
import com.mck.infra.mail.EmailService;
import com.mck.global.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mck.global.utils.CommonUtil.getRandomNumber;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    @Value("${environments.dev.url}")
    private String host;

    private final TemplateEngine templateEngine;
    private final EmailService service;

    private final UserService userService;

    @PostMapping("/certify-regis")
    public ResponseEntity<ReturnObject> certifyUser(@AuthenticationPrincipal String username, HttpServletRequest request){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/email/certify-regis").toUriString());

        if (username == null) {
            ErrorObject error = ErrorObject.builder().message("유저 정보가 없습니다.").code("notfound_user").build();
            ArrayList<ErrorObject> errors = new ArrayList<>();
            errors.add(error);
            ReturnObject object = ReturnObject.builder().success(false).error(errors).build();
            return ResponseEntity.ok().body(object);
        }

        User user = userService.getUser(username);

        String code = getRandomNumber(6);

        Context context = new Context();
        context.setVariable("link", "/user/" +
                "authComplete?code=" + code +
                "&username=" + user.getUsername() + "&email=" + user.getEmail());
        context.setVariable("username", user.getUsername());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "버튼을 클릭하시면 이메일 인증 및 회원가입이 완료됩니다.");
        context.setVariable("host", host);
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject("YEH 프로젝트, 회원 가입 인증")
                .message(message)
                .code(code)
                .build();

        service.sendEmail(emailMessage);
//        ReturnObject object = ReturnObject.builder()
//                .msg("ok").data("/api/check-email-code?code=" + code +
//                        "&username=" + user.getUsername() + "&email=" + user.getEmail()).build();

        ReturnObject object = ReturnObject.builder().success(true).build();

        return ResponseEntity.ok().body(object);
    }
}
