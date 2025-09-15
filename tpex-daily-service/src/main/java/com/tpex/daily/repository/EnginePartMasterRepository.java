package com.tpex.daily.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.daily.entity.EnginePartMasterEntity;
import com.tpex.daily.entity.EnginePartMasterIdEntity;

@Repository
public interface EnginePartMasterRepository extends JpaRepository<EnginePartMasterEntity, EnginePartMasterIdEntity> {

	long countByIdCrFmlyCodeAndIdImporterCodeAndIdExporterCodeAndIdLotModuleCodeAndIdPartNo(String crFmlyCode,
			String importerCode, String exporterCode, String lotModuleCode, String partNo);

}
