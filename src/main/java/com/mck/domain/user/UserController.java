package com.mck.domain.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mck.domain.role.Role;
import com.mck.domain.user.dto.*;
import com.mck.domain.useremail.UserEmail;
import com.mck.global.utils.*;
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
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mck.global.utils.CommonUtil.getRandomNumber;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
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

    // 유저 등록
    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserSignUpDto userSignUpDto, HttpServletRequest request, Errors errors) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user").toUriString());

        ReturnObject returnObject;
        ErrorObject errorObject;

        if (!StringUtils.equals(userSignUpDto.getPassword(), userSignUpDto.getConfirmPassword())) {
            log.error("검증실패");

            errorObject = ErrorObject.builder().code("different_confirmPassword").message("비밀번호와 비밀번호 확인이 일치하지 않습니다.").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }

        if (errors.hasErrors()) {
            System.out.println("검증실패");

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

    // 토큰 재발급
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String authorizationHeader = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh_token")) {
                authorizationHeader = cookie.getValue();
            }
        }

        ReturnObject returnObject;
        ErrorObject errorObject;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer+")) {
            try {
                // 토크만 추출 하도록 type부분 제거
                String refresh_token = authorizationHeader.substring("Bearer+".length());
                Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
                // JWT 검증용 객체 생성(토큰 생성할때와 동일한 알고리즘 적용)
                JWTVerifier verifier = JWT.require(algorithm).build();
                // 토큰 검증
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                Map<String, Object> token = commonUtil.getToken(user, request);

                Map<String, Object> resultObject = new HashMap<>();

                resultObject.put("access_token", token.get("access_token"));

                String encodedValue = URLEncoder.encode("Bearer " + (String) token.get("refresh_token"), "UTF-8" ) ;

//                Cookie localCookie = new Cookie("refresh_token", encodedValue);
//                localCookie.setDomain("localhost");
//                cookie.setSecure(true);
//                cookie.setHttpOnly(true);
//                localCookie.setPath("/");

//                response.addCookie(localCookie);

                Cookie domainCookie = new Cookie("refresh_token", encodedValue);
                domainCookie.setDomain("www.devyeh.com");
                domainCookie.setSecure(true);
//                cookie.setHttpOnly(true);
                domainCookie.setPath("/");

                response.addCookie(domainCookie);

                CookieUtil.addCookie(response, "refresh_token", encodedValue);

                returnObject = ReturnObject.builder().success(true).data(resultObject).build();
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
            throw new RuntimeException("Refresh 토큰이 없습니다.");
        }
    }

    // 회원탈퇴
    @DeleteMapping
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
            errorObject = ErrorObject.builder().message("토큰이 없거나 올바르지 않은 토큰입니다.").code("invalid_token").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();
            return ResponseEntity.ok().body(returnObject);
        }
    }

    // 인증 메일 확인
    @GetMapping("/check-email-code")
    public ResponseEntity<ReturnObject> checkEmailCode(String code, String email, String username, Model model) {
        User user = userService.getUser(username);
        ReturnObject returnObject;
        ErrorObject errorObject;
        if(user == null){
            errorObject = ErrorObject.builder().message("이메일 확인 링크가 정확하지 않습니다.").code("wrong_username").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }

        UserEmail userEmail = UserEmail.builder().email(email).code(code).build();

        if (!emailService.checkCertifyEmail(userEmail)) {
            errorObject = ErrorObject.builder().message("인증 코드가 틀립니다.").code("wrong_code").build();
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

    // 사용자 id 찾기
    // 이메일을 입력하면 해당 이메일로 가입된 계정을 찾고 계정이 존재하면 입력한 이메일로 아이디 전달
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
                    .subject("YEH 프로젝트, 아이디 찾기")
                    .message(message)
                    .build();

            emailService.sendEmail(emailMessage);

            ReturnObject object = ReturnObject.builder().success(true).build();

            return ResponseEntity.ok().body(object);

        } else{
            log.error("해당 이메일로 가입된 계정을 찾을 수 없습니다.");

            errorObject = ErrorObject.builder().message("해당 이메일로 가입된 계정을 찾을 수 없습니다.").code("invalid_email").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }
    }

    // 비밀번호 찾기(비밀번호 초기화)
    // 이메일을 입력하면 해당 이메일로 가입된 계정을 찾고 계정이 존재하면 입력한 이메일로 비밀번호 초기화 링크 전달
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
                    .subject("YEH 프로젝트, 비밀번호 재설정")
                    .message(message)
                    .build();

            emailService.sendEmail(emailMessage);

            ReturnObject object = ReturnObject.builder().success(true).build();

            return ResponseEntity.ok().body(object);

        } else{
            log.error("해당 이메일로 가입된 계정을 찾을 수 없습니다.");

            errorObject = ErrorObject.builder().message("해당 이메일로 가입된 계정을 찾을 수 없습니다.").code("invalid_email").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }
    }

    // 인증 메일 확인 후 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<ReturnObject> resetPassword(UserResetPasswordDto dto, Model model) {
        User user = userService.checkUserEmail(dto.getEmail());

        ReturnObject returnObject;
        ErrorObject errorObject;
        if (!StringUtils.equals(dto.getPassword(), dto.getConfirmPassword())) {
            log.error("검증실패");

            errorObject = ErrorObject.builder().code("different_confirmPassword").message("비밀번호와 비밀번호 확인이 일치하지 않습니다.").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }

        if(user == null){
            errorObject = ErrorObject.builder().message("이메일 확인 링크가 정확하지 않습니다.").code("wrong_username").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        }

        UserEmail userEmail = UserEmail.builder().email(dto.getEmail()).code(dto.getCode()).build();

        if (!emailService.checkCertifyEmail(userEmail)) {
            errorObject = ErrorObject.builder().message("인증 코드가 틀립니다.").code("wrong_code").build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();
            return ResponseEntity.badRequest().body(returnObject);
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userService.updateUser(user);

        returnObject = ReturnObject.builder().success(true).build();

        return ResponseEntity.ok().body(returnObject);

    }

    // 유저 상세정보
    @GetMapping("/profile")
    public ResponseEntity<ReturnObject> findUserDetail(@AuthenticationPrincipal String username){
        ReturnObject returnObject;
        ErrorObject errorObject;

        User user = userService.getUser(username);

        if(user == null){
            ErrorObject error = ErrorObject.builder().message("유저 정보가 없습니다.").code("notfound_user").build();
            ArrayList<ErrorObject> errors = new ArrayList<>();
            errors.add(error);
            ReturnObject object = ReturnObject.builder().success(false).error(errors).build();
            return ResponseEntity.ok().body(object);
        }

        UserProfileDto userProdile = UserProfileDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .build();

        returnObject = ReturnObject.builder().success(true).data(userProdile).build();

        return ResponseEntity.ok().body(returnObject);
    }

    @PutMapping
    public ResponseEntity<ReturnObject> updateUser(@AuthenticationPrincipal String username, UserUpdateDto dto){
        ReturnObject returnObject;
        ErrorObject errorObject;

        User user = userService.getUser(username);

        if(user == null){
            ErrorObject error = ErrorObject.builder().message("유저 정보가 없습니다.").code("notfound_user").build();
            ArrayList<ErrorObject> errors = new ArrayList<>();
            errors.add(error);
            ReturnObject object = ReturnObject.builder().success(false).error(errors).build();
            return ResponseEntity.ok().body(object);
        }

        if(dto.getEmail() != null){
            user.setEmail(dto.getEmail());
        }
        if(dto.getNickname() != null){
            user.setNickname(dto.getNickname());
        }
        userService.updateUser(user);

        returnObject = ReturnObject.builder().success(true).build();

        return ResponseEntity.ok().body(returnObject);
    }
//
//    @GetMapping("/test")
//    public ResponseEntity<ReturnObject> aopTest(){
//        log.info("테스트 성공!");
//        ReturnObject returnObject;
//        Map<String, String> hello = new HashMap<>();
//        hello.put("test", "testValue");
//        returnObject = ReturnObject.builder().success(true).data(hello).build();
//        return ResponseEntity.ok().body(returnObject);
//    }

    // 비밀번호 변경(프로필)
    @PostMapping("/change-password")
    public ResponseEntity<ReturnObject> changePassword(@AuthenticationPrincipal String username, @RequestBody UserChangePasswordDto dto){
        ReturnObject returnObject;
        ErrorObject errorObject;
        if (!dto.getPassword().equals(dto.getConfirmPassword())){
            errorObject = ErrorObject.builder().message("비밀번호와 비밀번호 확인이 일치하지 않습니다.").code("notmatch_password").build();
            ArrayList<ErrorObject> errors = new ArrayList<>();
            errors.add(errorObject);
            ReturnObject object = ReturnObject.builder().success(false).error(errors).build();
            return ResponseEntity.ok().body(object);
        }
        User user = userService.getUser(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userService.updateUser(user);

        returnObject = ReturnObject.builder().success(true).build();

        return ResponseEntity.ok().body(returnObject);
    }
}
