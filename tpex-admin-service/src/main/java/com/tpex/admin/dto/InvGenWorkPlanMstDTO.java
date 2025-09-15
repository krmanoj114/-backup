package com.tpex.admin.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class InvGenWorkPlanMstDTO implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private InvGenWorkPlanMstIdDTO id;

	private String issueInvoiceFromDate;
	
	private String issueInvoiceToDate;
	
	private String issueInvoiceDate;

	private String etd1fromDate;
	
	private String etd1ToDate;
	
	private String etd1;

	private String broker;

	private String eta1;

	private Integer cont20;

	private Integer cont40;

	private String vessel1;

	private String voy1;

	private String etd2;

	private String eta2;

	private String vessel2;

	private String voy2;



	private String etd3;

	private String eta3;

	private String vessel3;

	private String voy3;


	private String type;

	private String portOfLoading;

	private String portOfDischarge;

	private String createBy;

	private String createDate;

	private String updateBy;

	private String updateDate;

	private String dsiTiNo;

	private String invGenFlag;

	private List<String> contDest;




}
