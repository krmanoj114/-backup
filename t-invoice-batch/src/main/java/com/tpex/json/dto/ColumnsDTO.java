package com.tpex.json.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Instantiates a new columns DTO.
 */
@Data
public class ColumnsDTO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The columns. */
	@JsonProperty("columns")
	private List<CoulmnsInfo> columns = new ArrayList<>();
	
	/** The table name. */
	private String tableName;
	
	/** The primary key. */
	private String primaryKey;
	
	/** The user id. */
	private String userId;
	
	/** The no of records. */
	private Integer noOfRecords;

}
