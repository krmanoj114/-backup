package com.tpex.invoice.service;

import java.io.FileNotFoundException;
import java.text.ParseException;

import net.sf.jasperreports.engine.JRException;

public interface SeparateInvoiceGenerationService {

	public Object getSeparateInvoiceGeneration(String cmpCd, String invNumber, String userId, String reportId,
			String reportFormat, String invoiceType) throws FileNotFoundException, JRException, ParseException;

}
