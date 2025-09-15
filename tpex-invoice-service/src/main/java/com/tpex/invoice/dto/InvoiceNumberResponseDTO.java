package com.tpex.invoice.dto;

import java.util.List;

import com.tpex.dto.CommonMultiSelectDropdownDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceNumberResponseDTO {

	private String etaFlag;
	private String invoiceNumber;
	private String invoiceType;
	private String etd;
	private String eta;
	private String importerCode;
	private String buyerCode;
	private String privilegeCode;
	private String currencyCode;
	private List<CommonMultiSelectDropdownDto> partNumber;
}
