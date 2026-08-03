package com.webjjang.api.pacs.service;

import com.webjjang.api.pacs.repository.PacsPatientRepository;
import com.webjjang.api.pacs.repository.PacsSeriesRepository;
import com.webjjang.api.pacs.repository.PacsStudyRepository;
import com.webjjang.api.pacs.vo.SeriesVO;
import com.webjjang.api.pacs.vo.StudyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor // private final 전역 변수에 값을 전달하는 생성자를 만든다. 자동 DI 적용
@Log4j2
public class PacsServiceImpl implements PacsService{

    // 자동 DI - WebClientConfig 에서 생성해 놓으라고 설정함.
    private final WebClient orthancWebClient;

    // DB에 저장하는 Repository 자동 DI
    private final PacsPatientRepository pacsPatientRepository;
    private final PacsStudyRepository pacsStudyRepository;
    private final PacsSeriesRepository pacsSeriesRepository;

    @Override
    public List<StudyVO> getStudyList(){
        List<StudyVO> list = new ArrayList<>();

        // study id 배열 데이터를 가져오기
        List<String> ids = orthancWebClient.get()
                .uri("/studies")
                .retrieve()// 받는 데이터 처리 쉽게 하기 위해
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})// 역직렬화 body -> List<String>
                .block(); // 비동기 통신이 끝날 때까지 기다린다.

        log.info("[getStudyList] ids = {}", ids);

        for (String id : ids){
            // study id에 대한 상세정보 가져오기
            Map<String, Object> study = orthancWebClient.get()
                    .uri("/studies/{id}", id)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            log.info("[getStudyList] study = {}", study);

            // study 정보가 들어 있음
            Map<String, String > mainDicomTags = (Map<String, String >) study.get("MainDicomTags");
            // patient 정보가 들어 있음.
            Map<String, String > patientMainDicomTags = (Map<String, String >) study.get("PatientMainDicomTags");

            log.info("[getStudyList] study.mainDicomTags = {}", mainDicomTags);
            log.info("[getStudyList] study.patientMainDicomTags = {}", patientMainDicomTags);

            // StudyVO 저장 -> List에 담는다.
            StudyVO vo = new StudyVO();

            // Orthanc 전체 정보
            vo.setId(id);
            vo.setStable(Boolean.TRUE.equals(study.get("IsStable")));
            vo.setParentPatient((String) study.get("ParentPatient"));

            // Patient 정보 저장
            vo.setPatientId(patientMainDicomTags.get("PatientID"));
            vo.setPatientName(patientMainDicomTags.get("PatientName"));
            vo.setPatientSex(patientMainDicomTags.get("PatientSex"));
            vo.setPatientBirthDate(patientMainDicomTags.get("PatientBirthDate"));

            // study data
            vo.setStudyInstanceUID(mainDicomTags.get("StudyInstanceUID"));
            vo.setStudyID(mainDicomTags.get("StudyID"));
            vo.setStudyDate(mainDicomTags.get("StudyDate"));
            vo.setStudyTime(mainDicomTags.get("StudyTime"));

            // Description이 없으면 null 대신에 -로 표시한다.
            String description = mainDicomTags.get("StudyDescription");
            // description이 비어 있으면(null이거나 "") "-" 바꾼다.
            if(description == null || description.isBlank())
                description = "-";
            vo.setStudyDescription(description);
            vo.setAccessionNumber(mainDicomTags.get("AccessionNumber"));
            vo.setRequestedProcedureDescription(mainDicomTags.get("RequestedProcedureDescription"));

            // Series 카운트 처리
            List<String> seriesIds = (List<String>) study.get("Series");
            vo.setSeriesCount(seriesIds == null ? 0 : seriesIds.size());

            list.add(vo);

        }

        return list;
    } // getStudyList() 의 끝

    // study 데이터 상세 보기
    @Override
    public StudyVO getStudyDetail(String studyId) {

        // studyId에 맞는 데이터 가져오기
        Map<String, Object> study = orthancWebClient
                .get()
                .uri("/studies/{id}", studyId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if(study == null) {
            return null;
        }

        StudyVO vo = new StudyVO();

        vo.setId((String) study.get("ID"));

        //-----------------------------
        // Patient 정보
        //-----------------------------
        Map<String, String> patient =
                (Map<String, String>) study.get("PatientMainDicomTags");

        if (patient != null) {
            vo.setPatientName(patient.get("PatientName"));
            vo.setPatientId(patient.get("PatientID"));
            vo.setPatientBirthDate(patient.get("PatientBirthDate"));
            vo.setPatientSex(patient.get("PatientSex"));
        }

        //-----------------------------
        // Study 정보
        //-----------------------------
        Map<String, String> tags =
                (Map<String, String>) study.get("MainDicomTags");

        if (tags != null) {
            vo.setStudyDate(tags.get("StudyDate"));
            vo.setStudyTime(tags.get("StudyTime"));
            vo.setStudyDescription(tags.get("StudyDescription"));
            vo.setAccessionNumber(tags.get("AccessionNumber"));
        }

        //-----------------------------
        // Count
        //-----------------------------

        // series id 꺼내오기
        List<String> series =
                (List<String>) study.get("Series");
        // instances 아이디 꺼내오기.
        List<String> instances =
                (List<String>) study.get("Instances");

        // .size() - List에 들어 있는 데이터의 개수를 가져오는 메서드
        vo.setSeriesCount(series == null ? 0 : series.size());
        vo.setInstanceCount(instances == null ? 0 : instances.size()); // 의미 없음. 밑에서 처리하고 있음.

        //-----------------------------
        // Series 조회
        //-----------------------------
        if (series != null) {

            List<SeriesVO> seriesList = new ArrayList<>();
            int totalInstanceCount = 0;

            for (String seriesId : series) {

                // series 데이터를 꺼내오기. getSeries() 는 밑에서 선언해 놓음.
                SeriesVO seriesVO = getSeries(seriesId);

                if (seriesVO != null) {
                    seriesList.add(seriesVO);

                    if (seriesVO.getInstanceCount() != null) {
                        // 각각의 series에 들어있는 Instance의 개수를 더한다.
                        totalInstanceCount += seriesVO.getInstanceCount();
                    }
                }
            } // for문의 끝

            // StudyVO에 seriesList 담기
            vo.setSeriesList(seriesList);
            // StudyVO에 전체 Instance 개수를 담아둔다.
            vo.setInstanceCount(totalInstanceCount);
        }

        return vo;
    } // getStudyDetail()의 끝

   // Siries의 정보를 가져오는 메서드
    private SeriesVO getSeries(String seriesId) {

        Map<String, Object> series = orthancWebClient
                .get()
                .uri("/series/{id}", seriesId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if(series == null) return null;

        log.info("[getSeries] series = {}", series);

        SeriesVO vo = new SeriesVO();
        vo.setId((String) series.get("ID"));

        Map<String, String> tags =
                (Map<String, String>) series.get("MainDicomTags");

        if (tags != null) {
            vo.setModality(tags.get("Modality"));
            vo.setSeriesDescription(tags.get("SeriesDescription"));
            vo.setSeriesNumber(tags.get("SeriesNumber"));
        }

        // 인스턴스 id 목록 받아오기
        List<String> instances =
                (List<String>) series.get("Instances");

        // instance 개수 저장하기.
        vo.setInstanceCount(instances == null ? 0 : instances.size());

        return vo;
    }

    @Override
    // DB의 텍스트 정보만 수정
    public StudyVO updateStudyInfo(Long no, StudyVO updateVO) {
        return null;
    } // updateStudyInfo() 메서드의 끝

    @Override
    // Pacs 서버에서 DICOM 데이터를 가져와서 DB에 저장하기
    public StudyVO saveStudyFromOrthanc(String orthancStudyId) {
        return null;
    }


} // PacsServiceImpl 클래스의 끝
