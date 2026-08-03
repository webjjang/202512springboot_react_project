package com.webjjang.api.pacs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacs_study")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacsStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    /**
     * Orthanc Study ID
     */
    @Column(length = 100, unique = true, nullable = false)
    private String orthancStudyId;

    /**
     * Study Instance UID
     */
    @Column(length = 128, unique = true)
    private String studyInstanceUID;

    /**
     * Accession Number - 접수번호
     */
    @Column(length = 64)
    private String accessionNumber;

    /**
     * Study Date - DICOM 날짜형식인데 날짜 타입과는 다른다.
     */
    @Column(length = 20)
    private String studyDate;

    /**
     * Study Time
     */
    @Column(length = 20)
    private String studyTime;

    /**
     * Study Description
     */
    @Column(length = 500)
    private String studyDescription;

    /**
     * Referring Physician
     */
    @Column(length = 200)
    private String referringPhysicianName; //검사를 의뢰한 의사(진료의사)

    /**
     * Requested Procedure Description
     */
    @Column(length = 500)
    private String requestedProcedureDescription;

    /**
     * Study ID
     */
    @Column(length = 64)
    private String studyID;

    /**
     * Orthanc Stable 여부
     */
    private Boolean stable;

    /**
     * Series 개수
     */
    private Integer seriesCount = 0;

    /**
     * Instance 개수
     */
    private Integer instanceCount = 0;

    /**
     * Patient (N:1)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_no", nullable = false)
    private PacsPatient patient;

    /**
     * Study -> Series (1:N)
     */
    @OneToMany(
            mappedBy = "study",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PacsSeries> seriesList = new ArrayList<>();
}