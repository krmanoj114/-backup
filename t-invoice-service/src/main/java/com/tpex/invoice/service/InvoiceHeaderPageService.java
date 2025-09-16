package com.tpex.invoice.service;

import java.io.FileNotFoundException;

import net.sf.jasperreports.engine.JRException;

public interface InvoiceHeaderPageService {
	
	Object getAttachedInvHeaderPageRptDownload(String invNumber, String reportName, String reportFormat)
			throws FileNotFoundException, JRException;

	

}
