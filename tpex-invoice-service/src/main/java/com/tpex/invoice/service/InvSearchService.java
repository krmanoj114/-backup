package com.tpex.invoice.service;

import java.io.FileNotFoundException;
import java.text.ParseException;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tpex.dto.SearchInvHaisenDetailResponse;
import com.tpex.invoice.dto.InvoiceDetailsResponseWrapper;
import com.tpex.invoice.dto.SearchByInvNoResponseDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailRequestDto;
import com.tpex.invoice.dto.UpdateInvDetailsRequestDTO;

import net.sf.jasperreports.engine.JRException;

public interface InvSearchService {

	SearchInvHaisenDetailResponse fetchHaisenDetail(SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto,
			String regular, String cpo, String spo) throws ParseException;

	SearchByInvNoResponseDto searchByInvNo(String envNo) ;

	InvoiceDetailsResponseWrapper getInvoiceDetails(String haisenNo, String haisenYear) throws JsonProcessingException;
	

	void updateInvDetailsByInvNo(@Valid UpdateInvDetailsRequestDTO updateRequest);
	
	Object downloadPINS103(String cmpCd, String templateId, String invoiceNo, String userId, String reportFormat) throws FileNotFoundException, JRException;


	
}
