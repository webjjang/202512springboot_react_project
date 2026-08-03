package com.webjjang.api.pacs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacs_patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacsPatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    /**
     * Orthanc Patient ID - Pacs 에서 생성된 ID
     * (예: 3a9c981d-2f0e-...)
     */
    @Column(length = 100, unique = true, nullable = false)
    private String orthancPatientId;

    /**
     * DICOM Patient ID - 병원에서 환자 ID
     */
    @Column(length = 100)
    private String patientId;

    /**
     * 환자명
     */
    @Column(length = 200)
    private String patientName;

    /**
     * 성별
     */
    @Column(length = 10)
    private String patientSex;

    /**
     * 생년월일
     */
    @Column(length = 20)
    private String patientBirthDate;

    /**
     * Orthanc Stable 여부
     */
    private Boolean stable;

    /**
     * Patient -> Study
     */
    @OneToMany(
            mappedBy = "patient", //외래키의 관리 주체 - pacsStudy가 관리
            cascade = CascadeType.ALL, // 참조하고 있는 내용을 적용시킨다.
            orphanRemoval = true, // 부모 데이터와 관계가 끊어지면 삭제한다.
            // LAZY - 필요할 때만 데이터를 가져온다. : select, FetchType.EAGER - 처음부터 데이터 가져옴.
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PacsStudy> studyList = new ArrayList<>();
}