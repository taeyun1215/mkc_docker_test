package com.mck.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

// 401(UnAuthorized) 인증 에러가 발생했을 때 처리해주는 로직
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 인증 에러가 발생했을 때 실행되는 메소드
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {

        ReturnObject returnObject;
        ErrorObject errorObject;

        errorObject = ErrorObject.builder().code("401").message("인증 에러가 발생했습니다.").build();
        returnObject = ReturnObject.builder().success(false).error(errorObject).build();

//        Map<String,Object> response = new HashMap<>();
//        response.put("status","34");
//        response.put("message","unauthorized access");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        OutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, returnObject);
        out.flush();
    }
}