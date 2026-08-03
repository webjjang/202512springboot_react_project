package com.webjjang.api.pacs.repository;

import com.webjjang.api.pacs.entity.PacsPatient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacsPatientRepository
extends JpaRepository<PacsPatient, Long> {

    // findById -> @Id를 붙어 놓은 프로퍼티 사용 : 기본이므로 작성하지 않고 사용.
    // PacsPatient getByOrthancPatientId()
    Optional<PacsPatient> findByOrthancPatientId(String orthancPatientId);

}
