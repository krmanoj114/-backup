package com.tpex.daily.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class EnginePartMstRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String exporterCode;
	
	private String importerCode;
	
	private String carFamilyCode;
	
	private String partNo;

}
