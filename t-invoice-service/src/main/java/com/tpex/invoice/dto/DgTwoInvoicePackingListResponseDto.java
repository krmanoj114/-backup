package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DgTwoInvoicePackingListResponseDto implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String insCnsgName;
	private String insCnsgAdd1;
	private String insCnsgAdd2;
	private String insCnsgAdd3;
	private String insCnsgAdd4;
	private String insInvNo;
	private String insInvDt;
	private String insPartNo;
	private Integer insUnitPerBox;
	private Integer insSumTotUnit;
	private String insIcoFlg;
	private Double insPartPrice;
	private String insPartName;
	private Double insPartWt;
	private Double insGrossWt;
	private Double insMeasurement;
	private String insShipmark4;
	private String insShipmark5;
	private String shipMarkGp;
	private String caseMod;
	private String insCfCd;
	private String insSrsName;
	private Integer insNoOfCase;
	private Integer insNoOfBoxes;
	private String insContSno;
	private String insIsoContNo;
	private String insTptCd;

}
