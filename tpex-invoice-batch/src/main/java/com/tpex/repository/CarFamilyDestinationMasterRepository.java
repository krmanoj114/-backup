package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.entity.CarFamilyDestinationMasterIdEntity;

@Repository
public interface CarFamilyDestinationMasterRepository extends JpaRepository<CarFamilyDestinationMasterEntity, CarFamilyDestinationMasterIdEntity>{

}