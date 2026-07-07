package com.webjjang.api.controller;

import com.webjjang.api.data.dto.SignInResultDto;
import com.webjjang.api.data.dto.SignUpResultDto;
import com.webjjang.api.service.SignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sign-api")
@Log4j2
public class SignController {

    // service 자동 DI
    private final SignService signService;

    @Autowired // 생성자를 이용한 자동 DI
    public SignController(SignService signService){
        this.signService = signService;
    }

    //로그인 처리
    @PostMapping("/sign-in") //  /sign-api/sign-in
    @Operation(summary = "(로그인)")
    public SignInResultDto signIn(
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

        return signInResultDto;
    }

    @PostMapping("/sign-up") //  /sign-api/sign-up
    @Operation(summary = "(회원가입)")
    public SignUpResultDto signUp(
            @Parameter(name = "id", description = "아이디", required = true) @RequestParam String id,
            @Parameter(name = "password", description = "비밀번호", required = true) @RequestParam String password,
            @Parameter(name = "name", description = "이름", required = true) @RequestParam String name,
            @Parameter(name = "role", description = "권한", required = true) @RequestParam String role
    ){

        // 넘어온 데이터 확인
        log.info("[signUp] 회원가입을 수행합니다. id : {}, password : {}, name : {}, role : {}",
               id, password, name, role );
        SignUpResultDto signUpResultDto = signService.signUp(id, password, name, role);
        log.info("[signUp] 회원가입을 완료했습니다. id : {}", id);
        log.info("[signUp] signUpResultDto : {}", signUpResultDto);

        return signUpResultDto;
    }

    // 권한이 없는 경우의 필터에서 걸러서 메서드 호출 실행한다.
    // CustomAccessDeniedHandler에서 redirect로 요청이 옴(정상 요청). 권한이 없는 경우의 처리
    @GetMapping("/exception")  //  /sign-api/exception
    public void exceptionTest() throws RuntimeException {
        throw  new RuntimeException("접근이 금지되었습니다.");
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> exceptionHandlerAdvice(RuntimeException e){

        HttpHeaders httpHeaders = new HttpHeaders();

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        log.error("ExceptionHandler 호출, {}, {}", e.getCause(), e.getMessage());

        Map<String, String> map = new HashMap<>();
        // 상태 문자열을 가져오는 메서드 :  httpStatus.getReasonPhrase() -> Bad Request
        map.put("error type", httpStatus.getReasonPhrase());
        map.put("code", httpStatus.value() + "");
//        map.put("message", "에러 발생");
        map.put("message", e.getMessage());
        return new ResponseEntity<>(map, httpHeaders, httpStatus);
    }
}
