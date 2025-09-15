package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class DownloadInvoiceReportsRequestDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String userId;

	private String orderType;

	private String invoiceNumber;

	private String etd;
	
	private String etdTo;
		
	private String bookingNo;

	private String[] destinations;

	private String[] reportTypes;

	private String reportName;
	private String reportFormate;
	private String cmpCd;
}
