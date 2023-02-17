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
public class UserUpdateDto {

    @Size(min=3, max=10, message = "닉네임은 3자 이상 10자 이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]*$",
            message = "닉네임은 영문, 숫자, 한글만 가능합니다.")
    private String nickname;

    @Email(message = "올바른 이메일 형식으로 입력해주세요")
    @Pattern(regexp = "^[a-z0-9]+@goldenplanet.co.kr$",
            message = "goldenplanet.co.kr 이메일만 사용 가능합니다")
    private String email;

}
