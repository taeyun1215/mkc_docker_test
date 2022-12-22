package com.mck.infra.mail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {

    private String to; // 누구에게 보내는지

    private String subject; // 제목

    private String message; // 내용

    private String code;

}