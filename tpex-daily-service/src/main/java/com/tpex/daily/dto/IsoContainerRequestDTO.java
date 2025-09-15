package com.tpex.daily.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class IsoContainerRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String vanningMonth;

	private String vanningPlant;

	private String containerDestination;

	private String etd;

	private String continerRanbanNo;

}
