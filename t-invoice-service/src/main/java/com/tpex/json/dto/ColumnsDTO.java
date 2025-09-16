package com.tpex.json.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ColumnsDTO implements Serializable{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("columns")
	private List<CoulmnsInfo> columns = new ArrayList<>();
	private String tableName;
	private String primaryKey;
	private String userId;
	private Integer noOfRecords;

}
