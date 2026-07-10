package com.webjjang.api.member.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {

    private String id;

    private String pw;

    private String name;

    private String gender;

    private LocalDateTime birth;

    private String tel;

    private String postNo;
    private String address;

    private String email;

    private LocalDateTime writeDate;

    private LocalDateTime conDate;

    private List<String> roles = new ArrayList<>();

}
