package com.tpex.invoice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartDetailDataDTO {

	private String invDt;
	private String partNo;
	private BigDecimal unitBox;
	private BigDecimal partWt;
	private String cfCd;
	private String dstCd;
	private String shipMark;
	private String shipMark4;
	private String series;
	private String partName;
	private BigDecimal sumTotalUnit;
	
}
