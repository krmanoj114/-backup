package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemLotSizeMstEntity;
import com.tpex.entity.OemLotSizeMstIDEntity;

/**
 * The Interface LotSizeMasterRepository.
 */
@Repository
public interface LotSizeMasterRepository extends JpaRepository<OemLotSizeMstEntity, OemLotSizeMstIDEntity>{

	/**
	 * Find lot size 1.
	 *
	 * @param carFamily the car family
	 * @param lotModImp the lot mod imp
	 * @param lotCode the lot code
	 * @param partNumber the part number
	 * @return the oem lot size mst entity
	 */
	@Query(value ="select CF_CD, MOD_IMP_CD, LOT_CD, PART_NO, LOT_SIZE, CMP_CD from TB_M_LOT_SIZE where CF_CD=:carFamily and MOD_IMP_CD=:lotModImp and LOT_CD=:lotCode and PART_NO=:partNumber", nativeQuery= true)
	OemLotSizeMstEntity findLotSize1(@Param("carFamily") String carFamily, @Param("lotModImp") String lotModImp, @Param("lotCode") String lotCode, @Param("partNumber") String partNumber);

	/**
	 * Find lot size 2.
	 *
	 * @param carFamily the car family
	 * @param finalDestination the final destination
	 * @param lotCode the lot code
	 * @return the oem lot size mst entity
	 */
	@Query(value ="select CF_CD, MOD_IMP_CD, LOT_CD, PART_NO, LOT_SIZE, CMP_CD from TB_M_LOT_SIZE where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination and LOT_CD=:lotCode", nativeQuery= true)
	OemLotSizeMstEntity findLotSize2(@Param("carFamily") String carFamily,@Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode);

	/**
	 * Count by lot code.
	 *
	 * @param lotCode the lot code
	 * @return the long
	 */
	long countByLotCode(String lotCode);
	
	/**
	 * Find top by lot mod imp and car family code and lot code.
	 *
	 * @param lotModImp the lot mod imp
	 * @param carFamilyCode the car family code
	 * @param lotCode the lot code
	 * @return the oem lot size mst entity
	 */
	OemLotSizeMstEntity findTopByLotModImpAndCarFamilyCodeAndLotCode(String lotModImp, String carFamilyCode, String lotCode);

}
