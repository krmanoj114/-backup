package com.tpex.invoice.dto;

import java.util.List;

import lombok.Data;
@Data
public class ManInvGenResponseDto {
	
	private List<ManualInvoiceGenResponseDto> listInvoiceResponse;
}
