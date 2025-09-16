package com.tpex.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data

/**
 * Instantiates a new oem lot prc mst entity ID.
 *
 * @param priceCFCode the price CF code
 * @param priceDestCode the price dest code
 * @param priceLotCode the price lot code
 * @param priceCurrCode the price curr code
 * @param effFromMonth the eff from month
 */
@AllArgsConstructor

/**
 * Instantiates a new oem lot prc mst entity ID.
 */
@NoArgsConstructor
public class OemLotPrcMstEntityID implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The price CF code. */
	private String priceCFCode;
	
	/** The price dest code. */
	private String priceDestCode;
	
	/** The price lot code. */
	private String priceLotCode;
	
	/** The price curr code. */
	private String priceCurrCode;
	
	/** The eff from month. */
	private String effFromMonth;

}
