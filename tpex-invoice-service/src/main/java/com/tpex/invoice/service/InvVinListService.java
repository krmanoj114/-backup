package com.tpex.invoice.service;

import java.io.FileNotFoundException;

import net.sf.jasperreports.engine.JRException;

public interface InvVinListService {
	Object getInvVinListReportDownload(String cmpCd, String invNumber, String userId, String reportName,
			String reportFormat) throws FileNotFoundException, JRException;
}
