package com.mck.global.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 인증
    ALREADY_REGISTERED_MEMBER(400, "이미 가입된 회원 입니다."),
    MISMATCHED_PASSWORD(401, "패스워드가 일치하지 않습니다."),
    MISMATCHED_FORMAT(401, "형식이 맞지 않습니다."),
    LOGIN_ERROR(401, "아이디 또는 비밀번호를 확인해주세요"),
    NOT_EXISTING_ACCOUNT(400, "존재하지 않는 회원 입니다."),

    MISMATCHED_ENTER_PASSWORD(401, "입력한 비밀번호가 기존 비밀번호와 일치하지 않습니다."),
    MISMATCHED_ENTER_NICKNAME(401, "입력한 닉네임이 기존 닉네임이 일치하지 않습니다."),

    // 게시글
    NOT_SAVE_POST(400, "게시글이 저장되지 않았습니다."),
    NOT_EDIT_POST(400, "게시글이 수정되지 않았습니다."),
    NOT_EXIST_POST(400, "게시글이 존재하지 않았습니다."),
    NOT_EDIT_PERMISSION_POST(400, "게시글을 수정할 권한이 없습니다."),
    NOT_DELETE_PERMISSION_POST(400, "게시글을 삭제할 권한이 없습니다."),

    // 댓글
    NOT_SAVE_COMMENT(400, "댓글이 저장되지 않았습니다."),
    NOT_EDIT_COMMENT(400, "댓글이 수정되지 않았습니다."),
    NOT_EXIST_COMMENT(400, "댓글이 존재하지 않았습니다."),
    NOT_EDIT_PERMISSION_COMMENT(400, "댓글을 수정할 권한이 없습니다."),
    NOT_DELETE_PERMISSION_COMMENT(400, "댓글을 삭제할 권한이 없습니다.");

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    private int status;
    private String message;
}
