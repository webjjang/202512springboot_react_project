package com.webjjang.api.pacs.vo;

import lombok.Data;

@Data
// divom 파일 대한 정보
public class SeriesVO {

    private String id; // series id

    private String modality; // 접수 번호

    private String seriesDescription; // 설명

    private Integer instanceCount; // 파일 개수

    private String seriesNumber; // 번호
}