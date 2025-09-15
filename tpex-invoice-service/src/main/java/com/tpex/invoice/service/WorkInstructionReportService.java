package com.tpex.invoice.service;


public interface WorkInstructionReportService {

	Object getRegularWrkInstructionRptDownload(String etd,String countryCd,String bookingNo,String userId,  String reportFormat, String reportId) throws Exception;

}
