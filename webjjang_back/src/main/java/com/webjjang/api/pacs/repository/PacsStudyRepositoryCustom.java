package com.webjjang.api.pacs.repository;

import com.webjjang.api.pacs.vo.StudyVO;

public interface PacsStudyRepositoryCustom {

    /**
     * Study 상세 조회
     *
     * @param studyId Orthanc Study ID
     * @return Study 상세 정보
     */
    StudyVO getStudyDetail(String studyId);

}
