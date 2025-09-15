package com.tpex.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.ReturnablePackingMasterEntity;
import com.tpex.entity.ReturnablePackingMasterIdEntity;
@Repository

public interface ReturnablePackingMasterDetailsRepository
		extends JpaRepository<ReturnablePackingMasterEntity, ReturnablePackingMasterIdEntity> {

	@Query(value = "SELECT PLNT_CD,IMP_CD,MOD_TYP,MOD_DESC,VAN_FROM,VAN_TO FROM TB_M_RRACK_MOD_TYPE "
			+ "WHERE (:packingPlant IS NULL OR PLNT_CD = :packingPlant) AND (:moduleType IS NULL OR MOD_TYP= :moduleType) AND (:cmpCd IS NULL OR CMP_CD= :cmpCd) AND RRACK_TYP=:returnableType AND (:vanDateFrom IS NULL OR VAN_FROM>=:vanDateFrom) AND (:vanDateTo IS NULL OR VAN_TO<= :vanDateTo) AND "
			+ "(COALESCE( null, :importerCode ) is null or IMP_CD  IN (:importerCode)) ORDER BY PLNT_CD,IMP_CD,MOD_TYP ASC", nativeQuery = true)
	List<Object[]> getData(@Param("packingPlant") String packingPlant, @Param("moduleType") String moduleType,
			@Param("cmpCd") String cmpCd, @Param("returnableType") String returnableType,
			@Param("vanDateFrom") String vanDateFrom, @Param("vanDateTo") String vanDateTo,
			@Param("importerCode") List<String> importerCode);

	List<ReturnablePackingMasterEntity> findByIdPlantCdAndIdImpCdAndIdModType(String packingPlant, String importerCode,
			String moduleType);

	long countByIdPlantCdAndIdImpCdAndIdModType(String packingPlant, String importerCode, String moduleType);
	
	@Query(value = "select count(VAN_FROM)  FROM TB_M_RRACK_MOD_TYPE where VAN_FROM = :fromDate ", nativeQuery = true)
	int validateVanningDateFrom(@Param("fromDate") LocalDate fromDate);

	@Query(value = "select count(VAN_TO)  FROM TB_M_RRACK_MOD_TYPE where VAN_TO = :toDate", nativeQuery = true)
	int validateVanningDateTo(@Param("toDate") LocalDate toDate);

}
