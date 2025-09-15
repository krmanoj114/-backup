package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.PackingAndVanningEntity;

@Repository

public interface PackingAndVanningRepository extends JpaRepository<PackingAndVanningEntity, String> {

	List<PackingAndVanningEntity> findByCmpCode(String companyCode);
	
	
	
	

}
