package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;

import com.tpex.util.ConstantUtils;

import lombok.Data;

@Data
public class LineError implements Serializable {

	private static final long serialVersionUID = 8872293362376199030L;
	
	private String message;
	private String type;
	
	public LineError(String message) {
		this.message = message;
		this.type = ConstantUtils.PL_STATUS_ERROR;
	}
	
	public LineError(String message, String type) {
		this.message = message;
		this.type = type;
	}
}
