package com.webjjang.api.data.entity;

// 설정의 어노테이션 들이 jakarta.persistence 안에 있는 모든 것을 import
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity // DB 스키마와 관련있는 객체
@Builder // 생성자를 사용할 때 데이터를 순서에 맞게 넣는데 항목이름의 메서드를 이용하여 생성할 수 있는 객체로 만든다.
@Getter // getter 생성
@Setter // setter 생성
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // private 변수를 전달 받아서 초기화 시키는 생성자
@EqualsAndHashCode // equals() 와 hashCode() 작성
@ToString(exclude = "name") // toString() 생성 name은 제외
@Table(name="product")
public class Product {

    @Id
    // IDENTITY - auto_increment : 1씩 계속 증가시키는 옵션(MySQL, MariaDB)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long number;

    @Column(nullable = false)
    private  String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private  Integer stock;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

}
