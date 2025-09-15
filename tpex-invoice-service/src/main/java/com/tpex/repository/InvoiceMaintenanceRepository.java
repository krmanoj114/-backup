package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tpex.dto.InvoiceMaintenanceDTO;
import com.tpex.entity.InsInvDtlsEntity;

/**
 * The Interface InvoiceMaintenanceRepository.
 */
@Repository
public interface InvoiceMaintenanceRepository extends JpaRepository<InsInvDtlsEntity, String> {

	@Query(value = "SELECT DISTINCT ORD_TYP FROM TB_R_INV_INVOICE_H", nativeQuery = true)
	List<String> getOrderTypes();

	@Query(value = "SELECT DISTINCT INV_NO FROM TB_R_INV_INVOICE_H where CANCEL_FLG !='Y' and PLS_SND_DT is not null", nativeQuery = true)
	List<String> getInvoiceNo();

	List<InsInvDtlsEntity> findByIndCancelFlagEqualsAndIndPlsSndDtNotNullOrderByIndInvNo(String flag) ;
	
	@Query(value = "SELECT DISTINCT INV_NO FROM TB_R_INV_INVOICE_H", nativeQuery = true)
	List<String> getAllInvoiceNo();

}