package com.study.library.service;

import com.study.library.dto.SigninReqDto;
import com.study.library.dto.SignupReqDto;
import com.study.library.entity.User;
import com.study.library.exception.SaveException;
import com.study.library.jwt.JwtProvider;
import com.study.library.repository.UserMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service

// 비즈니스 로직을 처리하는 서비스 클래스가 위치합니다. 데이터 처리 및 비즈니스 규칙 구현 등을 담당합니다.

public class AuthService {//0314-6

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

//    public boolean isDuplicatedByUsername (String username) {
//        return userMapper.findUserByUsername(username) != null;
//    }

    @Transactional(rollbackFor = Exception.class)
    public void signup(SignupReqDto signupReqDto) {
        int successCount = 0;
        User user = signupReqDto.toEntity(passwordEncoder);

       successCount += userMapper.saveUser(user);
       successCount += userMapper.saveRole(user.getUserId());

       if(successCount < 2) {
            throw new SaveException();
       }
    }

    public String signin(SigninReqDto signinReqDto) {
        User user = userMapper.findUserByUsername(signinReqDto.getUsername());

        if( user == null) {
            throw new UsernameNotFoundException("사용자 정보를 확인하세요");
        }
        if(!passwordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("사용자 정보를 확인하세요");
        }


        return jwtProvider.generateToken(user);
    }

}
