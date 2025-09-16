package com.tpex.invoice.dto;

import java.util.List;

import com.tpex.dto.CommonDropdownDto;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.dto.InvoiceSetupMasterDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceSetupListResponseDto {
	
	private List<CommonMultiSelectDropdownDto> cfcList;
	private List<CommonMultiSelectDropdownDto> exporterCodeList;
	private List<CommonMultiSelectDropdownDto> reExpCodeList;
	private List<CommonMultiSelectDropdownDto> lineCodeList;
	private List<CommonDropdownDto> packingMonthList;
	private List<CommonDropdownDto> priceMethodList;
	private String vanDateFrom;
	private String vanDateTo;
	private List<InvoiceSetupMasterDto> invSetupMasterList;
	private String setupType;
	private String importerCode;
	
}
