package com.mck.global.error;

import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 전역 예외 처리 핸들러
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 필수 param 값이 비어있을 경우
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ReturnObject> handleMissingParams(MissingServletRequestParameterException ex) {
        ReturnObject returnObject;
        ErrorObject errorObject;

        log.error("필수 param 값이 비어있습니다.");

        errorObject = ErrorObject.builder().message("필수 param 값이 비어있습니다.").code("missing_param").field(ex.getParameterName()).build();
        returnObject = ReturnObject.builder().success(false).error(errorObject).build();

        return ResponseEntity.ok().body(returnObject);
    }

}
