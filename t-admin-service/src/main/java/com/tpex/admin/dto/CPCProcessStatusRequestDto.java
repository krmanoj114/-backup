package com.tpex.admin.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CPCProcessStatusRequestDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String processId;
	private String systemName;
	private String fromDateTime;
	private String endDateTime;
}
