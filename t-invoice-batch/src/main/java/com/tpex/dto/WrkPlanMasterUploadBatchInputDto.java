package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class WrkPlanMasterUploadBatchInputDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String dsiTiNo;
	private String issueInvoiceDate;
	private String originalEtd;
	private String contDest;
	private String etd1;
	private String eta1;
	private Integer cont20;
	private Integer cont40;
	private String renbanCode;
	private String shipComp;
	private String customBroker;
	private String vessel1;
	private String voyage1;
	private String etd2;
	private String eta2;
	private String vessel2;
	private String voyage2;
	private String etd3;
	private String eta3;
	private String vessel3;
	private String voyage3;
	private String bookingNo;
	private String folderName;
	private String portOfLoading;
	private String portOfDischarge;

}
