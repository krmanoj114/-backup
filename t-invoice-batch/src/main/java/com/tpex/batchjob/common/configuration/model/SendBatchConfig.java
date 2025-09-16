package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendBatchConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String batchId;
	
	private SendBatchFileInfoSectionConfig fileInfo;
	
	private transient SendBatchLineSeparatorConfig lineSeparator;
	
}
