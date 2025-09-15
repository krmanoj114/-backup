package com.tpex.invoice.dto;

import java.util.List;

import com.tpex.dto.CommonMultiSelectDropdownDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenbanGroupCodeMasterResponseFinalDto {
	
	private List<RenbanGroupCodeMasterResponseDto> renbanData;
	private List<CommonMultiSelectDropdownDto> renbanGroupCodeList;

}
