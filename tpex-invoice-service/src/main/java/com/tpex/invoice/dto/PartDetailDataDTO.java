package com.tpex.invoice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartDetailDataDTO {

	private String INV_DT;
	private String PART_NO;
	private BigDecimal UNIT_BOX;
	private BigDecimal PART_WT;
	private String CF_CD;
	private String DST_CD;
	private String SHIP_MARK;
	private String SHIP_MARK4;
	private String SERIES;
	private String PRT_NAME;
	private BigDecimal SUM_TOT_UNIT;
	
}
