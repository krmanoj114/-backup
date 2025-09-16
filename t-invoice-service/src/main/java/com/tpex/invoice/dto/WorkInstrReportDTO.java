package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkInstrReportDTO implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	private String bookingNumber;
	private String haisenNumber;
	private String finalDestination;
	private String inmPackMonth;
	private String etd;
	private String eta;
	private String vanningDate;
	private String shippingCompany;
	private String vesselNameOcean;
	private String voyageNumberOcean;
	private String vanPlantCode;
	private Integer count;
	private String invoiceNumber;
	private Integer modNumber;
	private String invoiceAmount;
	private String aicoName;
	private String series;
	private String inpPackMonth;
	private String rackCount;
	private String nonDg20;
	private String nonDg40;
	private String dg20;
	private String dg40;
	private String indBuyer;
	private String model;
	private String rboxdataCnt;
	

}
