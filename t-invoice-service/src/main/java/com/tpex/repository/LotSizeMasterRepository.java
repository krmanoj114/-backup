package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemLotSizeMstEntity;
import com.tpex.entity.OemLotSizeMstIDEntity;


@Repository
public interface LotSizeMasterRepository extends JpaRepository<OemLotSizeMstEntity, OemLotSizeMstIDEntity>{

	List<OemLotSizeMstEntity> findByCarFamilyCodeAndLotModImpAndLotCodeAndPartNumber(String carFamily,
			String finalDestination, String lotCode, String partNo);

	List<OemLotSizeMstEntity> findByCarFamilyCodeAndLotModImpAndLotCode(String carFamily, String finalDestination,
			String lotCode);

}
