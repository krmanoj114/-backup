package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.entity.CarFamilyDestinationMasterIdEntity;

@Repository
public interface CarFamilyDestinationMasterRepository extends JpaRepository<CarFamilyDestinationMasterEntity, CarFamilyDestinationMasterIdEntity>{

	List<CarFamilyDestinationMasterEntity> findByIdCarFmlyCodeAndIdDestinationCode(String carFmlyCode, String destCode);
    
	@Query(value="select SRS_NM from TB_M_CAR_FAMILY_DESTINATION where DST_CD= :destCode and CF_CD= :carFmlyCode and REXP_CD= :reExporterCode",nativeQuery=true)
	String findReExporterName(String destCode, String carFmlyCode, String reExporterCode);

	List<CarFamilyDestinationMasterEntity> findAllByOrderBySrsNameAsc();
	
	List<CarFamilyDestinationMasterEntity> findDistinctByIdDestinationCodeIn(List<String> importerCode);

}
