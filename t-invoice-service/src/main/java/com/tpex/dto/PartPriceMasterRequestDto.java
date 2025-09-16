package com.tpex.dto;

import javax.validation.constraints.NotBlank;

import com.tpex.util.ConstantUtils;

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
 * Instantiates a new part price master request dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new part price master request dto.
 *
 * @param carFamilyCode the car family code
 * @param importerCode the importer code
 * @param partNo the part no
 * @param effectiveMonth the effective month
 * @param userId the user id
 */
@AllArgsConstructor
public class PartPriceMasterRequestDto {

	/** The car family code. */
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String carFamilyCode;
	
	/** The importer code. */
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String importerCode;
	
	/** The part no. */
	private String partNo;
	
	/** The effective month. */
	private String effectiveMonth;
	
	/** The user id. */
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String userId;
	
}
