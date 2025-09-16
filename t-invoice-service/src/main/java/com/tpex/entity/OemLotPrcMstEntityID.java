package com.tpex.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OemLotPrcMstEntityID implements Serializable {

	/**
	 * @author R.1.Reddy 
	 * tpex-431 Composit Primary Key
	 */
	private static final long serialVersionUID = 1L;
	
	private String priceCFCode;
	private String priceDestCode;
	private String priceLotCode;
	private String priceCurrCode;
	private String effFromMonth;

}
