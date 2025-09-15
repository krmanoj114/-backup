package com.tpex.admin.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PxpPartPriceMasterDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String currency;
	private String cfc;
	private String imp;
	private String part;
	private String partName;
	private String fobAmt;

}
