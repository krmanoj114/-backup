package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.AddressMasterEntity;
import com.tpex.entity.AddressMasterIdEntity;

@Repository
public interface AddressMasterRepository extends JpaRepository<AddressMasterEntity, AddressMasterIdEntity>{

	long countByIdCmpCode(String cmpCode);
	
	long countByInvoiceFlagAndCompanyCode(String invoiceFlag, String companyCode);
}
