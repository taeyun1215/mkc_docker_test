package com.mck.global.config;

import com.mck.domain.user.UserRepo;
import com.mck.global.error.RestAccessDeniedHandler;
import com.mck.global.error.RestAuthenticationEntryPoint;
import com.mck.global.error.RestAuthenticationFailureHandler;
import com.mck.global.error.RestSuccessHandler;
import com.mck.global.filter.CustomAuthenticationFilter;
import com.mck.global.filter.CustomAuthorizationFilter;
import com.mck.global.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.http.HttpMethod.GET;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserRepo userRepo;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .mvcMatchers("/favicon.ico", "/static/**", "/templates/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), userRepo);
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(successHandler());
        customAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());

        // csrf 보안 설정 끄기
        // 토큰 방식, 즉 stateless 기반 인증에선 서버에서 인증 정보를 보관하지 않기 때문에
        // csrf 공격에 안전하고 매번 csrf 토큰을 받기 않기 때문에 불필요)
        http.csrf().disable();
        // 스프링 시큐리티가 세션을 생성하지 않고 기존 세션을 사용하지도 않음(JWT 사용을 위함)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler()) // 커스텀 엑세스 거부 핸들러 적용
                .authenticationEntryPoint(authenticationEntryPoint()); // 커스텀 인증 엔트리 포인트 적용
        http.authorizeRequests().antMatchers("/api/login/**", "/api/token/refresh/**", "/api/check-email-code", "/api/user", "/post/**", "/api/username").permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/user/**").hasAnyAuthority("ROLE_USER");
        // http.authorizeRequests().antMatchers(POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().anyRequest().authenticated();
        // jwt 인증을 위한 커스텀 인증 필터 추가
        http.addFilter(customAuthenticationFilter);
        // UsernamePasswordAuthenticationFilter 필터보다 먼저 실행되어야 하므로 Before
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtExceptionFilter(), new CustomAuthorizationFilter().getClass());
        http.httpBasic().disable().cors();

        return http.build();
    }


    // 사용할 인코더 설정
    // Bean으로만 등록해둬도 Spring security가 찾아서 적용해줌
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 외부에서 authenticationManager 적용시켜주기 위해 구현
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    //Cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    RestAccessDeniedHandler accessDeniedHandler() {
        return new RestAccessDeniedHandler();
    }

    @Bean
    RestAuthenticationEntryPoint authenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    RestAuthenticationFailureHandler authenticationFailureHandler() {
        return new RestAuthenticationFailureHandler();
    }

    @Bean
    RestSuccessHandler successHandler() {
        return new RestSuccessHandler();
    }

}
