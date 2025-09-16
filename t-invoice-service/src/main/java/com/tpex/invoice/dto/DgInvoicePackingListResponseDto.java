package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DgInvoicePackingListResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String renban;
	private String totCasesRb;
	private String caseNo;
	private Double totNwCase;
	private Double totGwCase;
	private Double totM3Case;
	private String partNo;
	private String partNm;
	private String qtyBox;
	private String totQtyPcs;
	private String netWtPc;
	private String totNetWtPc;
	private String boxGrossWtPc;
	private String boxM3;
	private String invList;
	private String totNwRb;
	private String totGwRb;
	private String totM3Rb;
	private String bookingNo;
	private String ctryCd;

}
