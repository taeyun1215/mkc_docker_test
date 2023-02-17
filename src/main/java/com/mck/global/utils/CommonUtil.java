package com.mck.global.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mck.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class CommonUtil {
    private static String SECRET_KEY;
    private static int TOKEN_EXPIRES_TIME;
    private static int REFRESH_TOKEN_EXPIRES_TIME;

    @Value("${property.secretKey}")
    public void setSecretKey(String value){
        SECRET_KEY = value;
    }

    @Value("${property.tokenExpiresTime}")
    public void setTokenExpiresTime(int value){
        TOKEN_EXPIRES_TIME = value;
    }

    @Value("${property.refreshTokenExpiresTime}")
    public void setRefreshTokenExpiresTime(int value){
        REFRESH_TOKEN_EXPIRES_TIME = value;
    }

    // 랜덤 숫자 생성기
    public static String getRandomNumber(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int n = (int) (Math.random() * 10);
            buffer.append(n);
        }
        return buffer.toString();
    }

    public static Map<String, Object> getToken(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        // 토큰 서명용 키 생성
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
        // 최초 접속시 발급하는 토큰
        String access_token = JWT.create()
                // 토큰 이름
                .withSubject(user.getUsername())
                // 토큰 만료일
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRES_TIME * 60 * 1000))
                // 토큰 발행자
                .withIssuer(request.getRequestURI().toString())
                // 토큰 payload 작성
                .withClaim("roles", authenticationToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                // 토큰 서명
                .sign(algorithm);

        // access_token을 재발급 받을 수 있는 토큰
        String refresh_token = JWT.create()
                // 토큰 이름
                .withSubject(user.getUsername())
                // 토큰 만료일
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRES_TIME * 60 * 1000))
                // 토큰 발행자
                .withIssuer(request.getRequestURI().toString())
                // 토큰 서명
                .sign(algorithm);

        Map<String, Object> token = new HashMap<>();
        token.put("access_token", access_token);
        token.put("refresh_token", refresh_token);
        return token;
    }

}
