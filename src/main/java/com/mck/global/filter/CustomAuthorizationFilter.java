package com.mck.global.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// OncePerRequestFilter - filter chain을 거치며 중복 실행되는것을 방지하기 위해 요청당 1번만 실행되는 필터
// 사용자의 모든 요청을 가로채고 토큰을 찾아 처리한 다음 사용자가 특정 리소스에 액세스 할 수 있는지 여부를 결정하는 필터
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 사용자가 로그인을 시도한다면
        if(request.getServletPath().equals("/login") || request.getServletPath().equals("/user/token/refresh") ){
            // 필터체인을 호출
            filterChain.doFilter(request, response);
        } else {
            // 헤더에서 Authorization 키 찾음
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            // 토큰 기반 인증시 요청 헤더에 Authorization : <type> <token>의 구성을 취하는 것이 기본 포멧
            // Bearer는 JWT or OAuth에 대한 토큰을 사용하는 방식
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                try {
                    // 토크만 추출 하도록 type부분 제거
                    String token = authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm = Algorithm.HMAC256("pvxmdnogszqqakzssfvvivldk".getBytes());
                    // JWT 검증용 객체 생성(토큰 생성할때와 동일한 알고리즘 적용)
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    // 토큰 검증
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    // roles안에 있는 권한들을 SimpleGrantedAuthority 객체로 만들어 리턴
                    stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    // 인증된 유저 정보를 Authentication 객체에 등록
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    // 나머지 필터가 계속 진행되도록 필터체인 호출
                    filterChain.doFilter(request, response);

                } catch (SignatureVerificationException e) {
                    log.info("Invalid JWT signature");
                    throw new JwtException("invalid_signature");
                } catch (TokenExpiredException e) {
                    log.info("Expired JWT token");
                    throw new JwtException("expired_token");
                } catch (InvalidClaimException e){
                    log.info("Invalid JWT token");
                    throw new JwtException("invalid_token");
                } catch (Exception e) {
                    log.error("로그인 에러 : {}", e.getMessage());
                    throw new JwtException("invalid_token");
                }
            } else {
                // 토큰 정보가 없으면 다음 필터 진행
                filterChain.doFilter(request, response);
            }
        }
    }
}
