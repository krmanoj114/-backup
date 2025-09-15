package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InfErrorLogEntity;

@Repository
public interface InfErrorLogRepository extends JpaRepository<InfErrorLogEntity, Integer> {
	
	InfErrorLogEntity findTopByOrderByLogIdDesc();

}
