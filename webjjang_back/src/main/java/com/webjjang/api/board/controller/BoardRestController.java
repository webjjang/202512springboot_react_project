package com.webjjang.api.board.controller;

import com.webjjang.api.board.service.BoardService;
import com.webjjang.api.board.vo.BoardVO;
import com.webjjang.api.util.page.PageObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor // private final 변수 자동 DI 어노테이션
public class BoardRestController {

    private final BoardService service;

    // 1. list
    @GetMapping("/list.do")
    public ResponseEntity<Map<String, Object>> list(HttpServletRequest request) throws Exception {

        // 페이지 정보와 검색 정보를 전달받는 처리를 해서 PageObject를 만든다.
        PageObject pageObject = PageObject.getInstance(request);

        Map<String, Object> map = new HashMap<>();
        map.put("list", service.list(pageObject));
        map.put("pageObject", pageObject);

        return ResponseEntity.status(HttpStatus.OK)
                .body(map);
    }

    // 2. view
    @GetMapping("/view.do")
    public ResponseEntity<BoardVO> view(Long no, Integer inc){
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.view(no, inc));
    }

    // 3. write
    @PostMapping("/write.do")
    public ResponseEntity<String> write(BoardVO vo){
        service.write(vo);
        return ResponseEntity.status(HttpStatus.OK)
                .body("일반 게시판 글등록이 성공적으로 되었습니다.");
    }

    // 4. update
    @PostMapping("/update.do")
    public ResponseEntity<String> update(BoardVO vo){
        service.update(vo);
        return ResponseEntity.status(HttpStatus.OK)
                .body("일반 게시판 글수정이 성공적으로 되었습니다.");
    }

    // 5. delete
    @PostMapping("/delete.do")
    public ResponseEntity<String> delete(BoardVO vo){
        service.delete(vo);
        return ResponseEntity.status(HttpStatus.OK)
                .body("일반 게시판 글삭제가 성공적으로 되었습니다.");
    }

}
