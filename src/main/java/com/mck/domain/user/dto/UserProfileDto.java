package com.mck.domain.user.dto;

import com.mck.domain.role.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Builder
public class UserProfileDto {
    private Long id;

    private String nickname; // 닉네임

    private String username;

    private Collection<Role> roles = new ArrayList<>();

    private String email;

    private boolean emailVerified; // 이메일 인증 여부

    private LocalDateTime joinedAt; // 로그인한 시간


}
