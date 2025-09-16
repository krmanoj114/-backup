package com.tpex.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OemLotPartPrcMstEntityID implements Serializable{

	/**
	 * @author R.1.Reddy
	 *  Tpex-447 Part Price details 
	 */
	private static final long serialVersionUID = 1L;
	
	private String partPricericeCFCode;
	private String partPriceDestCode;
	private String partPriceLotCode;
	private String partPriceCurrCode;
	private String partPriceEffFromMonth;
	private String partPriceNo; 
}
