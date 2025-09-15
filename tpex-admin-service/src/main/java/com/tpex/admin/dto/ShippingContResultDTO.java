package com.tpex.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingContResultDTO {
	
	private Integer srNo;
	private String etd;
	private String containerRenbanNo;
	private String containerSize;
	private String dgContainer;
	private String planVanningDate;
	private String shippingResultRecieved;
	private String invoiceStatus;
	private String bookingNo;
	private String isoContainerNo;
	private String sealNo;
	
}
