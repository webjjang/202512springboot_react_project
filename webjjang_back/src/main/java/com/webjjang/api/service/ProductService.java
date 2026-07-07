package com.webjjang.api.service;

import com.webjjang.api.data.dto.ProductDto;
import com.webjjang.api.data.dto.ProductResponseDto;

public interface ProductService {

    // 상품 상세 보기
    ProductResponseDto getProduct(Long number);

    // 상품 등록
    ProductResponseDto saveProduct(ProductDto productDto);

    // 상품명 변경
    ProductResponseDto changeProductName(Long number, String name) throws Exception;

    // 상품 정보 삭제
    void deleteProduct(Long number) throws Exception;

}
