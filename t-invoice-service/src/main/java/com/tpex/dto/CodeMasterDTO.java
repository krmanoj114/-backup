package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CodeMasterDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer codeMasterId;
	private String codeMasterName;
	private String jsonFileName;

}
