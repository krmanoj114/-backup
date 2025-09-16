package com.tpex.json.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class CoulmnsInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String type;
	private String max;
	private String min;
	private Boolean required = false;
	private Boolean editable = false;
	private Boolean unique = false;
	private Boolean link = false;
	

}
