package com.mck.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor // 테스트 코드 작성용
public class UserSignUpDto {

    @NotBlank
    @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank
    @Pattern(regexp = "^[a-z]+[a-z0-9]{5,15}$",
            message = "아이디는 최소 5자 이상, 15자 이하여야 하고 영어로 시작하는 영어+숫자 조합으로 구성되어야 합니다")
    private String username;

    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 최소 8자 이상 16자 이하, 하나 이상의 문자 및 숫자, 하나 이상의 특수문자가 포함되어야 합니다")
    private String password;

    @NotBlank(message = "비밀번호확인은 필수 입력 값입니다.")
    private String confirmPassword;

    @NotBlank
    @Email(message = "올바른 이메일 형식으로 입력해주세요")
    @Pattern(regexp = "^[a-z0-9]+@goldenplanet.co.kr$",
            message = "goldenplanet.co.kr 이메일만 사용 가능합니다")
    private String email;

}
