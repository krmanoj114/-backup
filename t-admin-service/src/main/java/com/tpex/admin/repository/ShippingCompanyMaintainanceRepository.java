package com.tpex.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.ShippingCompanyMasterEntity;

@Repository
public interface ShippingCompanyMaintainanceRepository extends JpaRepository<ShippingCompanyMasterEntity, String>{

}
