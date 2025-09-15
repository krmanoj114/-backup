package com.tpex.invoice.service;

public interface MpmiInvoiceCoverPageService {

	public Object getInvoiceCoverPageRptDownload(String invNumber, String fileTemplateName,String reportFormat) throws Exception;
}
