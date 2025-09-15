package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.CountryMasterEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CountryMasterRepository extends JpaRepository<CountryMasterEntity, String> {
	
	@Query(value = "SELECT COUNT(COUNTRY_NAME) FROM TB_M_COUNTRY_CODE where COUNTRY_NAME = :countryName and COUNTRY_CODE != :countryCode", nativeQuery = true)
	int getCountOfCountryName(@Param("countryName") String countryName,@Param("countryCode") String countryCode);
	
	@Query(value = "SELECT COUNT(COUNTRY_CODE) FROM TB_M_COUNTRY_CODE where COUNTRY_CODE= :countryCode and  COUNTRY_NAME != :countryName", nativeQuery = true)
	int getCountOfCountryCode(@Param("countryCode") String countryCode,@Param("countryName") String countryName);
	
	@Query(value = "SELECT COUNT(COUNTRY_NAME) FROM TB_M_COUNTRY_CODE where COUNTRY_NAME = :countryName and COUNTRY_CODE = :countryCode", nativeQuery = true)
	int getCountOfCountryNameAndCode(@Param("countryName") String countryName,@Param("countryCode") String countryCode);

	long countByCountryCode(String string);
	
}
