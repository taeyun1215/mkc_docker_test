package com.mck.domain.alert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mck.domain.comment.Comment;
import com.mck.domain.comment.CommentRepo;
import com.mck.global.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertAspect {
    private final AlertRepo alertRepo;

    private final CommentRepo commentRepo;

    @Pointcut("execution(* com.mck.domain.comment.CommentController..saveComment(..))")
    public void commentPointcut() {}
    @Pointcut("execution(* com.mck.domain.comment.CommentController..saveReComment(..))")
    public void replyPointcut() {}

    @Around("commentPointcut()")
    public ResponseEntity<ReturnObject> commentAlert(ProceedingJoinPoint joinPoint) throws Throwable{
        // api 실행 전
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURL().toString();

        String post_id = null;

        Pattern regex = Pattern.compile("[\\w-]+$");

        Matcher regexMatcher = regex.matcher(requestUrl);

        if (regexMatcher.find()) {
            post_id = regexMatcher.group();
        }


        String url = "/post/read?id=" + post_id;

        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, String[]> parameterMap = request.getParameterMap();

        String content = parameterMap.get("content")[0];

        // api 실행
        ResponseEntity<ReturnObject> response = (ResponseEntity<ReturnObject>) joinPoint.proceed();

        if (response.getBody().isSuccess()){
//            String now_date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Alert alert = Alert.builder().username(principal).type("comment").url(url).message(content).confirm(0).createdAt(LocalDateTime.now()).build();
            alertRepo.save(alert);
        }

        return response;
    }

    @Around("replyPointcut()")
    public ResponseEntity<ReturnObject> replyAlert(ProceedingJoinPoint joinPoint) throws Throwable{
        // api 실행 전
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURL().toString();

        String[] requestUrlList = requestUrl.split("/");
        String post_id = requestUrlList[requestUrlList.length - 2];
        String comment_id = requestUrlList[requestUrlList.length - 1];

        String url = "/post/read?id=" + post_id;

        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, String[]> parameterMap = request.getParameterMap();

        String content = parameterMap.get("content")[0];

        // api 실행
        ResponseEntity<ReturnObject> response = (ResponseEntity<ReturnObject>) joinPoint.proceed();

        if (response.getBody().isSuccess()){
//            String now_date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Comment commentObj = commentRepo.findById(Long.parseLong(comment_id)).get();
            Alert alert = Alert.builder().username(commentObj.getUser().getUsername()).type("reply").url(url).message(content).confirm(0).createdAt(LocalDateTime.now()).build();
            alertRepo.save(alert);
        }

        return response;
    }
}
