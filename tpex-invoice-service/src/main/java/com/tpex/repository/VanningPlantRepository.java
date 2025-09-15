package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.VanningPlantEntity;

@Repository

public interface VanningPlantRepository extends JpaRepository<VanningPlantEntity, String> {
	
	List<VanningPlantEntity> findByCompanyCode(String companyCode);
	List<VanningPlantEntity> findByCompanyCodeAndPackingFlag(String companyCode, String packingFlag);
	List<VanningPlantEntity> findByCompanyCodeAndVanningFlag(String companyCode,String vanningFlag);
	
	
	

}
