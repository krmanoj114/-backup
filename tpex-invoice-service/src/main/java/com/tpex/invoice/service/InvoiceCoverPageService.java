package com.tpex.invoice.service;

public interface InvoiceCoverPageService {

	Object getInvoiceCoverPageRptDownload(String invNumber, String reportName, String reportFormat) throws Exception;

}
