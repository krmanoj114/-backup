package com.tpex.admin.batchjob.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class BatchRetry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String retryLimit;

}
