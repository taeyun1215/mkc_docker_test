package com.mck.global.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    // 원하는 시점에서 로그인 하기위해 authenticationManager를 외부에서 주입받음
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
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
        // 토큰 서명용 키 생성
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        // 최초 접속시 발급하는 토큰
        String access_token = JWT.create()
                // 토큰 이름
                .withSubject(user.getUsername())
                // 토큰 만료일
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                // .withExpiresAt(new Date(System.currentTimeMillis() + 15 * 1000))
                // 토큰 발행자
                .withIssuer(request.getRequestURI().toString())
                // 토큰 payload 작성
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                // 토큰 서명
                .sign(algorithm);

        // access_token을 재발급 받을 수 있는 토큰
        String refresh_token = JWT.create()
                // 토큰 이름
                .withSubject(user.getUsername())
                // 토큰 만료일
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                // 토큰 발행자
                .withIssuer(request.getRequestURI().toString())
                // 토큰 서명
                .sign(algorithm);

        Map<String, String> token = new HashMap<>();
        token.put("access_token", access_token);
        token.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), token);
    }
}