package com.tpex.invoice.dto;

import lombok.Data;

@Data
public class InvPackingListResponseDTO {

	private String cnsgName;
	private String cnsgAdd1;
	private String cnsgAdd2;
	private String cnsgAdd3;
	private String cnsgAdd4;
	private String invNo;
	private String invDt;
	private String partNo;
	private int unitPerBox;
	private int sumTotalUnit;
	private String icoFlg;
	private double partPrice;
	private String partName;
	private double partWt;
	private double grossWt;
	private double measurement;
	private String shipMark4;
	private String shipMark5;
	
	private String shipMarkGp;
	private String caseMod;
	private String cfCd;
	private String srsName;
	private int noOfCases;
	
}
