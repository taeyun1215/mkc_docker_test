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

        // csrf ?????? ?????? ??????
        // ?????? ??????, ??? stateless ?????? ???????????? ???????????? ?????? ????????? ???????????? ?????? ?????????
        // csrf ????????? ???????????? ?????? csrf ????????? ?????? ?????? ????????? ?????????)
        http.csrf().disable();
        // ????????? ??????????????? ????????? ???????????? ?????? ?????? ????????? ??????????????? ??????(JWT ????????? ??????)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler()) // ????????? ????????? ?????? ????????? ??????
                .authenticationEntryPoint(authenticationEntryPoint()); // ????????? ?????? ????????? ????????? ??????
        http.authorizeRequests().antMatchers("/api/login/**", "/api/token/refresh/**", "/api/check-email-code*", "/api/user", "/post/**", "/api/username", "/api/password*").permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/user/**").hasAnyAuthority("ROLE_USER");
        // http.authorizeRequests().antMatchers(POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().anyRequest().authenticated();
        // jwt ????????? ?????? ????????? ?????? ?????? ??????
        http.addFilter(customAuthenticationFilter);
        // UsernamePasswordAuthenticationFilter ???????????? ?????? ??????????????? ????????? Before
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtExceptionFilter(), new CustomAuthorizationFilter().getClass());
        http.httpBasic().disable().cors();

        return http.build();
    }


    // ????????? ????????? ??????
    // Bean????????? ??????????????? Spring security??? ????????? ????????????
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ???????????? authenticationManager ?????????????????? ?????? ??????
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    //Cors ??????
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
