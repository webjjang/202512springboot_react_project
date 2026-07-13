package com.webjjang.api.image.controller;

import com.webjjang.api.board.service.BoardService;
import com.webjjang.api.board.vo.BoardVO;
import com.webjjang.api.config.security.JwtTokenProvider;
import com.webjjang.api.image.service.ImageService;
import com.webjjang.api.image.vo.ImageVO;
import com.webjjang.api.util.page.PageObject;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/image")
@Log4j2
@RequiredArgsConstructor // private final 변수 자동 DI 어노테이션
@CrossOrigin(origins = "http://localhost:5173") // react 서버로 데이터 공유 허용해준다.
public class ImageRestController {


    private final JwtTokenProvider jwtTokenProvider;

    private final ImageService service;

    private final String savePath = "c:/upload/image/"; // realPath

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
    public ResponseEntity<ImageVO> view(Long no, Integer inc){
        log.info("[view] no = {}, inc = {}",no, inc);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.view(no, inc));
    }

//   localhost/image/~~~.jpg
//    @GetMapping("/{fileName}") - config.WebConfig에서 설정 처리하는 방법이 더 간편.
    public ResponseEntity<Resource> viewImage(
            @PathVariable String fileName) throws MalformedURLException {

        log.info("[viewImage] 이미지 보기 처리되고 있음.");
        Path path = Paths.get(savePath).resolve(fileName);

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType;

        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


    // 3. write
    @PostMapping(value = "/write.do",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> write(
            @Parameter(
                    description = "이미지 정보",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ImageVO.class)
                    )
            )
            @RequestPart ImageVO vo,
            @RequestPart MultipartFile imageFile,
            @RequestHeader("X-AUTH-TOKEN") String token) throws IOException {

        log.info("[write] vo = {}, fileName = {}",
                vo, imageFile.getOriginalFilename());
        // 데이터 세팅 - title, content 전달 받음. id : 헤더의 토큰으로 전달 받는다.(하드코딩)
//        vo.setId("test");
        vo.setId(jwtTokenProvider.getId(token));
        // 파일을 저장하고 저장된 파일의 정보 세팅
        log.info("[write] 저장 경로 : {}", savePath);
        String originalFilename = imageFile.getOriginalFilename();
        String extension = null;
        if (originalFilename != null && originalFilename.contains(".")) {
            // .을 포함한 확장자를 구하자.
            extension = originalFilename.substring(
                    originalFilename.lastIndexOf(".")
            );
        } else {
            throw  new RuntimeException("이미지가 첨부되어야만 합니다.");
        }

        // UUID 파일명 생성
        String savedFilename = UUID.randomUUID().toString() + extension;

        // 저장 경로 생성
        Path filePath = Paths.get(savePath, savedFilename);

        // 디렉터리 없으면 생성
        Files.createDirectories(filePath.getParent());

        // 파일 저장
        imageFile.transferTo(filePath.toFile());

        // vo에 fileName 추가
        vo.setFileName(savedFilename);

        // vo 확인
        log.info("[write] 데이터 세팅 완료 vo = {}", vo);

        //DB에 저장 처리
        ImageVO savedImageVO = service.write(vo);
        log.info("[write] DB에 저장된 데이터 확인. savedImageVO = {}", savedImageVO);

        return ResponseEntity.status(HttpStatus.OK)
                .body("이미지 게시판 등록이 성공적으로 되었습니다.");
    }

    // 4. update
    @PostMapping("/update.do")
    public ResponseEntity<String> update(@RequestBody ImageVO vo){
        service.update(vo);
        return ResponseEntity.status(HttpStatus.OK)
                .body("일반 게시판 글수정이 성공적으로 되었습니다.");
    }

    // 5. delete
    @PostMapping("/delete.do")
    public ResponseEntity<String> delete(@RequestBody ImageVO vo){
        service.delete(vo);
        return ResponseEntity.status(HttpStatus.OK)
                .body("일반 게시판 글삭제가 성공적으로 되었습니다.");
    }

    // 6. changeImage
    public ResponseEntity<String> changeIamge(@RequestBody Long no, MultipartFile changeImage){
        // 데이터 수집
        // no : 파라메터로 넘어온다. fileName : changeImage 추출, id : 토큰
        return ResponseEntity.status(HttpStatus.OK)
                .body("이미지 게시판 이미지가 성공적으로 변경되었습니다.");
    }

}
