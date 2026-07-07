package com.webjjang.api.controller;

import com.webjjang.api.data.dto.ChangeProductNameDto;
import com.webjjang.api.data.dto.ProductDto;
import com.webjjang.api.data.dto.ProductResponseDto;
import com.webjjang.api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Rest Data를 URL로 주고 받는 Controller -> 어노테이션을 이용해서 자동생성
@RestController
@RequestMapping("/product")
public class ProductController {

    // 처리를 담당하는 service를 간절히 원한다.
    private final ProductService productService;

    // service를 전달 받는 생성자. final 변수로 만들어서 초기값만 세팅 가능
    @Autowired // 자동 DI
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    // 기능 별로 메서드 한개 씩 만든다. URL 매핑시킨다.
    // 상품 상세보기 / 상품 등록 / 상품명 변경 / 상품 삭제
    // 기능별로 처리할 때
    //    1. URL을 다르게 매핑시킨다. 2. 다른 처리 방식(method)을 사용한다.

    // 상품 상세보기
    // ResponseEntity<전달 데이터> - 전달 데이터 + 처리 상태
    // mapping 안에 내용이 없다. -> class 위에 매핑을 그대로 사용한다. 단, get 방식 처리
    @GetMapping()
    public ResponseEntity<ProductResponseDto> getProduct(Long number){
        ProductResponseDto productResponseDto = productService.getProduct(number);
        // return new ResponseEntity(productResponseDto, HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDto);
    }

    // 상품 등록
    // ResponseEntity<전달 데이터> - 전달 데이터 + 처리 상태
    // mapping 안에 내용이 없다. -> class 위에 매핑을 그대로 사용한다. 단, post 방식 처리
    @PostMapping()
    @Operation(summary = "상품 등록")
    @Parameters({
            @Parameter(
                    name = "X-AUTH-TOKEN",
                    description = "인증 토큰",
                    required = true,
                    in = ParameterIn.HEADER
            )
    })
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductDto productDto){
        ProductResponseDto productResponseDto = productService.saveProduct(productDto);
        // return new ResponseEntity(productResponseDto, HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDto); // Builder
    }


    // 상품 이름 바꾸기
    // ResponseEntity<전달 데이터> - 전달 데이터 + 처리 상태
    // mapping 안에 내용이 없다. -> class 위에 매핑을 그대로 사용한다. 단, put 방식 처리
    @PutMapping()
    public ResponseEntity<ProductResponseDto> changeProductName(
            @RequestBody ChangeProductNameDto changeProductNameDto) throws Exception{
        ProductResponseDto productResponseDto
                = productService.changeProductName(
                    changeProductNameDto.getNumber(),
                    changeProductNameDto.getName());
        // return new ResponseEntity(productResponseDto, HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDto);
    }

    // 상품 삭제
    // ResponseEntity<전달 데이터> - 전달 데이터 + 처리 상태
    // mapping 안에 내용이 없다. -> class 위에 매핑을 그대로 사용한다. 단, delete 방식 처리
    @DeleteMapping()
    public ResponseEntity<String> deleteProduct(Long number) throws Exception{
        productService.deleteProduct(number);
        // return new ResponseEntity(productResponseDto, HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body("정상적으로 삭제되었습니다.");
    }

}
