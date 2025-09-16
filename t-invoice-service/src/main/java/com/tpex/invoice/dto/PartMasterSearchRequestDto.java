package com.tpex.invoice.dto;

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
 * Instantiates a new part master search request dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new part master search request dto.
 * @param partNo the part no
 * @param partNo the part name
 * @param partNo the part type
 */
@AllArgsConstructor
public class PartMasterSearchRequestDto {

	/** The part no. */
	private String partNo;
	
	/** The Part Name. */
	private String partName;
	
	/** The Part Type */
	private String partType;
	
	/** The Part Type */
	private String cmpCd;
	
}
