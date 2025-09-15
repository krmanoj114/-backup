package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.PlantMstEntity;


@Repository
public interface PlantMstRepository extends JpaRepository<PlantMstEntity, String> {

	List<PlantMstEntity> findByCmpCdOrderByPlantCdAsc(String cmpCd);
}
