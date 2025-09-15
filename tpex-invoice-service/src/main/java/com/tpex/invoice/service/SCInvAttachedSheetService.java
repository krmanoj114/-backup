package com.tpex.invoice.service;

import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;

public interface SCInvAttachedSheetService {

	
	Object downloadPINS103forSc(String cmpCd, String invoiceNo, String userId, String reportFormat, String templateId) throws Exception;
	
	
}
