package com.webjjang.api.pacs.repository;

import com.webjjang.api.pacs.entity.PacsStudy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacsStudyRepository
extends JpaRepository<PacsStudy, Long> {

    Optional<PacsStudy> findByOrthancStudyId(String orthancStudyId);

    Optional<PacsStudy> findByStudyInstanceUID(String studyInstanceUID);

    boolean existsByOrthancStudyId(String orthancStudyId);

    boolean existsByStudyInstanceUID(String studyInstanceUID);


}
