package com.webjjang.api.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// setter & getter, toString 메서드 등을 만든다.
@Data
@NoArgsConstructor // 기본 생성자를 만든다.
@AllArgsConstructor // 모든 private 변수를 전달받아서 세팅하는 생성자
public class ProductResponseDto {

    private Long number; // 상품번호
    private String name; // 상품명
    private int price; // 가격
    private int stock; // 재고

}
