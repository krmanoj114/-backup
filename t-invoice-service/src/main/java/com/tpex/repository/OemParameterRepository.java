package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemParameterEntity;

@Repository
public interface OemParameterRepository extends JpaRepository<OemParameterEntity, String> {
	
	@Query(value = "SELECT IFNULL(PARA_VAL,'N') PARAM_VAL FROM TB_M_PARAMETER WHERE PARA_CD='CNTRY_ORG'", nativeQuery = true)
	String getCountryOfOriginFlg();

	@Query(value="SELECT PARA_VAL FROM TB_M_PARAMETER WHERE PARA_CD = 'PART_MAT_CD'",nativeQuery = true)
	String getParameter();
}
