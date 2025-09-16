package com.tpex.invoice.dto;

import java.io.Serializable;
import java.util.List;

import com.tpex.dto.CommonMultiSelectDropdownDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnablePackingMasterDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private List<CommonMultiSelectDropdownDto> packingPlantList;
	private List<CommonMultiSelectDropdownDto> carFamilyList;
	private List<CommonMultiSelectDropdownDto> importerCodeList;

}
