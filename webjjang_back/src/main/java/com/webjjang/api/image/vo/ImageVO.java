package com.webjjang.api.image.vo;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class ImageVO {

    private Long no;
    private String title;
    private String content;
    private String fileName;
    private String id;
    private String name;
    private LocalDateTime writedDate;
    private LocalDateTime updatedDate;
    private Long hit;

}
