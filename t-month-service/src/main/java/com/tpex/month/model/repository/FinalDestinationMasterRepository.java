package com.tpex.month.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.month.model.entity.FinalDestinationMasterEntity;
import com.tpex.month.model.projection.FinalDestinationMasterCodeAndName;

public interface FinalDestinationMasterRepository extends JpaRepository<FinalDestinationMasterEntity, String> {

	public List<FinalDestinationMasterCodeAndName> findAllProjectedCodeAndNameByOrderByFdDstCd();
	
}

