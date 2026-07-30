package com.webjjang.api.pacs.vo;

import lombok.Data;

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
    private String accessionNumber;
    private String studyDate;
    private String studyTime;
    private String studyDescription;
    private String referringPhysicianName;
    private String requestedProcedureDescription;
    private String studyID;

    // 기타
    private String parentPatient;
    private boolean stable;

    // Series 정보
    private List<String> series;

}
