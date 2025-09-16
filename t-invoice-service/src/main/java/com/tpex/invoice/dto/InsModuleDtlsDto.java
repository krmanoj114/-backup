package com.tpex.invoice.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class InsModuleDtlsDto {

	

	public InsModuleDtlsDto(String cfCdModNo, BigDecimal grossWt, BigDecimal totalM3, BigDecimal cCount) {
	
		this.cfCdModNo = cfCdModNo;
		this.grossWt = grossWt;
		this.totalM3 = totalM3;
		this.cCount = cCount;
	}

	private String cfCdModNo;
	private BigDecimal grossWt;
	private BigDecimal totalM3;
	private BigDecimal cCount;
}
