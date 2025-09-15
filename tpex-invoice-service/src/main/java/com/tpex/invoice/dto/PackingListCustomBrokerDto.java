package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackingListCustomBrokerDto implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String insCnsgName;
	private String insCnsgAdd1;
	private String insCnsgAdd2;
	private String insCnsgAdd3;
	private String insCnsgAdd4;
	private String insInvNo;
	private String insInvDt;
	private String insPartNo;
	private Double insUnitPerBox;
	private Double insSumTotUnit;
	private String insIcoFlag;
	private Double insPartPrice;
	private String insPartName;
	private Double insPartWt;
	private Double insGrossWt ;
	private Double insMeasurement;
	private String insShipmark4;
	private String insShipmark5;
	private String shipMarkGp;
	private String caseMod;
	private String insCfCd;
	private String insSrsName;
	private Double insNoOfCases;

}

