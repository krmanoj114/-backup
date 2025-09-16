package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailsResponseDto implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String haisenNo;
	private String etdDate;

	private String etaDate;
	private String oceanVessel;
	private String oceanVoyage;

	private String invoiceNo;
	private String invoiceDate;
	private String invoiceAmount;
	private String invoiceM3;
	private String feederVessel;
	private String feederVoyage;
	private String portOfLoading;
	private String portOfDischarge;
	
	private String shipCompName;
	private String buyer;


}
