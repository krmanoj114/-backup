package com.tpex.admin.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.CountryOriginMstEntity;

@Repository
public interface CountryOriginMstRepository extends JpaRepository<CountryOriginMstEntity, String> {

	@Query(value = "Select PART_NO, PART_NM, CNTRY_ORG, EFF_FROM, EFF_TO FROM tb_m_country_origin WHERE CNTRY_ORG IN (:coutryCode) AND (:partPriceNo is null or PART_NO = :partPriceNo) AND EFF_FROM < EFF_TO;", nativeQuery = true)
	List<String[]> getPartNameDetails(@Param("partPriceNo") String partPriceNo, @Param("coutryCode") List<String> coutryCode);
	
	@Query(value = "Select PART_NO, PART_NM, CNTRY_ORG, EFF_FROM, EFF_TO FROM tb_m_country_origin WHERE EFF_FROM BETWEEN :vanDateFrom AND (SELECT MAX(EFF_TO) FROM tb_m_country_origin ) ", nativeQuery = true)
	List<String[]> getPartNameDetailsForVanDateFrom(@Param("vanDateFrom") Date vanDateFrom);

	@Query(value = "Select PART_NO, PART_NM, CNTRY_ORG, EFF_FROM, EFF_TO FROM tb_m_country_origin WHERE EFF_TO BETWEEN (SELECT MIN(EFF_TO) FROM tb_m_country_origin) AND :vanDateToo", nativeQuery = true)
	List<String[]> getPartNameDetailsForVanDateTo(@Param("vanDateToo") Date vanDateToo); 
    
	@Query(value = "Select PART_NO, PART_NM, CNTRY_ORG, EFF_FROM, EFF_TO FROM tb_m_country_origin WHERE PART_NO = :partPriceNo", nativeQuery = true)
	List<String[]> getPartNameDetailsForPartNo(@Param("partPriceNo") String partPriceNo);
	
	@Query(value = "Select PART_NO, PART_NM, CNTRY_ORG, EFF_FROM, EFF_TO FROM tb_m_country_origin WHERE CNTRY_ORG IN (:coutryCode)", nativeQuery = true)
	List<String[]> getPartNameDetailsForCountryCode(@Param("coutryCode") List<String> coutryCode);
	
	@Query(value = "Select PART_NO, PART_NM, CNTRY_ORG, EFF_FROM, EFF_TO FROM tb_m_country_origin WHERE CNTRY_ORG IN (:coutryCode) AND (:partPriceNo is null or PART_NO = :partPriceNo) AND (STR_TO_DATE(:vanDateFrom,'%d/%m/%Y') IS NULL OR EFF_FROM  = STR_TO_DATE(:vanDateFrom,'%d/%m/%Y') < STR_TO_DATE(:vanDateTo,'%d/%m/%Y') IS NULL OR EFF_TO  = STR_TO_DATE(:vanDateTo,'%d/%m/%Y'));", nativeQuery = true)
	List<String[]> getPartNameDetailsForOtherCase(@Param("partPriceNo") String partPriceNo, @Param("coutryCode") List<String> coutryCode, @Param("vanDateFrom") Date vanDateFrom, @Param("vanDateTo") Date vanDateTo);
	
	@Query(value = "Select PART_NO, PART_NM, CNTRY_ORG, EFF_FROM, EFF_TO FROM tb_m_country_origin;", nativeQuery = true)
	List<String[]> getPartNameDetailsForAllData();
}
