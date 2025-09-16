package com.tpex.invoice.service;

import java.io.FileNotFoundException;

import net.sf.jasperreports.engine.JRException;

public interface InvPackingListService {

	Object getInvPackingListRptDownload(String invNumber, String reportName, String reportFormat) throws FileNotFoundException, JRException;

}
