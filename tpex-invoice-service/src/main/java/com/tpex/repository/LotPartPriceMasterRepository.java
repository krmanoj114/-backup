package com.tpex.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemLotPartPrcMstEntity;

import io.lettuce.core.dynamic.annotation.Param;

@SuppressWarnings("squid:S107")
@Repository
public interface LotPartPriceMasterRepository extends JpaRepository<OemLotPartPrcMstEntity, String>{

	@Query(value = "select * from TB_M_LOT_PART_PRICE "
			+ "where CF_CD=:carFamily and DST_CD=:finalDestination and LOT_CD=:lotCode and "
			+ "CURR_CD=:currency and EFF_FR_MTH=:effectiveFromMonth and EFF_TO_MTH=:effectiveToMonth", nativeQuery = true)
	public List<OemLotPartPrcMstEntity> findLotPartPriceDetails(@Param("effectiveFromMonth") String effectiveFromMonth, @Param("effectiveToMonth") String effectiveToMonth, @Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode, @Param("currency") String currency);

	@Query(value = "select * from TB_M_LOT_PART_PRICE where CF_CD=:carFamily and DST_CD=:finalDestination and LOT_CD=:lotCode and CURR_CD=:currency and EFF_FR_MTH=:effectiveFromMonth and EFF_TO_MTH=:effectiveToMonth and PART_NO=:partNo", nativeQuery = true)
	public OemLotPartPrcMstEntity findNameByReq(@Param("carFamily") String carFamily,
			@Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode,
			@Param("currency") String currency, @Param("effectiveFromMonth") String effectiveFromMonth,
			@Param("effectiveToMonth") String effectiveToMonth, @Param("partNo") String partNo );

	@Modifying
	@Query(value = "update TB_M_LOT_PART_PRICE set PART_NM=:trimmedPartName, UPD_BY=:updateBy, UPD_DT=:updateDate where CF_CD=:carFamily and DST_CD=:finalDestination and LOT_CD=:lotCode and CURR_CD=:currency and EFF_FR_MTH=:effectiveFromMonth and EFF_TO_MTH=:effectiveToMonth and PART_NO=:partNo", nativeQuery = true)
	public Integer updatePartPriceName(@Param("trimmedPartName") String trimmedPartName,
			@Param("effectiveFromMonth") String effectiveFromMonth,
			@Param("effectiveToMonth") String effectiveToMonth, @Param("finalDestination") String finalDestination,
			@Param("carFamily") String carFamily, @Param("currency") String currency, @Param("lotCode") String lotCode,
			@Param("partNo") String partNo, @Param("updateBy") String updateBy,LocalDate  updateDate);

	@Modifying(clearAutomatically = true)
	@Query(value = "update TB_M_LOT_PART_PRICE set PART_PRC=:partPrice, UPD_BY=:updateBy, UPD_DT=:updateDate where CF_CD=:carFamily and DST_CD=:finalDestination and LOT_CD=:lotCode and CURR_CD=:currency and EFF_FR_MTH=:effectiveFromMonth and EFF_TO_MTH=:effectiveToMonth and PART_NO=:partNo", nativeQuery = true)
	public Integer updatePartPriceInPartMst( @Param("partPrice") Double partPrice, @Param("effectiveFromMonth") String effectiveFromMonth,
			@Param("effectiveToMonth") String effectiveToMonth, @Param("finalDestination") String finalDestination,
			@Param("carFamily") String carFamily, @Param("currency") String currency, @Param("lotCode") String lotCode,
			@Param("partNo") String partNo, @Param("updateBy") String updateBy, @Param("updateDate") LocalDate  updateDate);

	@Modifying(clearAutomatically = true)
	@Query(value = "update TB_M_LOT_PART_PRICE set PART_PRC=:partPrice, PUSAGE=:partusage, UPD_BY=:updateBy, UPD_DT=:updateDate where CF_CD=:carFamily and DST_CD=:finalDestination and LOT_CD=:lotCode and CURR_CD=:currency and EFF_FR_MTH=:effectiveFromMonth and EFF_TO_MTH=:effectiveToMonth and PART_NO=:partNo", nativeQuery = true)
	public Integer updatePartPriceUsage( @Param("partusage")
	Double partusage,@Param("partPrice") Double partPrice, @Param("effectiveFromMonth") String effectiveFromMonth,
			@Param("effectiveToMonth") String effectiveToMonth, @Param("finalDestination") String finalDestination,
			@Param("carFamily") String carFamily, @Param("currency") String currency, @Param("lotCode") String lotCode,
			@Param("partNo") String partNo, @Param("updateBy") String updateBy, @Param("updateDate") LocalDate  updateDate);
	

}
