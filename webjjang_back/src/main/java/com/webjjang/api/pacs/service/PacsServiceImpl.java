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
    // Pacs 서버에서 DICOM 전체 데이터를 가져와서 DB에 저장하기 ---------------------------------------------------------------------------
    public StudyVO saveStudyFromOrthanc(String orthancStudyId) {
        // study ids를 가져오기
        List<String> orthancStudyids = orthancWebClient.get()
                .uri("/studies")
                .retrieve()// 받는 데이터 처리 쉽게 하기 위해
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})// 역직렬화 body -> List<String>
                .block(); // 비동기 통신이 끝날 때까지 기다린다.

        log.info("[getStudyList] orthancStudyids = {}", orthancStudyids);

        if(orthancStudyids == null) orthancStudyids = new ArrayList<>();


        // 처리 결과로 가지는 처리 개수들의 변수 정의
        int savedCount = 0; // DB에 저장되면 +1 한다.
        int skippedCount = 0; // DB에 데이터가 있으면 저장하지 않고 +1 한다.
        int failedCount = 0; // Exception이 발생되면 +1 한다.

        /*
         * 2. Orthanc Study ID별 반복 처리 - 하나의 Study 데이터 가져오기
         */

        for(String studyId : orthancStudyids){
            try{

                // studyId가 DB 에 있는지 확인
                if(pacsStudyRepository.existsByOrthancStudyId(studyId)){
                    skippedCount ++; // DB에 존재하는 studyID 저장하지 않고 넘긴다.
                    continue; // 다음 데이터를 확인하려 간다.
                }

                /*
                 * 3. Orthanc Pacs 서버에서 상세 정보를 가져온다.
                 */

                Map<String, Object> studyDetailData =
                        getStudyFromOrthanc(studyId); // 아래 메서드로 작성해 놓음. 상세 정보 가져오기

                if(studyDetailData == null){
                    failedCount ++;
                    continue; // 다음 데이터 처리로 간다.
                }

                /*
                 * 4. Study 에 정보를 위해서 MainDicomTags 꺼내기
                 */
                Map<String, String>  studyTags = getStringMap(studyDetailData, "MainDicomTags");

                String studyInstanceUID = getTag(studyTags, "StudyInstanceUID");

                //
                if(studyInstanceUID != null && !studyInstanceUID.isBlank()
                    && pacsStudyRepository.existsByStudyInstanceUID(studyInstanceUID)){
                    skippedCount ++;
                    continue; // 다음 데이터로 넘어간다 - for문 처음으로 간다.
                }

                // 환자 정보

                // study 저장
                // series 저장

            }catch (Exception e){
                failedCount ++; // 실패 카운트 1 증가.
                // 예외 메시지 처리 로그
                log.info("[saveStudyFromOrthanc] Study 저장 실패 : {}", studyId, e);
            }
        }

        return null;
    }

    /*
     * Study 상세 정보를 받아 와서 돌려주는 메서드
     */
    public Map<String, Object> getStudyFromOrthanc(
            String orthancStudyId
    ) {
        return orthancWebClient // Orthanc Server의 접근 정보가 있다. Bean 객체로 만다.
                .get()
                .uri(
                        "/studies/{id}",
                        orthancStudyId
                )
                .retrieve()
                .bodyToMono( // 역직렬화 - 문자열의 JSON 데이터를 Map으로 만든다.
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                )
                .block(); // 처리가 다 끝날 때 까지 기다린다.
    } //getStudyFromOrthanc

    // String 을 Map으로 만들어 주는 메서드 (JSON 데이터 문자열을 k, value 형식인 Map으로 만들어준다.)
    @SuppressWarnings("unchecked")
    private Map<String, String> getStringMap(
            Map<String, Object> data, // 전체 데이터
            String key // 찾으려는 JSON 데이터의 이름
    ){
        if(data == null) return null;

        // data에서 key에 해당되는 데이터를 꺼낸다.
        Object value = data.get(key);

        // Map<String, String 변경 가능?
        if(value instanceof Map<?, ?>) return (Map<String, String>) value;

        return null;
    }

    // tags(Map)에서 일정한 데이터를 꺼내는 메서드
    private String getTag(
            Map<String, String> tags, // tag가 여러개 전체 데이터
            String key // key에 해당되는 tag를 찾는다.
    ){
        if(tags == null) return null;

        return tags.get(key);
    }

} // PacsServiceImpl 클래스의 끝
