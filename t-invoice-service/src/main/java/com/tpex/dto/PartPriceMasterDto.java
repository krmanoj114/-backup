package com.tpex.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
 * Instantiates a new part price master dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new part price master dto.
 *
 * @param id the id
 * @param carFamilyCode the car family code
 * @param importerCode the importer code
 * @param effectiveFromMonth the effective from month
 * @param effectiveToMonth the effective to month
 * @param partNo the part no
 * @param partName the part name
 * @param partPrice the part price
 * @param currency the currency
 */
@AllArgsConstructor
public class PartPriceMasterDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	private String id;
	
	/** The car family code. */
	private String carFamilyCode;
	
	/** The importer code. */
	private String importerCode;
	
	/** The effective from month. */
	private String effectiveFromMonth;
	
	/** The effective to month. */
	private String effectiveToMonth;
	
	/** The part no. */
	private String partNo;
	
	/** The part name. */
	private String partName;
	
	/** The part price. */
	private BigDecimal partPrice;
	
	/** The currency. */
	private String currency;

}
