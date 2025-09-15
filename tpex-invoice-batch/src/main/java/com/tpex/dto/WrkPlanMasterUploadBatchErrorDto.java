package com.tpex.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WrkPlanMasterUploadBatchErrorDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String dsiTiNo;
	private LocalDate issueInvoiceDate;
	private LocalDate originalEtd;
	private String contDest;
	private LocalDate etd1;
	private LocalDate eta1;
	private Integer cont20;
	private Integer cont40;
	private String renbanCode;
	private String shipComp;
	private String customBroker;
	private String vessel1;
	private String voyage1;
	private LocalDate etd2;
	private LocalDate eta2;
	private String vessel2;
	private String voyage2;
	private LocalDate etd3;
	private LocalDate eta3;
	private String vessel3;
	private String voyage3;
	private String bookingNo;
	private String folderName;
	private String portOfLoading;
	private String portOfDischarge;
	private String errorReason;
	private String warningReason;

}
