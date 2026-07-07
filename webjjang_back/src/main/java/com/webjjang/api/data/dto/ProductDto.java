package com.webjjang.api.data.dto;

import lombok.*;

// 기본 생성자, setter & getter, toString, hashCode 메서드들이 자동으로 만들어 진다.
@Data
@NoArgsConstructor // 기본 생성자
// 모든 private 변수의 값을 생성자를 만들어서 세팅한다.
@AllArgsConstructor
@ToString // toString()  작성 - 데이터 확인 용도로 사용하는 메서드
@Builder // 항목별 데이터를 저장하는 메서드를 만든다. productDto.name("컴퓨터").price(1200000).stoke(100)
public class ProductDto {

    private String name;
    private int price;
    private int stoke;

}
