package com.tpex.invoice.service;


public interface InvoiceHeaderPageService {
	
	Object getAttachedInvHeaderPageRptDownload(String invNumber, String reportName, String reportFormat) throws Exception;

	

}
