package com.webjjang.api.service;

import com.webjjang.api.data.dto.SignInResultDto;
import com.webjjang.api.data.dto.SignUpResultDto;

public interface SignService {

    //회원가입
    SignUpResultDto signUp(String id, String password, String name, String role);

    // 로그인
    SignInResultDto signIn(String id, String password) throws RuntimeException;

}
