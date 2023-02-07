package com.mck.domain.user;

import com.mck.domain.role.Role;
import com.mck.domain.user.dto.UserSignUpDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface UserService {
    // DB에 유저정보 저장
    User saveUser(User user);

    // 유저 정보 수정
    void updateUser(User user);

    // DB에 Role 저장
    Role saveRole(Role role);

    // 특정유저 정보 가져오기
    User getUser(String username);

    // 유저 정보 삭제
    void deleteUser(String username);

    // 유저 정보 생성
    User newUser(UserSignUpDto userSignUpDto);

    // 이메일로 유저 확인 후 유저 정보 리턴
    User checkUserEmail(String email);

}
