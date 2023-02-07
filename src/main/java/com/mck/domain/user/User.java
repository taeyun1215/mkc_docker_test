package com.mck.domain.user;

import com.mck.domain.role.Role;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = AUTO)
    private Long id;

    @Size(min=3, max=10)
    @Column(nullable = false)
    private String nickname; // 닉네임

    @Size(min=5, max=15)
    @Column(nullable = false, unique = true)
    private String username;

    @Size(max = 300)
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @Size(max=30)
    private Collection<Role> roles = new ArrayList<>();

    @Size(max=100)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified; // 이메일 인증 여부

    private LocalDateTime joinedAt; // 로그인한 시간

    // 회원가입 완료 처리
    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}
