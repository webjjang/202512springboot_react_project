package com.webjjang.api.pacs.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudyVO {

    /* Orthanc Study ID */
    private String id;

    // Patient - 환자 정보
    private String patientId;
    private String patientName;
    private String patientSex;
    private String patientBirthDate;

    // Study - 진료 정보
    private String studyInstanceUID;
    private String accessionNumber; // 접수번호
    private String studyDate;
    private String studyTime;
    private String studyDescription;
    private String referringPhysicianName;//검사를 의뢰한 의사(진료의사)
    private String requestedProcedureDescription;
    private String studyID;

    // 기타
    private String parentPatient;
    private boolean stable;

    // 카운트 정보 저장 변수
    private Integer seriesCount = 0;
    private Integer instanceCount = 0;

    // Series 정보 : String - id --> SeriesVO
    // private List<String> series;
    private List<SeriesVO> seriesList = new ArrayList<>();

}
