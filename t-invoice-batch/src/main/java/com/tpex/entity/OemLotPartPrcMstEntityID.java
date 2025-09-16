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
 * Instantiates a new oem lot part prc mst entity ID.
 *
 * @param partPricericeCFCode the part pricerice CF code
 * @param partPriceDestCode the part price dest code
 * @param partPriceLotCode the part price lot code
 * @param partPriceCurrCode the part price curr code
 * @param partPriceEffFromMonth the part price eff from month
 * @param partPriceNo the part price no
 */
@AllArgsConstructor

/**
 * Instantiates a new oem lot part prc mst entity ID.
 */
@NoArgsConstructor
public class OemLotPartPrcMstEntityID implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The part pricerice CF code. */
	private String partPricericeCFCode;
	
	/** The part price dest code. */
	private String partPriceDestCode;
	
	/** The part price lot code. */
	private String partPriceLotCode;
	
	/** The part price curr code. */
	private String partPriceCurrCode;
	
	/** The part price eff from month. */
	private String partPriceEffFromMonth;
	
	/** The part price no. */
	private String partPriceNo; 
}
