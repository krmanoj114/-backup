package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsigneeCompanyNameDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cmpName;
	 private String cmpCd;
	private String cmpAdd1;
	private String cmpAdd2;
	private String cmpAdd3;
	private String cmpAdd4;
//	private String indCustNm;
//	private String indCust;
//	private String indCustAddr1;
//	private String indCustAddr2;
//	private String indCustAddr3;
//	private String indCustAddr4;

}
