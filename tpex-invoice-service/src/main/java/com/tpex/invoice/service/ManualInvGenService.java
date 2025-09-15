package com.tpex.invoice.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.ManualInvoiceGenResponseDto;
@Service
public interface ManualInvGenService {

	public List<ManualInvoiceGenResponseDto> searchManualInvDetails(Map<String, String> queryParam);

}
