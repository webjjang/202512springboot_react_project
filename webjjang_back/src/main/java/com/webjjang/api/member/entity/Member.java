package com.webjjang.api.member.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
// 날짜 자동 세팅
@EntityListeners(AuditingEntityListener.class)
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "gender in ('남자','여자')")
public class Member {

    @Id
    private String id;

    // 객체 -> JSON : 직렬화. 안됨. DB에서 데이터 가져와서 다른 곳에 보낼때 직렬화 시킨다. 비밀번호 제외
    // JSON -> 객체 : 역직렬화. 됨. 가져온 데이터를 DB에서 맞는지 확인.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String pw;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private LocalDateTime birth;

    private String tel;

    private String postNo;
    private String address;

    @Column(nullable = false)
    private String email;

    @CreatedDate
    @Column(updatable = false) // 한번 등록하면 변경하지 않는다.
    private LocalDateTime writeDate;

    // 로그인을 하면 현재 날자와 시간으로 변경시킨다.
    @CreatedDate
    private LocalDateTime conDate;

    // 1:n 관계의 별도의 테이블을 만들고 조회 시 즉시 조회한다.(FetchType.EAGER)
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default // new ArrayList<>() 를 보장한다. 데이터가 없어도 null이 되는 것을 막는다.
    // table로 생성 -> user_roles. user_id - user(id)와 연결
    private List<String> roles = new ArrayList<>();


}
