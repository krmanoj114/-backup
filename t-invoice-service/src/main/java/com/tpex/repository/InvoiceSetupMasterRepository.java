package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InvoiceSetupMasterEntity;

@Repository
public interface InvoiceSetupMasterRepository extends JpaRepository<InvoiceSetupMasterEntity,Integer>{
	
	List<InvoiceSetupMasterEntity> findBySetupTypeAndDestinationCodeIn(String setupType, List<String> importerCode);

	InvoiceSetupMasterEntity findBySetupTypeAndDestinationCode(String setupType, String importerCode);
}
