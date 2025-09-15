package com.tpex.invoice.service;

public interface CertificateOriginReportService {
	Object getCertificateOriginReportDownload(String cmpCd, String invNumber, String userId, String reportName, String reportFormat) throws Exception;
}
