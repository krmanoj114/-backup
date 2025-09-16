package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpex.admin.entity.ProcessLogDtlsEntity;

public interface ProcessLogDtlsRepository extends JpaRepository<ProcessLogDtlsEntity, Integer> {

	ProcessLogDtlsEntity findTopByOrderByIdDesc();

	List<ProcessLogDtlsEntity> findAllByProcessControlIdAndProcessId(int processControlId, String processId);

}
