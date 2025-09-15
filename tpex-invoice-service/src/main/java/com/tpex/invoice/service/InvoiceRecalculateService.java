package com.tpex.invoice.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.InvoiceNumberResponseDTO;
import com.tpex.invoice.dto.InvoiceReCalculateRequestDto;
import com.tpex.invoice.dto.InvoiceReCalculationResponseDTO;

@Service
public interface InvoiceRecalculateService {

	InvoiceNumberResponseDTO getDetailsByInvNo(String request, String companyCode) throws ParseException;

	List<InvoiceReCalculationResponseDTO> getInvRecalculateDetails(String invoiceNumber, List<String> partNo,
			String privilege, String companyCode) throws ParseException;

	void recalculateInvoice(InvoiceReCalculateRequestDto request);
}
