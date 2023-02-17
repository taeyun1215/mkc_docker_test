package com.mck.global.service;

import com.mck.domain.user.User;
import com.mck.domain.user.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

// SecurityConfig 에서 PasswordEncoder 참조 사이클 에러 발생해서 UserDetailsService 부분만 따로 분리
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepo userRepo;

    // 해당 회원이 존재하는지 여부 체크
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username).get();
        if (user == null){
            log.error("해당 유저 정보를 찾을 수 없습니다");
            throw new UsernameNotFoundException("해당 유저 정보를 찾을 수 없습니다");
        } else{
            log.info("유저 정보를 찾았습니다 : {}", username);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {authorities.add(new SimpleGrantedAuthority(role.getName()));});

        // 동일한 이름의 User 메소드를 이미 import 해둬서 패키지명까지 써서 사용
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
