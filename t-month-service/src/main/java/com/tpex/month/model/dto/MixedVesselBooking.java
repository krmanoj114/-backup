package com.tpex.month.model.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MixedVesselBooking {

	private String vanningMonth;
	private String destinationCode;
	private String etdFrom;
	private String etdTo;
	private String shippingCompanyCode;
	private String etd;
	private String groupId;
	private int cont40;
	private int cont20;
	private String keihenRevNo;
	private String flagCont;
	private String contGroupCode;
	private String renbanCode;
	private String bookingNo;
	
	private String listType;
	
	private List<String> destinationCodes;
	
	public MixedVesselBooking(String destinationCode, String shippingCompanyCode){
		this.destinationCode = destinationCode;
		this.shippingCompanyCode = shippingCompanyCode;
	}
}
