package com.mck.global.utils;

import org.springframework.http.ResponseCookie;

import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .sameSite("None")
                .domain("localhost")
//                .httpOnly(false)
                .secure(true)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

}
