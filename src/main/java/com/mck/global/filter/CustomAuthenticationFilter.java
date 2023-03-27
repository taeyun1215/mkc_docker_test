package com.mck.global.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mck.domain.user.UserRepo;
import com.mck.global.utils.CommonUtil;
import com.mck.global.utils.CookieUtil;
import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// Form based 인증 방식을 사용할 때 사용되는 상속한 커스텀 인증 필터
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // 유저 인증을 담당할 인터페이스
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;

    // 원하는 시점에서 로그인 하기위해 authenticationManager를 외부에서 주입받음
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, UserRepo userRepo){
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
    }

    // 유저 인증
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Username is : {}", username);
        log.info("Password is : {}", password);

        // 요청에서 받아온 유저 정보로 토큰 발급
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 토큰으로 인증 수행하고 결과 반환
        return authenticationManager.authenticate(authenticationToken);
    }

    // 인증 성공시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        com.mck.domain.user.User user_domain = new com.mck.domain.user.User();
        user_domain.setUsername(user.getUsername());
        user_domain.setPassword(user.getPassword());

        Map<String, Object> token = CommonUtil.getToken(user_domain, request);

        com.mck.domain.user.User userDetail = userRepo.findByUsername(user.getUsername()).get();

        Map<String, Object> resultObject = new HashMap<>();

        resultObject.put("access_token", token.get("access_token"));
        resultObject.put("emailVerified", userDetail.isEmailVerified());
        resultObject.put("nickname", userDetail.getNickname());

        String encodedValue = URLEncoder.encode("Bearer " + (String) token.get("refresh_token"), "UTF-8" ) ;

//                Cookie localCookie = new Cookie("refresh_token", encodedValue);
//                localCookie.setDomain("localhost");
//                cookie.setSecure(true);
//                cookie.setHttpOnly(true);
//                localCookie.setPath("/");

//                response.addCookie(localCookie);

        CookieUtil.addCookie(response, "refresh_token", encodedValue);

        Cookie domainCookie = new Cookie("refresh_token", encodedValue);
        domainCookie.setDomain("www.devyeh.com");
//                cookie.setSecure(true);
//                cookie.setHttpOnly(true);
        domainCookie.setPath("/");

        response.addCookie(domainCookie);

        response.setContentType(APPLICATION_JSON_VALUE);
        ReturnObject returnObject = ReturnObject.builder().success(true).data(resultObject).build();
        new ObjectMapper().writeValue(response.getOutputStream(), returnObject);
    }
}