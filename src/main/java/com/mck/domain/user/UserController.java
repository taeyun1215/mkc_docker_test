package com.mck.domain.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mck.domain.role.Role;
import com.mck.domain.user.dto.UserResetPasswordDto;
import com.mck.domain.user.dto.UserSignUpDto;
import com.mck.domain.useremail.UserEmail;
import com.mck.global.utils.CommonUtil;
import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import com.mck.global.utils.SignUpFormValidator;
import com.mck.infra.mail.EmailMessage;
import com.mck.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.mck.global.utils.CommonUtil.getRandomNumber;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class UserController {

    @Value("${property.secretKey}")
    private String secretKey;

    @Value("${environments.dev.url}")
    private String host;

    private final UserService userService;
    private final EmailService emailService;
    private final SignUpFormValidator signUpFormValidator;
    private final TemplateEngine templateEngine;
    private final CommonUtil commonUtil;

    private final PasswordEncoder passwordEncoder;

    @InitBinder("userSignUpDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    // ?????? ??????
    @PostMapping("/user")
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserSignUpDto userSignUpDto, HttpServletRequest request, Errors errors) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user").toUriString());

        ReturnObject returnObject;
        ErrorObject errorObject;

        if (!StringUtils.equals(userSignUpDto.getPassword(), userSignUpDto.getConfirmPassword())) {
            log.error("????????????");

            errorObject = ErrorObject.builder().code("different_confirmPassword").message("??????????????? ???????????? ????????? ???????????? ????????????.").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }

        if (errors.hasErrors()) {
            System.out.println("????????????");

            errorObject = ErrorObject.builder().code(errors.getFieldError().getCode()).message(errors.getFieldError().getDefaultMessage()).build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        } else {
            User user = userService.newUser(userSignUpDto);
            User saveUser = userService.saveUser(user);

            Map<String, Object> token = commonUtil.getToken(user, request);

            returnObject = ReturnObject.builder().success(true).data(token).build();


            return ResponseEntity.ok().body(returnObject);
        }
    }

    // ????????? ?????? ??????
    @PostMapping("/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    // ?????? ?????????
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        ReturnObject returnObject;
        ErrorObject errorObject;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // ????????? ?????? ????????? type?????? ??????
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
                // JWT ????????? ?????? ??????(?????? ??????????????? ????????? ???????????? ??????)
                JWTVerifier verifier = JWT.require(algorithm).build();
                // ?????? ??????
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                Map<String, Object> token = commonUtil.getToken(user, request);

                returnObject = ReturnObject.builder().success(true).data(token).build();

                response.setContentType(APPLICATION_JSON_VALUE);

                new ObjectMapper().writeValue(response.getOutputStream(), returnObject);
            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                response.setStatus(OK.value());
                // response.sendError(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());

                errorObject = ErrorObject.builder().code("invalid_token").message(e.getMessage()).build();
                returnObject = ReturnObject.builder().success(false).error(errorObject).build();

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), returnObject);
            }
        } else {
            throw new RuntimeException("Refresh ????????? ????????????.");
        }
    }

    // ????????????
    @DeleteMapping("/user")
    public ResponseEntity<ReturnObject> deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        ReturnObject returnObject;
        ErrorObject errorObject;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String username = decodedJWT.getSubject();
                userService.deleteUser(username);

                returnObject = ReturnObject.builder().success(true).build();

                return ResponseEntity.ok().body(returnObject);

            } catch (Exception e) {
                errorObject = ErrorObject.builder().message(e.getMessage()).code(APPLICATION_JSON_VALUE).build();
                returnObject = ReturnObject.builder().success(false).error(errorObject).build();

                return ResponseEntity.ok().body(returnObject);
            }
        } else {
            errorObject = ErrorObject.builder().message("????????? ????????? ???????????? ?????? ???????????????.").code("invalid_token").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();
            return ResponseEntity.ok().body(returnObject);
        }
    }

    // ?????? ?????? ??????
    @GetMapping("/check-email-code")
    public ResponseEntity<ReturnObject> checkEmailCode(String code, String email, String username, Model model) {
        User user = userService.getUser(username);
        ReturnObject returnObject;
        ErrorObject errorObject;
        if(user == null){
            errorObject = ErrorObject.builder().message("????????? ?????? ????????? ???????????? ????????????.").code("wrong_username").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }

        UserEmail userEmail = UserEmail.builder().email(email).code(code).build();

        if (!emailService.checkCertifyEmail(userEmail)) {
            errorObject = ErrorObject.builder().message("?????? ????????? ????????????.").code("wrong_code").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();
            return ResponseEntity.badRequest().body(returnObject);
        }

        user.completeSignUp();
        user.setJoinedAt(LocalDateTime.now());
        userService.updateUser(user);

        model.addAttribute("username", username);

        returnObject = ReturnObject.builder().success(true).data(model).build();

        return ResponseEntity.ok().body(returnObject);

    }

    // ????????? id ??????
    // ???????????? ???????????? ?????? ???????????? ????????? ????????? ?????? ????????? ???????????? ????????? ???????????? ????????? ??????
    @GetMapping("/username")
    public ResponseEntity<ReturnObject> findUsername(@RequestParam String email) {
        User result = userService.checkUserEmail(email);
        ReturnObject returnObject;
        ErrorObject errorObject;
        if (result != null){
            Context context = new Context();
            context.setVariable("link", "/api/username");
            context.setVariable("username", result.getUsername());
            String message = templateEngine.process("mail/findUsernameForm", context);

            EmailMessage emailMessage = EmailMessage.builder()
                    .to(email)
                    .subject("MCK ????????????, ????????? ??????")
                    .message(message)
                    .build();

            emailService.sendEmail(emailMessage);

            ReturnObject object = ReturnObject.builder().success(true).build();

            return ResponseEntity.ok().body(object);

        } else{
            log.error("?????? ???????????? ????????? ????????? ?????? ??? ????????????.");

            errorObject = ErrorObject.builder().message("?????? ???????????? ????????? ????????? ?????? ??? ????????????.").code("invalid_email").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }
    }

    // ???????????? ??????(???????????? ?????????)
    // ???????????? ???????????? ?????? ???????????? ????????? ????????? ?????? ????????? ???????????? ????????? ???????????? ???????????? ????????? ?????? ??????
    @GetMapping("/password")
    public ResponseEntity<ReturnObject> findPassword(@RequestParam String email) {
        User result = userService.checkUserEmail(email);
        ReturnObject returnObject;
        ErrorObject errorObject;
        if (result != null){
            String code = getRandomNumber(6);

            Context context = new Context();
            context.setVariable("link", "/api/password?" +
                    "checkCode=" + code +
                    "&email=" + email
            );
            context.setVariable("host", host);
            String message = templateEngine.process("mail/findPasswordForm", context);

            EmailMessage emailMessage = EmailMessage.builder()
                    .to(email)
                    .subject("MCK ????????????, ???????????? ?????????")
                    .message(message)
                    .build();

            emailService.sendEmail(emailMessage);

            ReturnObject object = ReturnObject.builder().success(true).build();

            return ResponseEntity.ok().body(object);

        } else{
            log.error("?????? ???????????? ????????? ????????? ?????? ??? ????????????.");

            errorObject = ErrorObject.builder().message("?????? ???????????? ????????? ????????? ?????? ??? ????????????.").code("invalid_email").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }
    }

    // ?????? ?????? ?????? ??? ???????????? ?????????
    @GetMapping("/reset-password")
    public ResponseEntity<ReturnObject> resetPassword(UserResetPasswordDto dto, Model model) {
        User user = userService.checkUserEmail(dto.getEmail());

        ReturnObject returnObject;
        ErrorObject errorObject;
        if (!StringUtils.equals(dto.getPassword(), dto.getConfirmPassword())) {
            log.error("????????????");

            errorObject = ErrorObject.builder().code("different_confirmPassword").message("??????????????? ???????????? ????????? ???????????? ????????????.").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }

        if(user == null){
            errorObject = ErrorObject.builder().message("????????? ?????? ????????? ???????????? ????????????.").code("wrong_username").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }

        UserEmail userEmail = UserEmail.builder().email(dto.getEmail()).code(dto.getCode()).build();

        if (!emailService.checkCertifyEmail(userEmail)) {
            errorObject = ErrorObject.builder().message("?????? ????????? ????????????.").code("wrong_code").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();
            return ResponseEntity.badRequest().body(returnObject);
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userService.updateUser(user);

        returnObject = ReturnObject.builder().success(true).build();

        return ResponseEntity.ok().body(returnObject);

    }

    @PutMapping("/user")
    public ResponseEntity<ReturnObject> updateUser(@AuthenticationPrincipal String username){
        return null;
    }
}
