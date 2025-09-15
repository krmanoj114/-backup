package com.tpex.admin.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
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
