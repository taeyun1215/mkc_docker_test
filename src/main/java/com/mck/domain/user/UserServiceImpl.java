package com.mck.domain.user;

import com.mck.domain.role.Role;
import com.mck.domain.role.RoleRepo;
import com.mck.domain.user.dto.UserSignUpDto;
import com.mck.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepo.findByName("ROLE_USER");
        user.getRoles().add(role);
        User save = userRepo.save(user);
        log.info("새로운 유저 정보를 DB에 저장했습니다 : ", user.getUsername());
        return save;
    }

    @Override
    public void updateUser(User user) {
//        User findUser = userRepo.findByUsername(user.getUsername()).orElseThrow(() -> {
//            return new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
//        });

        userRepo.save(user);
        log.info("유저 정보를 수정했습니다 : ", user.getUsername());
    }

    @Override
    public Role saveRole(Role role) {
        log.info("새로운 Role 정보를 DB에 저장했습니다 : ", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public User getUser(String username) {
        log.info("사용자 {}의 상세 정보를 가져왔습니다.", username);
        return userRepo.findByUsername(username).get();
    }

    @Override
    public void deleteUser(String username) {
        log.info("사용자 {} 를 삭제하였습니다.", username);
        userRepo.deleteByUsername(username);
    }

    // 유저 생성
    @Override
    public User newUser(UserSignUpDto userSignUpDto) {
        User user = new User();
        user.setUsername(userSignUpDto.getUsername());
        user.setPassword(userSignUpDto.getPassword());
        user.setNickname(userSignUpDto.getNickname());
        user.setEmail(userSignUpDto.getEmail());
        user.setEmailVerified(false);

        return user;
    }

    @Override
    public User checkUserEmail(String email) {
        boolean result = userRepo.existsByEmail(email);
        if (result){
            Optional<User> user = userRepo.findByEmail(email);
            return user.get();
        } else{
            return null;
        }
    }
}
