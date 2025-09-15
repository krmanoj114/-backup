package com.tpex.dto;

import java.util.List;

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
 * Instantiates a new final destination and car family codes DTO.
 */
@NoArgsConstructor

/**
 * Instantiates a new final destination and car family codes DTO.
 *
 * @param destinationList the destination list
 * @param carFamilyList   the car family list
 */
@AllArgsConstructor
public class FinalDestinationAndCarFamilyCodesDTO {

	/** The destination list. */
	private List<CommonMultiSelectDropdownDto> destinationList;

	/** The car family list. */
	private List<CommonMultiSelectDropdownDto> carFamilyList;
}
