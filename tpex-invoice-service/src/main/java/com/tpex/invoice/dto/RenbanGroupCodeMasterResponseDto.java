package com.tpex.invoice.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenbanGroupCodeMasterResponseDto {

	private String contDstCd;
	private String etdFromDate;
	private String etdToDate;

	Map<String, List<GroupCodeDetailsDto>> goupdIdDetails;

}
