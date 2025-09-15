package com.tpex.month.model.dto;

import lombok.Data;

@Data
public class VesselBookingMasterSearch {

	private String destinationCode;
	private String etd1;
	private String finalEta;
	private String renbanCode;
	private String noOfContainer20ft;
	private String noOfContainer40ft;
	private String shippingCompany;
	private String customBrokerCode;
	private String customBrokerName;
	private String bookingNo;
	private String vessel1;
	private String bookingStatus;
	private String groupId;
	private String vanningMonth;
	private String vanEndDate;
	private String cbFlag;
	
	//for check input and old value
	private String oldCustomBrokerCode;
	private String oldBookingNo;
	private String oldVessel1;

	//for set highlight
	private boolean isUpdate;
	//for disable row
	private boolean isEtdPassMonth;
	//for disable vessel1 field
	private boolean isCancelled;
}
