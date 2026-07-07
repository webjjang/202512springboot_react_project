package com.webjjang.api.board.vo;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class BoardVO {

    private Long no;
    private String title;
    private String content;
    private String writer;
    private Long hit;
    private LocalDateTime writeDate;
    private LocalDateTime updateDate;
    private String pw;

}
