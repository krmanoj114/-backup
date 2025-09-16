package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCodeDtoObj implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String indCust;	
	private String indCustNm;
	private String indCustAddr1;
	private String indCustAddr2;
	private String indCustAddr3;
	private String indCustAddr4;

}