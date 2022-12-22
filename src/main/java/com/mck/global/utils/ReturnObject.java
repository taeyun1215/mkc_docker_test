package com.mck.global.utils;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

// api 결과값 return 할때 이 객체에 담아서 return
@Builder
@Data
public class ReturnObject {
    @Builder.Default
    private String msg = ""; // 에러 메세지
    @Builder.Default
    private String type = ""; // 에러 타입
    @Builder.Default
    private Object data = new HashMap<>(); // 결과값
}
