package com.tpex.invoice.service;

import java.io.FileNotFoundException;

import net.sf.jasperreports.engine.JRException;

public interface DownloadInvoiceReportsService {

	Object generateInvoiceReports(String loginUserId, String orderType, String invoiceNumber, String etd, String[] dest,
			String reportName, boolean isExcel) throws FileNotFoundException, JRException;

}
