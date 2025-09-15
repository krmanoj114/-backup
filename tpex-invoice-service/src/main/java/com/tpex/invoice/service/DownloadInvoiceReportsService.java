package com.tpex.invoice.service;

import java.util.stream.Stream;


public interface DownloadInvoiceReportsService {
	
	Object generateInvoiceReports(String loginUserId,String orderType,String invoiceNumber,String etd,String[] dest,String reportName,boolean isExcel) throws Exception;




}
