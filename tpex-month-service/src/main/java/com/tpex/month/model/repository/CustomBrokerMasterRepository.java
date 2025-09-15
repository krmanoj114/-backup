package com.tpex.month.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.month.model.entity.CustomBrokerMasterEntity;
import com.tpex.month.model.projection.CustomBrokerMasterCodeAndName;

public interface CustomBrokerMasterRepository extends JpaRepository<CustomBrokerMasterEntity, String> {
	
	public List<CustomBrokerMasterCodeAndName> findAllProjectedCodeAndNameByOrderByCbmCbCd();
}
