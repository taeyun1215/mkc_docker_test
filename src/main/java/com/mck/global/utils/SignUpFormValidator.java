package com.mck.global.utils;

import com.mck.domain.user.UserRepo;
import com.mck.domain.user.dto.UserSignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final UserRepo userRepo;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(UserSignUpDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserSignUpDto signUpDto = (UserSignUpDto) target;
        if(userRepo.existsByUsername(signUpDto.getUsername())){
            errors.rejectValue("username", "invalid.username", new Object[]{signUpDto.getUsername()}, "이미 사용중인 아이디 입니다.");
        }
        if(userRepo.existsByEmail(signUpDto.getEmail())){
            errors.rejectValue("email", "invalid.email", new Object[]{signUpDto.getEmail()}, "이미 사용중인 이메일 입니다.");
        }
    }
}
