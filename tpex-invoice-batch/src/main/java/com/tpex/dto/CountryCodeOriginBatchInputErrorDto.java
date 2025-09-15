package com.tpex.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryCodeOriginBatchInputErrorDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String partNo;
	private String partName;
	private String countryOriginCode;
	private LocalDate vanDateFrom;
	private LocalDate vanDateTo;	
	private String errorReason;

}
