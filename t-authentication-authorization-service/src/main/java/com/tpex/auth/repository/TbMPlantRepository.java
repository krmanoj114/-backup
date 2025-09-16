package com.tpex.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.auth.entity.TbMPlantEntity;

public interface TbMPlantRepository extends JpaRepository<TbMPlantEntity, String> {

	public List<TbMPlantEntity> findByCompanyCode(String companyCode);
	
}
