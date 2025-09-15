package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.ReturnablePackingMasterEntity;
import com.tpex.entity.ReturnablePackingMasterIdEntity;

@Repository
public interface ReturnablePackingMasterDetailsRepository extends JpaRepository<ReturnablePackingMasterEntity, ReturnablePackingMasterIdEntity>{
	
	List<ReturnablePackingMasterEntity> findByIdPlantCdAndIdImpCdAndIdModType(String packingPlant, String importerCode, String moduleType);

}
