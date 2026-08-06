package com.webjjang.api.pacs.service;

import com.webjjang.api.pacs.entity.PacsPatient;
import com.webjjang.api.pacs.entity.PacsSeries;
import com.webjjang.api.pacs.entity.PacsStudy;
import com.webjjang.api.pacs.repository.PacsPatientRepository;
import com.webjjang.api.pacs.repository.PacsSeriesRepository;
import com.webjjang.api.pacs.repository.PacsStudyRepository;
import com.webjjang.api.pacs.vo.SeriesVO;
import com.webjjang.api.pacs.vo.StudySaveResultVO;
import com.webjjang.api.pacs.vo.StudyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        return pacsStudyRepository.findStudyList();
    } // getStudyList() 의 끝

    // study 데이터 상세 보기
    @Override
    @Transactional(readOnly = true)
    public StudyVO getStudyDetail(String studyId) {

        if (studyId == null || studyId.isBlank()) {
            throw new IllegalArgumentException(
                    "Orthanc Study ID는 필수입니다."
            );
        }

        log.info(
                "[getStudyDetail] DB Study 상세 조회 - orthancStudyId={}",
                studyId
        );

        return pacsStudyRepository.getStudyDetail(studyId);
    }


   // Siries의 정보를 가져오는 메서드 삭제
   // private SeriesVO getSeries(String seriesId)

    @Override
    // DB의 텍스트 정보만 수정
    public StudyVO updateStudyInfo(Long no, StudyVO updateVO) {
        return null;
    } // updateStudyInfo() 메서드의 끝



    @Override
    /*
     *   Pacs 서버에서 DICOM 전체 데이터를 가져와서 DB에 저장하기 -------------------
     */
    public StudySaveResultVO saveStudyFromOrthanc(String orthancStudyId) {
        // study ids를 가져오기
        List<String> orthancStudyIds = orthancWebClient.get()
                .uri("/studies")
                .retrieve()// 받는 데이터 처리 쉽게 하기 위해
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})// 역직렬화 body -> List<String>
                .block(); // 비동기 통신이 끝날 때까지 기다린다.

        log.info("[getStudyList] orthancStudyids = {}", orthancStudyIds);

        if(orthancStudyIds == null) orthancStudyIds = new ArrayList<>();


        // 처리 결과로 가지는 처리 개수들의 변수 정의
        int savedCount = 0; // DB에 저장되면 +1 한다.
        int skippedCount = 0; // DB에 데이터가 있으면 저장하지 않고 +1 한다.
        int failedCount = 0; // Exception이 발생되면 +1 한다.


        /*
         * 2. Orthanc Study ID별 반복 처리 - 하나의 Study 데이터 가져오기
         */

        log.info("\n[saveStudyFromOrthanc] 동기화 시작 ------------------>");

        for(String studyId : orthancStudyIds){
            try{

                log.info("[saveStudyFromOrthanc] 각 studyID 동기화 : {}", studyId);

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

                // 가져오지 못한 경우
                if(studyDetailData == null){
                    failedCount ++;
                    continue; // 다음 데이터 처리로 간다.
                }

                /*
                 * 4. Study -> studyDefailData 에 정보를 위해서 MainDicomTags(study 정보가 있음) 꺼내기
                 */
                // studyDetailData에서 JSON 데이터를 꺼내서 Map으로 만든다. - getStringMap
                // studyTags - studyTag[k-v]가 여러개
                Map<String, String>  studyTags = getStringMap(studyDetailData, "MainDicomTags");
                // 항목 이름에 맞는 tag의 값을 꺼낸다.
                // study :: 1회 검사 구분을 위해 사용하는 tag가 studyInstanceUID이다.
                String studyInstanceUID = getTag(studyTags, "StudyInstanceUID");

                /*
                 * Orthanc ID는 다르더라도
                 * StudyInstanceUID가 이미 있으면 같은 Study로 판단
                 */
                // Tag의 값이 존재하고 DB에도 존재하면 skip 시킨다.(저장하지 않는다.)
                if(studyInstanceUID != null && !studyInstanceUID.isBlank()
                    && pacsStudyRepository.existsByStudyInstanceUID(studyInstanceUID)){
                    skippedCount ++;
                    continue; // 다음 데이터로 넘어간다 - for문 처음으로 간다.
                }


                /*
                 * <<<<<< DB에 저장하는 처리 >>>>>>>>>
                 */

                /*
                 * 5. Patient 조회 또는 신규 저장
                 * - 환자 정보 - 조회 : DB에 있으면 있는 데이터 / DB에 없으면 신규 데이터 저장
                 */
                PacsPatient patient = findOrCreatePatient(studyDetailData);
                log.info("[saveStudyFromOrthanc] - DB의 저장되어 있는 PacsPatient = {}", patient);

                /*
                 * 6. Study 신규 저장 - DB에 존재하면 다음 데이터를 처리하는 코드가 앞 부분에 작성되어 있다.
                 */
                PacsStudy study =
                        createStudyEntity(
                                studyDetailData,
                                studyTags,
                                patient
                        );

                // 수집한 PacsStudy 엔티티 데이터를 DB에 저장한다.
                PacsStudy savedStudy =
                        pacsStudyRepository.save(study);

                /*
                 * 7. Study에 포함된 Series 신규 저장
                 */
                saveSeriesList(
                        studyDetailData,
                        savedStudy
                );

                // 저장 완료 카운트 1 증가
                savedCount++;


            }catch (Exception e){
                failedCount ++; // 실패 카운트 1 증가.
                // 예외 메시지 처리 로그
                log.info("[saveStudyFromOrthanc] Study 저장 실패 : {}", studyId, e);
            }
        } // for(String studyId : orthancStudyIds) 의 끝

        return StudySaveResultVO.builder()
                .totalCount(orthancStudyIds.size())
                .savedCount(savedCount)
                .skippedCount(skippedCount)
                .failedCount(failedCount)
                .build();
    };

    // 환자 정보를 DB에서 찾아봐서 있으면 저장 안함. 없으면 새로 저장하는 메서드 : 결과는 PacsPatient Entity 가 리턴된다. ------------
    private PacsPatient findOrCreatePatient(Map<String, Object> studyDetailData) throws Exception {
        /*
         * Orthanc Study의 ParentPatient가
         * Orthanc Patient ID이다.
         */
        String orthancPatientId = getString(studyDetailData, "ParentPatient");

        if(orthancPatientId == null || orthancPatientId.isBlank())
            throw new Exception("Orthanc Patient Id가 없습니다.");

        /*
         * 이미 저장된 Patient가 있으면 그대로 사용
         */
        return pacsPatientRepository
                .findByOrthancPatientId( // Optional 객체를 가져옴. 가져와 지면 orElseGet() 실행되지 않는다. 가져온 데이터 사용
                        orthancPatientId
                )
                // 가녀온 Optional 객체가 있으면 그 값을 리턴하고 없으면 메서드가 실행이 되어 새로운 객체를 생성해서 저장하고 리턴하는 메서드
                .orElseGet(() -> {

                    log.info("[findOrCreatePatient] - DB에 Patient 정보가 존재하지 않아 저장을 진행합니다.");

                    // 넘겨 받은 study 데이터에서 PatientMainDicomTags의 JSON데이터를 MAP로 꺼낸다.
                    Map<String, String> patientTags =
                            getStringMap(
                                    studyDetailData,
                                    "PatientMainDicomTags"
                            );

                    PacsPatient patient =
                            PacsPatient.builder()
                                    .orthancPatientId(
                                            orthancPatientId
                                    )
                                    .patientId(
                                            getTag(
                                                    patientTags,
                                                    "PatientID"
                                            )
                                    )
                                    .patientName(
                                            getTag(
                                                    patientTags,
                                                    "PatientName"
                                            )
                                    )
                                    .patientSex(
                                            getTag(
                                                    patientTags,
                                                    "PatientSex"
                                            )
                                    )
                                    .patientBirthDate(
                                            getTag(
                                                    patientTags,
                                                    "PatientBirthDate"
                                            )
                                    )
                                    .stable(
                                            getBoolean(
                                                    studyDetailData,
                                                    "IsStable"
                                            )
                                    )
                                    .build();

                    // 데이터가 채워진 patient를 DB 에 저장한 데이터가 리턴되거나 또는 DB에서 가져온 데이터가 리턴된다.다.
                    return pacsPatientRepository.save(
                            patient
                    );
                }); // orElseGet() 의 끝


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

    // tags(Map)에서 일정한 데이터를 꺼내는 메서드 : JSON 데이터를 꺼내서 값을 받아내는 처리
    private String getTag(
            Map<String, String> tags, // tag가 여러개 전체 데이터
            String key // key에 해당되는 tag를 찾는다.
    ){
        if(tags == null) return null;

        return tags.get(key);
    }

    // Map<String, Object> JSON의 상위 데이터를 문자열로 꺼내서 반환해 주는 메서드
    private String getString(
            Map<String, Object> data, String key
    ){
        if(data == null) return null;

        Object value = data.get(key);

        // value가 null이면 null 리턴 아니면, 문자열로 만들어서 리턴한다.
        return value == null ? null: String.valueOf(value);
    }


    private Boolean getBoolean(
            Map<String, Object> data,
            String key
    ) {

        if (data == null) {
            return false;
        }

        Object value = data.get(key);

        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }

        if (value instanceof String stringValue) {
            return Boolean.parseBoolean(
                    stringValue
            );
        }

        return false;
    }

    /*
     *
     */
    private PacsStudy createStudyEntity(
            Map<String, Object> studyData, // studyData == studyDetailData
            Map<String, String> studyTags,
            PacsPatient patient
    ) {

        String orthancStudyId =
                getString(
                        studyData,
                        "ID"
                );

        List<String> seriesIds =
                getStringList(
                        studyData,
                        "Series"
                );

        PacsStudy study = PacsStudy.builder()
                .orthancStudyId(
                        orthancStudyId
                )
                .studyInstanceUID(
                        getTag(
                                studyTags,
                                "StudyInstanceUID"
                        )
                )
                .accessionNumber(
                        getTag(
                                studyTags,
                                "AccessionNumber"
                        )
                )
                .studyDate(
                        getTag(
                                studyTags,
                                "StudyDate"
                        )
                )
                .studyTime(
                        getTag(
                                studyTags,
                                "StudyTime"
                        )
                )
                .studyDescription(
                        getTag(
                                studyTags,
                                "StudyDescription"
                        )
                )
                .referringPhysicianName(
                        getTag(
                                studyTags,
                                "ReferringPhysicianName"
                        )
                )
                .requestedProcedureDescription(
                        getTag(
                                studyTags,
                                "RequestedProcedureDescription"
                        )
                )
                .studyID(
                        getTag(
                                studyTags,
                                "StudyID"
                        )
                )
                .stable(
                        getBoolean(
                                studyData,
                                "IsStable"
                        )
                )
                .seriesCount(
                        seriesIds.size()
                )
                .instanceCount(0)
                .patient(patient)
                .build();

        /*
         * 양방향 관계 설정
         */
        patient.getStudyList().add(study);

        return study;
    }


    /*
     *
     */

    @SuppressWarnings("unchecked")
    private List<String> getStringList(
            Map<String, Object> data,
            String key
    ) {

        if (data == null) {
            return new ArrayList<>();
        }

        Object value = data.get(key);

        if (value instanceof List<?>) {
            return (List<String>) value;
        }

        return new ArrayList<>();
    }

    private void saveSeriesList(
            Map<String, Object> studyData, // studyData == studyDetailData
            PacsStudy study
    ) {

        List<String> orthancSeriesIds =
                getStringList(
                        studyData,
                        "Series"
                );

        int totalInstanceCount = 0;

        // Series는 배열로 되어 있다. List<String> 타입의 데이터 - 향상된 for문으로 전체 반복 처리.
        for (String orthancSeriesId : orthancSeriesIds) {

            /*
             * Orthanc Series ID가 이미 있다면 건너뜀
             */
            if (pacsSeriesRepository
                    .existsByOrthancSeriesId(
                            orthancSeriesId
                    )) {

                continue;
            }

            // DB에 없으면 저장한다.

            // series 한개의 데이터를 가져온다.
            Map<String, Object> seriesData =
                    getSeriesFromOrthanc(
                            orthancSeriesId
                    );

            // DB에 데이터가 없는 경우에만 로그 출력이 된다.
            log.info("[saveSeriesList] Orthanc Pacs 서버에서 가져온 series 데이터 : {}", seriesData);

            // 데이터가 없으면 다음 series 데이터를 가지러 간다.
            if (seriesData == null) {
                continue;
            }

            // Othanc Pacs 서버에 데이터가 있으면서 DB에는 데이터가 없는 경우 저장 처리한다.
            Map<String, String> seriesTags =
                    getStringMap(
                            seriesData,
                            "MainDicomTags"
                    );

            String seriesInstanceUID =
                    getTag(
                            seriesTags,
                            "SeriesInstanceUID"
                    );

            /*
             * SeriesInstanceUID가 이미 있으면 건너뜀
             */
            if (seriesInstanceUID != null
                    && !seriesInstanceUID.isBlank()
                    && pacsSeriesRepository
                    .existsBySeriesInstanceUID(
                            seriesInstanceUID
                    )) {

                continue;
            }

            List<String> instanceIds =
                    getStringList(
                            seriesData,
                            "Instances"
                    );

            int instanceCount =
                    instanceIds.size();

            PacsSeries series =
                    PacsSeries.builder()
                            .orthancSeriesId(
                                    orthancSeriesId
                            )
                            .seriesInstanceUID(
                                    seriesInstanceUID
                            )
                            .modality(
                                    getTag(
                                            seriesTags,
                                            "Modality"
                                    )
                            )
                            .seriesDescription(
                                    getTag(
                                            seriesTags,
                                            "SeriesDescription"
                                    )
                            )
                            .seriesNumber(
                                    getTag(
                                            seriesTags,
                                            "SeriesNumber"
                                    )
                            )
                            .instanceCount(
                                    instanceCount
                            )
                            .study(study)
                            .build();

            /*
             * 양방향 관계 설정
             */
            study.getSeriesList().add(series);

            /*
             * cascade 설정이 있으므로 Study를 저장해도 되지만,
             * 여기서는 Series 저장을 명시적으로 수행
             */

            // DB에 Series 저장
            pacsSeriesRepository.save(series);

            // Study에 전체 InstanceCount 를 저장하기 위해 합산한다.
            totalInstanceCount += instanceCount;
        }

        // Study 데이터 수정
        study.setSeriesCount(
                study.getSeriesList().size()
        );

        study.setInstanceCount(
                totalInstanceCount
        );

        // Study에 데이터 SeriesCount, InstanceCount 계산한 내용으로 수정한다.
        pacsStudyRepository.save(study);
    }


    private Map<String, Object> getSeriesFromOrthanc(
            String orthancSeriesId
    ) {

        return orthancWebClient
                .get()
                .uri(
                        "/series/{id}",
                        orthancSeriesId
                )
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<
                                Map<String, Object>
                                >() {
                        }
                )
                .block();
    }
} // PacsServiceImpl 클래스의 끝
