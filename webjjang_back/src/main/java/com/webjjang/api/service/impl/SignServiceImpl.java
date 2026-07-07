package com.webjjang.api.service.impl;

import com.webjjang.api.common.CommonResponse;
import com.webjjang.api.config.security.JwtTokenProvider;
import com.webjjang.api.data.dto.SignInResultDto;
import com.webjjang.api.data.dto.SignUpResultDto;
import com.webjjang.api.data.entity.User;
import com.webjjang.api.data.repository.UserRepository;
import com.webjjang.api.service.SignService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Log4j2
public class SignServiceImpl implements SignService {

    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SignServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SignUpResultDto signUp(String id, String password, String name, String role) {
        log.info("[signUp] 회원가입 정보 전달 : {} / {} / {} / {}", id, password, name, role);
        User user;
        if(role.equalsIgnoreCase("admin")){
            user = User.builder()
                    .uid(id)
                    .name(name)
                    .password(passwordEncoder.encode(password)) // 비밀번호는 암호화 시킨다.
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();
        }else{
            user = User.builder()
                    .uid(id)
                    .name(name)
                    .password(passwordEncoder.encode(password)) // 비밀번호는 암호화 시킨다.
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();
        }

        User savedUser = userRepository.save(user);

        SignUpResultDto signUpResultDto = new SignUpResultDto(); // 회원 가입 등록 후 처리되는 데이터 리턴하기 위한 객체

        log.info("[signUp] userEntity 값이 드어 왔는지 확인 후 결과값 주입");

        // 정상적으로 회원가입이 된 경우 처리
        if(!savedUser.getName().isEmpty()){
            log.info("[signUp] 정상 처리 완료");
            // 아래 메서드를 호출해서 처리
            setSuccessResult(signUpResultDto);
        } else {
            log.info("[signUp] 실패 처리 완료");
            // 아래 메서드를 호출해서 처리
            setFailResult(signUpResultDto);
        }

        return signUpResultDto;
    }

    @Override
    public SignInResultDto signIn(String id, String password) throws RuntimeException {
        log.info("[signIn] signDataHandler 로 회원 정보 요청 - id : {}", id);
        User user = userRepository.getByUid(id);
        log.info("[signIn] User : {}", user);

        log.info("[signIn] 패스워드 비교 수행");
        // 비밀번호가 같지 않으면 예외를 발생시킨다.
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("패스워드가 다릅니다.");
        }

        log.info("[signIn] 패스워드 일치");
        log.info("[signIn] SignInResultDto 객체 생성");
        SignInResultDto signInResultDto = SignInResultDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(user.getUid()), user.getRoles()))
                .build();

        log.info("[signIn] SignInResultDto 객체에 값 주입(토근은 제외)");
        setSuccessResult(signInResultDto);

        return signInResultDto;
    }

    // return이 없는데 전달된 객체는 주소가 전달이 되서 메서드 안에서 수정하면 밖에서도 적용된다.
    private void setSuccessResult(SignUpResultDto result){
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    // return이 없는데 전달된 객체는 주소가 전달이 되서 메서드 안에서 수정하면 밖에서도 적용된다.
    private void setFailResult(SignUpResultDto result){
        result.setSuccess(false);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
    }

}
