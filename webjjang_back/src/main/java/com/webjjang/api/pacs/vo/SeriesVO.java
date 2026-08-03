package com.webjjang.api.pacs.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
// divom 파일 대한 정보
public class SeriesVO {

    private String id; // series id

    private String seriesInstanceUID; // DICOM Series instanceUID - 추가

    private String modality; // 영상 장비 종류 : CT, MR...

    private String seriesDescription; // 설명

    private Integer instanceCount = 0; // 파일 개수

    private String seriesNumber; // 번호

    // 영상 상세 조회가 필요할 때 사용 리스트 - 추가
    private List<InstanceVO> instanceList = new ArrayList<>();
}