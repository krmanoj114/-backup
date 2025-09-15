package com.tpex.invoice.service;

import java.util.List;

import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.InvoiceSetupListResponseDto;
import com.tpex.invoice.dto.InvoiceSetupMasterSaveRequestDto;

import java.text.ParseException;

public interface InvoiceSetupMasterService {
	
	InvoiceSetupListResponseDto getInvoiceSetupMstDetails(String setupType, String importerCode);

	void deleteInvoiceSetupMaster(List<Integer> ids) throws ParseException;

	boolean saveInvoiceSetupMaster(InvoiceSetupMasterSaveRequestDto invoiceSetupMasterSaveRequestDto,
			String userId) throws InvalidInputParametersException;

}
