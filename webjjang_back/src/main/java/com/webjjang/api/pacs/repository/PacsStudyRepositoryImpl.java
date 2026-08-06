package com.webjjang.api.pacs.repository;

import com.webjjang.api.pacs.entity.PacsPatient;
import com.webjjang.api.pacs.entity.PacsSeries;
import com.webjjang.api.pacs.entity.PacsStudy;
import com.webjjang.api.pacs.vo.SeriesVO;
import com.webjjang.api.pacs.vo.StudyVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PacsStudyRepositoryImpl
        implements PacsStudyRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public StudyVO getStudyDetail(String studyId) {

        if (studyId == null || studyId.isBlank()) {
            throw new IllegalArgumentException(
                    "Orthanc Study ID는 필수입니다."
            );
        }

        PacsStudy study = entityManager.createQuery(
                        """
                        select distinct ps
                        from PacsStudy ps
                        join fetch ps.patient patient
                        left join fetch ps.seriesList series
                        where ps.orthancStudyId = :studyId
                        """,
                        PacsStudy.class
                )
                .setParameter("studyId", studyId)
                .getResultStream()
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Study 정보를 찾을 수 없습니다. studyId="
                                        + studyId
                        )
                );

        return toStudyVO(study);
    }

    private StudyVO toStudyVO(PacsStudy study) {

        StudyVO studyVO = new StudyVO();

        /*
         * StudyVO.id는 Orthanc Study ID를 의미하므로
         * DB PK인 study.getNo()가 아니라 orthancStudyId를 넣습니다.
         */
        studyVO.setOrthancStudyId(study.getOrthancStudyId());

        /*
         * Patient 정보
         */
        setPatientInfo(studyVO, study.getPatient());

        /*
         * Study 정보
         */
        studyVO.setStudyInstanceUID(study.getStudyInstanceUID());
        studyVO.setAccessionNumber(study.getAccessionNumber());
        studyVO.setStudyDate(study.getStudyDate());
        studyVO.setStudyTime(study.getStudyTime());
        studyVO.setStudyDescription(study.getStudyDescription());

        studyVO.setReferringPhysicianName(
                study.getReferringPhysicianName()
        );

        studyVO.setRequestedProcedureDescription(
                study.getRequestedProcedureDescription()
        );

        studyVO.setStudyID(study.getStudyID());

        /*
         * 상태 정보
         */
        studyVO.setStable(Boolean.TRUE.equals(study.getStable()));

        /*
         * Series 정보
         */
        List<SeriesVO> seriesList = toSeriesVOList(
                study.getSeriesList()
        );

        studyVO.setSeriesList(seriesList);

        /*
         * DB에 저장된 count 대신 실제 조회한 Series 크기를 사용할 수도 있습니다.
         *
         * 현재는 DB의 seriesCount 값이 있으면 사용하고,
         * 없으면 조회한 리스트 크기를 사용하도록 처리합니다.
         */
        studyVO.setSeriesCount(
                study.getSeriesCount() != null
                        ? study.getSeriesCount()
                        : seriesList.size()
        );

        /*
         * Instance는 DB에 개별 저장하지 않고
         * Study에 저장된 총 개수를 사용합니다.
         */
        studyVO.setInstanceCount(
                study.getInstanceCount() != null
                        ? study.getInstanceCount()
                        : calculateInstanceCount(seriesList)
        );

        return studyVO;
    }

    private void setPatientInfo(
            StudyVO studyVO,
            PacsPatient patient
    ) {

        if (patient == null) {
            return;
        }

        /*
         * 아래 getter 이름은 실제 PacsPatient 필드명에 맞게
         * 수정해야 합니다.
         */
        studyVO.setPatientId(patient.getPatientId());
        studyVO.setPatientName(patient.getPatientName());
        studyVO.setPatientSex(patient.getPatientSex());
        studyVO.setPatientBirthDate(patient.getPatientBirthDate());

        /*
         * StudyVO.parentPatient가 Orthanc Patient ID를 의미한다면
         * 아래처럼 설정합니다.
         */
        studyVO.setParentPatient(patient.getOrthancPatientId());
    }

    private List<SeriesVO> toSeriesVOList(
            List<PacsSeries> seriesList
    ) {

        if (seriesList == null || seriesList.isEmpty()) {
            return new ArrayList<>();
        }

        return seriesList.stream()
                .map(this::toSeriesVO)
                .sorted(seriesComparator())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private SeriesVO toSeriesVO(PacsSeries series) {

        SeriesVO seriesVO = new SeriesVO();

        /*
         * 실제 PacsSeries 필드명에 맞게 사용합니다.
         */
        seriesVO.setId(series.getOrthancSeriesId());
        seriesVO.setSeriesInstanceUID(
                series.getSeriesInstanceUID()
        );
        seriesVO.setModality(series.getModality());
        seriesVO.setSeriesDescription(
                series.getSeriesDescription()
        );
        seriesVO.setSeriesNumber(series.getSeriesNumber());

        seriesVO.setInstanceCount(
                series.getInstanceCount() != null
                        ? series.getInstanceCount()
                        : 0
        );

        return seriesVO;
    }

    private int calculateInstanceCount(
            List<SeriesVO> seriesList
    ) {

        return seriesList.stream()
                .map(SeriesVO::getInstanceCount)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private Comparator<SeriesVO> seriesComparator() {

        return Comparator.comparing(
                SeriesVO::getSeriesNumber,
                Comparator.nullsLast(this::compareSeriesNumber)
        );
    }

    private int compareSeriesNumber(
            String first,
            String second
    ) {

        try {
            return Integer.compare(
                    Integer.parseInt(first),
                    Integer.parseInt(second)
            );
        } catch (NumberFormatException e) {
            return first.compareTo(second);
        }
    }
}