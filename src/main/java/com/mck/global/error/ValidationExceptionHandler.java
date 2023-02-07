package com.mck.global.error;

import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// valid 검증 에러 핸들링
@ControllerAdvice
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {
    /* response 예제
    {
        "success": false,
        "error": [
            {
                "code": "invalid_pattern",
                "field": "email",
                "message": "goldenplanet.co.kr 이메일만 사용 가능합니다"
            },
            {
                "code": "invalid_size",
                "field": "nickname",
                "message": "닉네임은 3자 이상 10자 이하로 입력해주세요."
            },
            {
                "code": "invalid_pattern",
                "field": "password",
                "message": "비밀번호는 최소 8자 이상 16자 이하, 하나 이상의 문자 및 숫자, 하나 이상의 특수문자가 포함되어야 합니다"
            }
        ]
    }
    */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ErrorObject> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> {
                    return ErrorObject.builder().code(getCodeName(e.getCode())).message(e.getDefaultMessage()).field(e.getField()).build();
                })
                .collect(Collectors.toList());

        ArrayList error_list = new ArrayList(errors);

        ReturnObject object = ReturnObject.builder().success(false).error(error_list).build();

        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    private String getCodeName(String code){
        String code_name = "invalid_parameter";
        switch (code){
            case "Pattern":
                code_name = "invalid_pattern";
                break;
            case "Size":
                code_name = "invalid_size";
                break;
        }
        return code_name;
    }

}
