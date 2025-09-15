package com.tpex.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.InvNoEntity;

@Repository
	public interface InvNoRepository extends JpaRepository<InvNoEntity, String> {

	@Query(value = "select ixos_flg from  tb_r_inv_invoice_h where INV_NO=:invoiceNumber", nativeQuery = true)
	String findIxosNumber(@Param("invoiceNumber") String invoiceNumber);  
	
	@Query(value = "select distinct ixos_flg from  tb_r_inv_invoice_h where HAISEN_NO=:haisenNo and ixos_flg='Y'", nativeQuery = true)
	String findIxosNumberForHaisenNo(@Param("haisenNo") String haisenNo); 
		
	}


