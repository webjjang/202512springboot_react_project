package com.webjjang.api.image.entity;

import com.webjjang.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
// 날짜 자동 세팅
@EntityListeners(AuditingEntityListener.class)
@Table
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false, length = 300)
    private String title;

    // 내용을 긴 글자 입력이 가능하도록 text 타입 사용.
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "member_id")
//    @ToString.Exclude
    private Member member;

    @CreatedDate
    @Column(updatable = false) // 한번 등록하면 변경하지 않는다.
    private LocalDateTime writedDate;

    // 최근 수정일 - 자동 변경 : 단, JPA Factory를 사용하면 적용 안됨. -> 별도로 세팅
    //  - JPARepository를 사용하면 적용됨. save()를 사용하면 적용됨.
    @LastModifiedDate
    private LocalDateTime updatedDate;

    private Long hit = 0L;

}
