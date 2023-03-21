package com.mck.domain.alert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mck.global.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertAspect {
    private final AlertRepo alertRepo;

//    @Pointcut("execution(* com.mck.domain.user..*(..))")
//    public void apiPointcut() {}
//
//    @Around("apiPointcut()")
//    public void doLogging(ProceedingJoinPoint joinPoint) throws Throwable{
//        // api 실행 전
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String requestUrl = request.getRequestURL().toString();
//        String requestMethod = request.getMethod();
//        String requestParams = new ObjectMapper().writeValueAsString(request.getParameterMap());
//
//        // api 실행
//        ResponseEntity<ReturnObject> response = (ResponseEntity<ReturnObject>) joinPoint.proceed();
//
//        // api 실행 후
//        int statusCodeValue = response.getStatusCodeValue();
//        ReturnObject body = response.getBody();
//
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        ServletWebRequest tt = (ServletWebRequest) RequestContextHolder.getRequestAttributes();
//    }
}
