package com.tpex.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.OemProgDtlsEntity;
import com.tpex.admin.entity.OemProgDtlsIdEntity;
@Repository
public interface OemProgDtlsRepository extends JpaRepository<OemProgDtlsEntity, OemProgDtlsIdEntity> {


	OemProgDtlsEntity findByIdProgramId(String programId);

	
}