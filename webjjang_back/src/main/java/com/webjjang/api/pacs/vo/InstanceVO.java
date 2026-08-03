package com.webjjang.api.pacs.vo;

import lombok.Data;

@Data
public class InstanceVO {

    // Orthanc Instance ID
    private String id;

    // DICOM SOP Instance UID
    private String sopInstanceUID;

    // DICOM SOP Class UID
    private String sopClassUID;

    // 영상 순서
    private String instanceNumber;

    // 영상 크기
    private Integer rows;
    private Integer columns;

    // 다중 프레임 영상의 프레임 개수
    private Integer numberOfFrames;

    // Orthanc 미리보기 또는 파일 조회 URL
    private String previewUrl;
    private String fileUrl;
}