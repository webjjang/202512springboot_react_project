package com.webjjang.api.image.vo;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class ImageVO {

    private Long no;
    private String title;
    private String content;
    private String writer;
    private Long hit;
    private LocalDateTime writeDate;
    private LocalDateTime updateDate;
    private String pw;

}
