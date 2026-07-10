package com.webjjang.api.service;

import com.webjjang.api.data.dto.SignInResultDto;
import com.webjjang.api.data.dto.SignUpResultDto;
import com.webjjang.api.member.vo.MemberVO;

public interface SignService {

    //회원가입
    SignUpResultDto signUp(MemberVO vo);

    // 로그인
    SignInResultDto signIn(String id, String pw) throws RuntimeException;

}
