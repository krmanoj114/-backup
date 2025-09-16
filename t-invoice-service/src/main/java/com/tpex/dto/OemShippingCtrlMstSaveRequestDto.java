package com.tpex.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OemShippingCtrlMstSaveRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String buyer;
	private String impCode;
	private String expCode;
	private String cfcCode;
	private String series;
	private String setPartCode;
	private String portOfDischarge;
	private String productGroup;
	private String folderName;
	private String goodDesc1;
	private String goodDesc2;
	private String goodDesc3;
	private String consignee;
	private String notifyParty;
	private String tradeTerm;
	private String certificationOfOriginReport;
	private String soldToMessrs;
	private String plsFlag;
	private String updateByUserId;
	private String isNewRow;
	
}
