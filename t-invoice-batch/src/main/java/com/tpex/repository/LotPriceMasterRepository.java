package com.tpex.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemLotPrcMstEntity;
import com.tpex.entity.OemLotPrcMstEntityID;

@Repository
public interface LotPriceMasterRepository extends JpaRepository<OemLotPrcMstEntity, OemLotPrcMstEntityID> {

	@Query(value = "select EFF_FR_MTH, EFF_TO_MTH, LOT_CD, LOT_PRC, CURR_CD, DESCRIPTION from TB_M_LOT_PRICE INNER JOIN TB_M_CURRENCY ON TB_M_LOT_PRICE.CURR_CD= TB_M_CURRENCY.CD\r\n"
			+ "where CF_CD=:carFamily and DST_CD=:finalDestination and EFF_FR_MTH>=:effectiveFromMonth ORDER BY EFF_FR_MTH DESC, EFF_TO_MTH DESC, LOT_CD ASC"
			, nativeQuery = true)
	public List<Tuple> findLotPriceDetails(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, @Param("effectiveFromMonth") String effectiveFromMonth);

}
