package com.mck.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

// 로그인에 실패했을 때 에러 처리를 담당하는 handler
@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler
{
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse httpServletResponse,
                                        AuthenticationException ex) throws IOException, ServletException
    {
        ErrorObject errorObject = ErrorObject.builder().code("notfound_user").message("해당 유저 정보를 찾을 수 없습니다").build();
        ReturnObject returnObject = ReturnObject.builder().success(false).error(errorObject).build();

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        OutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, returnObject);
        out.flush();
    }
}