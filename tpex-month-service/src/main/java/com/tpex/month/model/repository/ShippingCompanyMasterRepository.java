package com.tpex.month.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.month.model.entity.ShippingCompanyMasterEntity;
import com.tpex.month.model.projection.ShippingCompanyMasterCode;

public interface ShippingCompanyMasterRepository extends JpaRepository<ShippingCompanyMasterEntity, String> {
	
	public List<ShippingCompanyMasterCode> findAllProjectedCodeByOrderByScmCd();
}

