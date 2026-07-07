package com.webjjang.api.data.dao;

import com.webjjang.api.data.entity.Product;

// Product CRUD 처리 메서드 선언 - 115 페이지
public interface ProductDAO {

    // 제품 등록
    Product insertProduct(Product product);

    // 제품 보기
    Product selectProduct(Long number);

    // 제품명 변경?
    Product updateProductName(Long number, String name)
            throws Exception;

    // 제품 삭제
    void deleteProduct(Long number) throws Exception;
}
