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
 * Instantiates a new part price master delete request dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new part price master delete request dto.
 *
 * @param userId the user id
 * @param data the data
 */
@AllArgsConstructor
public class PartPriceMasterDeleteRequestDto {
	
	/** The user id. */
	private String userId;
	
	/** The data. */
	private List<PartPriceMasterDto> data;

}
