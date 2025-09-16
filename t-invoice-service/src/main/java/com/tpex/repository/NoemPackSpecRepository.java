package com.tpex.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tpex.entity.NoemVprPkgSpecEntity;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface NoemPackSpecRepository extends JpaRepository<NoemVprPkgSpecEntity, String>{

	@Query(value ="select QTY_BOX from TB_M_SPEC_VPR_PKG where CF_CD=:carFamily and MOD_IMP_CD=:finalDestination and LOT_CD=:lotCode and PART_NO=:partNo and date_format(EFF_FROM,'%Y%m')>=:effectiveFromMonth and date_format(EFF_TO,'%Y%m')<=:effectiveToMonth", nativeQuery= true)
	List<Integer> findSpecDetails(@Param("carFamily") String carFamily, @Param("finalDestination") String finalDestination, @Param("lotCode") String lotCode, @Param("partNo") String partNo,
			@Param("effectiveFromMonth") String effectiveFromMonth, @Param("effectiveToMonth") String effectiveToMonth);

}
