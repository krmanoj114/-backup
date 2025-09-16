package com.tpex.admin.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotPartShortageReportsRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String reportName;
	private String etdMonth;
	private String revisionNo;
	private String destination;
	private String carFamilyCode;
	private String pkgMonth;
	private String invoiceNo;
	private String etdDate;
	private String userId;

}
