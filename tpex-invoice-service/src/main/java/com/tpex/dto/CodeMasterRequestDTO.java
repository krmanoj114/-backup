package com.tpex.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CodeMasterRequestDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private Integer codeMasterId;
	private String codeMasterName;
	private String jsonFileName;
	private List<Map<String, String>> data ;
	private String primaryKey;
	private String tableName;
}
