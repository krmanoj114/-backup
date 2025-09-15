package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SCInvAttachedSheetResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String insCnsgName; // INS_CNSG_ADD1 //INS_CNSG_NAME
	private String insCnsgAdd1; // INS_CNSG_ADD1
	private String insCnsgAdd2;
	private String insCnsgAdd3;
	private String insCnsgAdd4;
	private String insInvNo;
	private String insInvDt;
	private String insPartNo;
	private Double insUnitPerBox;
	private Integer insSumTotUnit;
	private String insIcoFlg;
	private Double insPartPrice;
	private String insPartName;
	private Double insPartWt;
	private Double insGrossWt;
	private Double insMeasurement;
	private String insShipmark4;
	private String insShipmark5;
	private String insCfCd;
	private String insSrsName;
	private String insCurrCd;
	private String insIcoDesc;
	private String insCoCd;
	private String orgCriteria;
	private String scRemark;
	private String scAuthEmp;
	private Double insSortSeq;
	private String insHsCd;
}
