package com.webjjang.api.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter & setter, toString ... 등을 자동으로 만든다.
@AllArgsConstructor // 모든 private 변수의 값을 세팅하는 생성자 만듬
@NoArgsConstructor // 기본 생성자 만듬.
public class ChangeProductNameDto {

    private Long number;
    private String name;

}
