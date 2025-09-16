package com.tpex.invoice.service;

import java.io.IOException;
import java.text.ParseException;

public interface DownloadInvCntnrLvlReportService {
	
	public byte[] generateContainerLevelReport(String reportId,
			String reportFormate, String etd, String[] destination) throws IOException, ParseException;
	
	public String getReportfeildItems(String etd, String[] destination) throws ParseException;

}
