package com.tpex.invoice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenbancodeMasterDeleteDto {
	
	
	private String contDstCd;
	
	private String effectiveFromDt;
	
	private String effctiveToDt;

	private List<String> contGrpCd;

}
