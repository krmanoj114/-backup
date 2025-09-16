package com.tpex.invoice.dto;

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
 * Instantiates a new part price master response dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new part price master response dto.
 *
 * @param effectiveFromMonth the effective from month
 * @param effectiveToMonth the effective to month
 * @param partNo the part no
 * @param partName the part name
 * @param partPrice the part price
 * @param currency the currency
 * @param currencyList the currency list
 */
@AllArgsConstructor
public class PartMasterSearchResponseDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The part no. */
	private String partNo;
	
	/** The Part Name. */
	private String partName;
	
	/** The Part Type */
	private String partType;
	
	
	/** Inhouse Shop */
	private String inhouseShop;
	
	/** Part Weight*/
	private String partWeight;
	
}
