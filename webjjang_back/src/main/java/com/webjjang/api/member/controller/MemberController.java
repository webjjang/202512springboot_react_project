package com.webjjang.api.member.controller;

import com.webjjang.api.data.dto.SignInResultDto;
import com.webjjang.api.data.dto.SignUpResultDto;
import com.webjjang.api.member.vo.MemberVO;
import com.webjjang.api.service.SignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
@Log4j2
public class MemberController {

    // service 자동 DI
    private final SignService signService;

    @Autowired // 생성자를 이용한 자동 DI
    public MemberController(SignService signService){
        this.signService = signService;
    }

    //로그인 처리
    @PostMapping("/login.do") //  /sign-api/sign-in
    @Operation(summary = "(로그인)")
    public SignInResultDto login(
            @Parameter(name = "id", description = "아이디", required = true) @RequestParam String id,
            @Parameter(name = "password", description = "비밀번호", required = true) @RequestParam String password
    ) throws RuntimeException {

        // 넘어오는 데이터 확인하기
        log.info("[signIn] 로그인 시도를 하고 있습니다. id : {}, pw : {}", id, password);
        // 정상적인 로그인 처리가 되어서 데이터를 가져오거나 정보가 틀리면 예외가 발생된다.
        SignInResultDto signInResultDto = signService.signIn(id, password);

        if(signInResultDto.getCode() == 0){
            // signInResultDto - success, code, msg, token
            log.info("[signIn] 정상적으로 로그인되었습니다. id : {}, token : {}",
                    id, signInResultDto.getToken());
        }

        // 토큰이 포함됨. - react에서 X-AUTH-TOKEN 헤더에 추가해서 서버에 보내야한다.
        return signInResultDto;
    }

    @PostMapping("/sign-up") //  /sign-api/sign-up
    @Operation(summary = "(회원가입)")
    public SignUpResultDto signUp(
            MemberVO vo
    ){

        // 넘어온 데이터 확인
        log.info("[signUp] 회원가입을 수행합니다. vo : {}", vo);
        // DB에 적용
        SignUpResultDto signUpResultDto = signService.signUp(vo);
        log.info("[signUp] 회원가입을 완료했습니다.");
        log.info("[signUp] signUpResultDto : {}", signUpResultDto);

        return signUpResultDto;
    }

    // 권한이 없는 경우의 필터에서 걸러서 메서드 호출 실행한다.
    // CustomAccessDeniedHandler에서 redirect로 요청이 옴(정상 요청). 권한이 없는 경우의 처리
    @GetMapping("/exception")  //  /sign-api/exception
    public void exceptionTest() throws RuntimeException {
        throw  new RuntimeException("접근이 금지되었습니다.");
    }

}
