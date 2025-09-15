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
 * Instantiates a new part master delete request dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new part master delete request dto.
 * @param partNo the part no
 
 * @param cmpCd The Part Type
 */
@AllArgsConstructor

public class PartMasterDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** The part no. */
	private String partNo;
	
	/** The Part Type */
	private String cmpCd;

}
