package com.tpex.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryCodeOriginUploadBatchInputDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String partNo;
	private String partName;
	private String countryOfOriginCode;
	private String vanDateFrom;
	private String vanDateTo;

}
