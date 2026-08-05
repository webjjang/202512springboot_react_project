package com.webjjang.api.pacs.service;

import com.webjjang.api.pacs.vo.StudySaveResultVO;
import com.webjjang.api.pacs.vo.StudyVO;

import java.util.List;

public interface PacsService {

    // Pacs DB - 데이터 리스트
    List<StudyVO> getStudyList();

    // DB - 상세보기 메서드
    StudyVO getStudyDetail(String studyId);

    // DB 내용 수정 - orthanc Pacs DICOM 데이터는 불러오더라도 덮어쓰기 안됨.
    StudyVO updateStudyInfo(Long no, StudyVO updateVO);

    // orthanc Pacs 서버에서 데이터 가져와서 DB에 저장하기
    StudySaveResultVO saveStudyFromOrthanc(String orthancStudyId);

}
