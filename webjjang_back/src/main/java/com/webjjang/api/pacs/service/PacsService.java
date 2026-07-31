package com.webjjang.api.pacs.service;

import com.webjjang.api.pacs.vo.StudyVO;

import java.util.List;

public interface PacsService {

    // Pacs study 데이터 리스트
    List<StudyVO> getStudyList();

    // 상세보기 메서드
    StudyVO getStudyDetail(String studyId);
}
