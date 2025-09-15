package com.tpex.repository;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemLotPrcMstEntity;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface LotPriceMasterRepository extends JpaRepository<OemLotPrcMstEntity, String> {

	@Query(value = "select EFF_FR_MTH, EFF_TO_MTH, LOT_CD, LOT_PRC, CURR_CD, DESCRIPTION from TB_M_LOT_PRICE INNER JOIN TB_M_CURRENCY ON TB_M_LOT_PRICE.CURR_CD= TB_M_CURRENCY.CD\r\n"
			+ "where CF_CD=:carFamily and DST_CD=:finalDestination and EFF_FR_MTH>=:effectiveFromMonth ORDER BY EFF_FR_MTH DESC, EFF_TO_MTH DESC, LOT_CD ASC"
			, nativeQuery = true)
	public List<Tuple> findLotPriceDetails(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, @Param("effectiveFromMonth") String effectiveFromMonth);

	@Modifying(clearAutomatically = true)
	@Query(value ="update TB_M_LOT_PRICE set LOT_PRC=:lotPrice, UPD_BY=:updateBy, UPD_DT=:updateDate where CF_CD=:carFamily and DST_CD=:finalDestination and LOT_CD=:lotCode and CURR_CD=:currency and EFF_FR_MTH=:effectiveFromMonth and EFF_TO_MTH=:effectiveToMonth", nativeQuery = true) 
	public Integer updateLotPrice(@Param("carFamily") String  carFamily, @Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode, @Param("currency") String currency,@Param("effectiveFromMonth") String effectiveFromMonth, @Param("effectiveToMonth") String effectiveToMonth, @Param("lotPrice") Double lotPrice, @Param("updateBy") String updateBy, @Param("updateDate") LocalDate  updateDate);

}
