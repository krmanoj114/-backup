package com.tpex.json.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Instantiates a new coulmns info.
 */
@Data
public class CoulmnsInfo implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	private String id;
	
	/** The name. */
	private String name;
	
	/** The type. */
	private String type;
	
	/** The max. */
	private String max;
	
	/** The min. */
	private String min;
	
	/** The required. */
	private Boolean required = false;
	
	/** The editable. */
	private Boolean editable = false;
	
	/** The unique. */
	private Boolean unique = false;
	
	/** The link. */
	private Boolean link = false;

}
