package com.tpex.invoice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnablePackingMasterSaveRequestDTO {
	
	private List<ReturnablePackingMasterDetailsDTO> returnablePackingMasterDetailsDTOList;
	private String rrackType;
	private String cmpCd;
	private String isNewRow;
}
