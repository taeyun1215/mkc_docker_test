package com.mck.global.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://www.devyeh.com")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST")
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true);
    }
}