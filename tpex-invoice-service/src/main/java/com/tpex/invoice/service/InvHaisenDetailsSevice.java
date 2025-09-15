package com.tpex.invoice.service;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tpex.dto.NoemHaisenDetailDTO;
import com.tpex.invoice.dto.InvoiceDetailsResponseDto;

/**
 * The Interface InvHaisenDetailsSevice.
 */
public interface InvHaisenDetailsSevice{

	List<String> saveHaisenDetails(String userId,List<NoemHaisenDetailDTO> dto) throws Exception;
    
	List<String> updateInvoiceDetails(String userId, List<InvoiceDetailsResponseDto> invoiceDetailsResponseDto) throws JsonProcessingException, ParseException, Exception;

}
