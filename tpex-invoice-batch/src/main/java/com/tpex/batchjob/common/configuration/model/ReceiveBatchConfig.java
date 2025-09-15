package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReceiveBatchConfig implements Serializable {

	private static final long serialVersionUID = -426530695528725666L;

	private String batchId;
	
	private ReceiveBatchFileInfoSectionConfig fileInfo;
	
}
