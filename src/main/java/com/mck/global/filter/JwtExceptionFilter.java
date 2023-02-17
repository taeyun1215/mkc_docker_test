package com.mck.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (JwtException ex) {
            setErrorResponse(request, response, ex);
        }
    }

    public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, Throwable ex) throws IOException {

        ReturnObject returnObject;
        ErrorObject errorObject;
        ArrayList<ErrorObject> errorObjectArrayList = new ArrayList<>();

        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message = "유효하지 않은 토큰";
        String error = "Unauthorized";

        if(ex.getMessage() == "invalid_signature"){
            message = "잘못된 서명";
            error = ex.getMessage();
        } else if(ex.getMessage() == "expired_token"){
            message = "만료된 토큰";
            error = ex.getMessage();
        } else if(ex.getMessage() == "invalid_token"){
            message = "유효하지 않은 토큰";
            error = ex.getMessage();
        }

        errorObject = ErrorObject.builder().message(message).code(error).build();
        errorObjectArrayList.add(errorObject);
        returnObject = ReturnObject.builder().success(false).error(errorObjectArrayList).build();

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(res.getOutputStream(), returnObject);
        res.setStatus(HttpServletResponse.SC_OK);
    }
}
