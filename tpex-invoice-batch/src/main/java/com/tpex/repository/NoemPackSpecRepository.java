package com.tpex.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.NoemVprPkgSpecEntity;

/**
 * The Interface NoemPackSpecRepository.
 */
@Repository
public interface NoemPackSpecRepository extends JpaRepository<NoemVprPkgSpecEntity, Integer>{

	/**
	 * Find spec details.
	 *
	 * @param carFamily the car family
	 * @param finalDestination the final destination
	 * @param lotCode the lot code
	 * @param partNo the part no
	 * @param effectiveFromMonth the effective from month
	 * @param effectiveToMonth the effective to month
	 * @return the list
	 */
	@Query(value ="select QTY_BOX from TB_M_SPEC_VPR_PKG where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination and LOT_CD=:lotCode and PART_NO=:partNo and date_format(EFF_FROM,'%Y%m')>=:effectiveFromMonth and date_format(EFF_TO,'%Y%m')<=:effectiveToMonth", nativeQuery= true)
	List<Integer> findSpecDetails(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode, @Param("partNo") String partNo,
			@Param("effectiveFromMonth") String effectiveFromMonth, @Param("effectiveToMonth") String effectiveToMonth);

	/**
	 * Find pck spec details.
	 *
	 * @param carFamily the car family
	 * @param finalDestination the final destination
	 * @param lotCode the lot code
	 * @param partNo the part no
	 * @param effectiveFromMonth the effective from month
	 * @return the list
	 */
	@Query(value ="select QTY_BOX from TB_M_SPEC_VPR_PKG where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination and LOT_CD=:lotCode and PART_NO=:partNo and date_format(EFF_FROM,'%Y%m')>=:effectiveFromMonth", nativeQuery= true)
	List<Integer> findPckSpecDetails(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode, @Param("partNo") String partNo,
			@Param("effectiveFromMonth") String effectiveFromMonth);

	/**
	 * Gets the count using lot key.
	 *
	 * @param carFamily the car family
	 * @param finalDestination the final destination
	 * @param lotCode the lot code
	 * @param effectiveFromMonth the effective from month
	 * @return the count using lot key
	 */
	@Query(value ="select count(*) from TB_M_SPEC_VPR_PKG where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination and LOT_CD=:lotCode "
			+ "and date_format(EFF_FROM,'%Y%m')>=:effectiveFromMonth", nativeQuery= true)
	long getCountUsingLotKey(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, 
			@Param("lotCode") String lotCode, @Param("effectiveFromMonth") String effectiveFromMonth);
	
	/**
	 * Gets the count using lot part key.
	 *
	 * @param carFamily the car family
	 * @param finalDestination the final destination
	 * @param lotCode the lot code
	 * @param partNo the part no
	 * @param effectiveFromMonth the effective from month
	 * @return the count using lot part key
	 */
	@Query(value ="select count(*) from TB_M_SPEC_VPR_PKG where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination and LOT_CD=:lotCode "
			+ "and PART_NO=:partNo and date_format(EFF_FROM,'%Y%m')>=:effectiveFromMonth", nativeQuery= true)
	long getCountUsingLotPartKey(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, 
			@Param("lotCode") String lotCode, @Param("partNo") String partNo, @Param("effectiveFromMonth") String effectiveFromMonth);
	
	/**
	 * Find all by nvps cf cd and nvps lot cd and nvps mod imp cd.
	 *
	 * @param nvpsCfCd the nvps cf cd
	 * @param nvpsLotCd the nvps lot cd
	 * @param nvpsModImpCd the nvps mod imp cd
	 * @return the list
	 */
	List<NoemVprPkgSpecEntity> findAllByNvpsCfCdAndNvpsLotCdAndNvpsModImpCd(String nvpsCfCd, String nvpsLotCd, String nvpsModImpCd);
	
	@Query(value ="select distinct PART_NO from TB_M_SPEC_VPR_PKG where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination and LOT_CD=:lotCode", nativeQuery= true)
	List<String> findPartNoByNvpsCfCdAndNvpsLotCdAndNvpsModImpCd(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, 
			@Param("lotCode") String lotCode);

	@Query(value ="select distinct PART_NO from TB_M_SPEC_VPR_PKG where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination and LOT_CD IS NULL and PART_NO LIKE concat(:partNo, '%') and date_format(EFF_FROM,'%Y/%m')>=:effectiveFromMonth and date_format(EFF_TO,'%Y/%m')<=:effectiveToMonth", nativeQuery= true)
	List<String> findPartNoList(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, @Param("partNo") String partNo,
			@Param("effectiveFromMonth") String effectiveFromMonth, @Param("effectiveToMonth") String effectiveToMonth);

	@Query(value ="select distinct PART_NO from TB_M_SPEC_VPR_PKG where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination", nativeQuery= true)
	List<String> findPartNoByNvpsCfCdAndNvpsModImpCd(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination);
	
}
