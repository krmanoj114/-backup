package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.entity.ProcessLogDtlsEntity;

public interface ProcessLogDtlsRepository extends JpaRepository<ProcessLogDtlsEntity, Integer> {
	
	ProcessLogDtlsEntity findTopByOrderByIdDesc();
	
	List<ProcessLogDtlsEntity> findAllByProcessControlIdAndProcessId(int processControlId, String processId);
	
	ProcessLogDtlsEntity findTopByOrderByProcessControlIdDesc();
}
