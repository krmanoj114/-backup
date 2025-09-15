package com.tpex.daily.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExporterImporterDataDTO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String exporterCode;
	
	private String importerCode;
	
	private String carFamilyCode;
	
	private String partNo;
	
	private String lotModuleCode;
	
	

}
