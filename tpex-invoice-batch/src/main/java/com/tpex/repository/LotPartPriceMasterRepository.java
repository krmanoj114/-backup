package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemLotPartPrcMstEntity;

/**
 * The Interface LotPartPriceMasterRepository.
 */
@Repository
public interface LotPartPriceMasterRepository extends JpaRepository<OemLotPartPrcMstEntity, String>{

	/**
	 * Find lot part price details.
	 *
	 * @param effectiveFromMonth the effective from month
	 * @param effectiveToMonth the effective to month
	 * @param carFamily the car family
	 * @param finalDestination the final destination
	 * @param lotCode the lot code
	 * @param currency the currency
	 * @return the list
	 */
	@Query(value = "select * from TB_M_LOT_PART_PRICE "
			+ "where CF_CD=:carFamily and DST_CD=:finalDestination and LOT_CD=:lotCode and "
			+ "CURR_CD=:currency and EFF_FR_MTH=:effectiveFromMonth and EFF_TO_MTH=:effectiveToMonth", nativeQuery = true)
	public List<OemLotPartPrcMstEntity> findLotPartPriceDetails(@Param("effectiveFromMonth") String effectiveFromMonth, @Param("effectiveToMonth") String effectiveToMonth, @Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode, @Param("currency") String currency);

	/**
	 * Find name by req.
	 *
	 * @param carFamily the car family
	 * @param finalDestination the final destination
	 * @param lotCode the lot code
	 * @param currency the currency
	 * @param effectiveFromMonth the effective from month
	 * @param effectiveToMonth the effective to month
	 * @param partNo the part no
	 * @return the oem lot part prc mst entity
	 */
	@Query(value = "select * from TB_M_LOT_PART_PRICE where CF_CD=:carFamily and DST_CD=:finalDestination and LOT_CD=:lotCode and CURR_CD=:currency and EFF_FR_MTH=:effectiveFromMonth and EFF_TO_MTH=:effectiveToMonth and PART_NO=:partNo", nativeQuery = true)
	public OemLotPartPrcMstEntity findNameByReq(@Param("carFamily") String carFamily,
			@Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode,
			@Param("currency") String currency, @Param("effectiveFromMonth") String effectiveFromMonth,
			@Param("effectiveToMonth") String effectiveToMonth, @Param("partNo") String partNo );

}
