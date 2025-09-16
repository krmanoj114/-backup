package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.CarFamilyDestinationMasterEntity;
import com.tpex.admin.entity.CarFamilyDestinationMasterIdEntity;

@Repository
public interface CarFamilyDestinationMasterRepository extends JpaRepository<CarFamilyDestinationMasterEntity, CarFamilyDestinationMasterIdEntity>{

	List<CarFamilyDestinationMasterEntity> findByIdCarFmlyCodeAndIdDestinationCode(String carFmlyCode, String destCode);

	List<CarFamilyDestinationMasterEntity> findAllByOrderBySrsNameAsc();

	List<CarFamilyDestinationMasterEntity> findAllByCompanyCode(String cmpCode);

}
