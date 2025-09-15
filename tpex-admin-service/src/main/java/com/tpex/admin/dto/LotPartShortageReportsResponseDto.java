package com.tpex.admin.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotPartShortageReportsResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer itemNo;
	private String invNo;
	private String revNo;
	private String etd;
	private String destCd;
	private String ordNo;
	private String cfCd;
	private String contSno;
	private String lotNo;
	private String caseCd;
	private String partNo;
	private String partName;
	private String planQty;
	private String actQty;
	private Integer diffQty;
	private Integer sno;
}
