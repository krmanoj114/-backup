package com.tpex.invoice.service;

import java.io.FileNotFoundException;

import net.sf.jasperreports.engine.JRException;

public interface SCInvAttachedSheetService {

	Object downloadPINS103forSc(String cmpCd, String invoiceNo, String userId, String reportFormat, String templateId)
			throws FileNotFoundException, JRException;

}
