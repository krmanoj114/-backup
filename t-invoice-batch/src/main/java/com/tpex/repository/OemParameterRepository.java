package com.tpex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.entity.OemParameterEntity;

public interface OemParameterRepository extends JpaRepository<OemParameterEntity, String> {
	
	public Optional<OemParameterEntity> findByOprParaCd(String paraCode);
	
}
