package com.webjjang.api.pacs.controller;

import com.webjjang.api.pacs.service.PacsService;
import com.webjjang.api.pacs.vo.StudyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pacs")
@RequiredArgsConstructor
@Log4j2
public class PacsRestController {

    // 자동 DI - @RequiredArgsConstructor 클래스에 선언하면 private final 변수에 자동 DI 된다. 데이터가 세팅되어 있는 것은 제외
    private final PacsService pacsService;

    @GetMapping("/list.do")
    public ResponseEntity<List<StudyVO>> list(){

        return ResponseEntity.status(HttpStatus.OK).body(pacsService.getStudyList());
    }

}
