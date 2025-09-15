package com.tpex.admin.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotPartPriceMasterDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String currency;

	private String lot;
	private String cfc;
	private String imp;
	private String partNo;
	private String partName;
	private String partPrice;
	private String usage;
}
