package com.tpex.invoice.dto;

import java.util.List;

import com.tpex.dto.CommonMultiSelectDropdownDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnablePackingMasterImporterDTO{
	
	private List<ReturnablePackingMasterDetailsDTO> returnablePackingMasterDetails; 
	private List<CommonMultiSelectDropdownDto> packingPlantList;
	private List<CommonMultiSelectDropdownDto> importerCodeList;
	private String rrackType;
	

}
