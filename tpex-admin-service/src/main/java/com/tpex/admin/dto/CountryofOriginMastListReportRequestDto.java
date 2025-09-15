package com.tpex.admin.dto;

import java.io.Serializable;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryofOriginMastListReportRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String partPriceNo;
	
	private List<String> countryOfCountryCode;
	
	private String vanDateFrom;	

	private String vanDateTo;

	
}
