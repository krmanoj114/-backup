package com.tpex.invoice.service;


public interface InvVinListService {
	Object getInvVinListReportDownload(String cmpCd, String invNumber, String userId, String reportName, String reportFormat) throws Exception;
}
