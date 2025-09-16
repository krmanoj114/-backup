package com.tpex.invoice.service;

import java.io.FileNotFoundException;

import net.sf.jasperreports.engine.JRException;

public interface MpmiInvoiceCoverPageService {

	public Object getInvoiceCoverPageRptDownload(String invNumber, String fileTemplateName, String reportFormat)
			throws FileNotFoundException, JRException;
}
