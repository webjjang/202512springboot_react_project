package com.webjjang.api.pacs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pacs_series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacsSeries {

    /**
     * PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    /**
     * Orthanc Series ID
     */
    @Column(name = "orthanc_series_id", length = 100, nullable = false, unique = true)
    private String orthancSeriesId;

    /**
     * DICOM Series Instance UID
     */
    @Column(name = "series_instance_uid", length = 128, nullable = false, unique = true)
    private String seriesInstanceUID;

    /**
     * Modality
     * CT, MR, CR, US ...
     */
    @Column(length = 20)
    private String modality;

    /**
     * Series Description
     */
    @Column(length = 500)
    private String seriesDescription;

    /**
     * Series Number
     */
    @Column(length = 20)
    private String seriesNumber;

    /**
     * Instance 개수
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer instanceCount = 0;

    /**
     * Study (N : 1)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_no", nullable = false)
    private PacsStudy study;
}