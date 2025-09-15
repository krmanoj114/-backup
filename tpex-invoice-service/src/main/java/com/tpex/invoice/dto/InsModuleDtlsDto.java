package com.tpex.invoice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class InsModuleDtlsDto {

	/*public InsModuleDtlsDto(BigDecimal grossWt, BigDecimal totalM3, String modNo, String invNo, String cfCd) {
		super();
		this.grossWt = grossWt;
		this.totalM3 = totalM3;
		this.modNo = modNo;
		this.invNo = invNo;
		this.cfCd = cfCd;
	}
	private BigDecimal  grossWt;
	private BigDecimal  totalM3;
	private String modNo;
	private String invNo;
	private String cfCd;*/
	
	public InsModuleDtlsDto(String cfCd_modNo, BigDecimal grossWt, BigDecimal totalM3, BigDecimal cCount) {
		// TODO Auto-generated constructor stub
		this.cfCd_modNo = cfCd_modNo;
		this.grossWt = grossWt;
		this.totalM3 = totalM3;
		this.cCount = cCount;
	}
	private String cfCd_modNo;
	private BigDecimal  grossWt;
	private BigDecimal  totalM3;
	private BigDecimal cCount;
}
