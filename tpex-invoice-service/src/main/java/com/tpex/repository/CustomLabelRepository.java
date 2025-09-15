package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.entity.CustomLabelEntity;

public interface CustomLabelRepository extends JpaRepository<CustomLabelEntity, String> {
	
	List<CustomLabelEntity> findByCmpCode(String cmpCode);
	

}
