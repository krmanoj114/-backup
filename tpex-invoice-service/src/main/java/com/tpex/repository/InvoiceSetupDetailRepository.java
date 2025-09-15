package com.tpex.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InvoiceSetupDetailEntity;

@Repository
public interface InvoiceSetupDetailRepository extends JpaRepository<InvoiceSetupDetailEntity,Integer>{

	List<InvoiceSetupDetailEntity> findByInvSetupId(Integer invSetupId);

	InvoiceSetupDetailEntity findByCarFamilyCodeAndEffFromAndEffToAndInvSetupId(String cfc, Date vanDateFrom, Date vanDateTo, int invSetupId);

	InvoiceSetupDetailEntity findByCarFamilyCodeContainingAndEffFromAndEffToAndInvSetupId(String cfc,
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2,
			int invSetupId);
	
	InvoiceSetupDetailEntity findByCarFamilyCodeContainingAndEffFromLessThanAndEffToGreaterThanAndInvSetupId(String cfc,
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2,
			int invSetupId);
	
}
