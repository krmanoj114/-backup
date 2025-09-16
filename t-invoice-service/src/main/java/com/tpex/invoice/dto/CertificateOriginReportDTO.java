package com.tpex.invoice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateOriginReportDTO {
	private String name;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private String invoiceNumber;
	private String partNumber;
	private BigDecimal sumTotalUnit;
	private String partName;
	private String consigneeName;
	private String consigneeAddress1;
	private String consigneeAddress2;
	private String consigneeAddress3;
	private String consigneeAddress4;
	private String consigneeTelephoneNumber;
	private String companyCode;
	private String notifyName;
	private String notifyAddress1;
	private String notifyAddress2;
	private String notifyAddress3;
	private String notifyAddress4;
	private String notifyZip;
	private String notifyCountry;
	private String companyName;
	private String vesselName;
	private String voyageNumber;
	private String destinationName;
	private String etd;
	private String invoiceDate;
}
