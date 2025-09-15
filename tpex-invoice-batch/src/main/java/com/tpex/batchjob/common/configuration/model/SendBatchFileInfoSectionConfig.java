package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendBatchFileInfoSectionConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<SendBatchFileConfig> outputFileNames;

}
