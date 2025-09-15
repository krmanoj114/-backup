package com.tpex.invoice.service;

import java.util.List;

import javax.validation.Valid;

import com.tpex.dto.SearchInvHaisenDetailResponse;
import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.invoice.dto.InvoiceDetailsResponseWrapper;
import com.tpex.invoice.dto.SearchByInvNoResponseDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailRequestDto;
import com.tpex.invoice.dto.UpdateInvDetailsRequestDTO;

public interface InvSearchService {

	SearchInvHaisenDetailResponse fetchHaisenDetail(SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto,
			String regular, String cpo, String spo) throws Exception;

	SearchByInvNoResponseDto searchByInvNo(String envNo) throws Exception;

	InvoiceDetailsResponseWrapper getInvoiceDetails(String haisenNo, String haisenYear) throws Exception;
	

	void updateInvDetailsByInvNo(@Valid UpdateInvDetailsRequestDTO updateRequest);
	
	Object downloadPINS103(String cmpCd, String templateId, String invoiceNo, String userId, String reportFormat) throws Exception;


	
}
