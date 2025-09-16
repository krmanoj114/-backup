package com.tpex.invoice.service;

import java.io.FileNotFoundException;
import java.text.ParseException;

import net.sf.jasperreports.engine.JRException;

public interface WorkInstructionReportService {

	Object getRegularWrkInstructionRptDownload(String etd, String countryCd, String bookingNo, String userId,
			String reportFormat, String reportId) throws ParseException, FileNotFoundException, JRException;

}
