package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemProgDtlsEntity;
import com.tpex.entity.OemProgDtlsIdEntity;

@Repository
public interface OemProgDtlsRepository extends JpaRepository<OemProgDtlsEntity, OemProgDtlsIdEntity> {


	OemProgDtlsEntity findTopByIdProgramId(String programId);

	
}