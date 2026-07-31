package com.webjjang.api.pacs.service;

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

            // Series
            List<String> seriesIds = (List<String>) study.get("Series");
            vo.setSeriesCount(seriesIds == null ? 0 : seriesIds.size());

            list.add(vo);

        }

        return list;
    }

}
