package com.tpex.jasperreport.service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import com.tpex.entity.RddDownLocDtlEntity;

import net.sf.jasperreports.engine.JRException;

public interface JasperReportService {

	String getFileName(String fileTemplateName, String fileFormat);
	
	RddDownLocDtlEntity saveOfflineDownloadDetail(String fileTemplateName, Map<String, Object> config,
			StringBuilder sb);
	
	void getJasperReportDownloadOffline(List<?> reportData, String fileFormat, String fileTemplateName, 
			Map<String, Object> reportParameters, Map<String, Object> reportConfiguration, int reportId, StringBuilder sb);

	Object getJasperReportDownloadOnline(List<?> reportData, String fileFormat,
			String fileTemplateName, String fileName, Map<String, Object> reportParameters, Map<String, Object> reportConfiguration) 
			throws FileNotFoundException, JRException;

	Object getJasperReportDownloadOfflineV1(List<?> reportData, String fileFormat, String fileTemplateName,
											Map<String, Object> reportParameters, Map<String, Object> reportConfiguration,
											int reportId, StringBuilder sb, String fileName);

}