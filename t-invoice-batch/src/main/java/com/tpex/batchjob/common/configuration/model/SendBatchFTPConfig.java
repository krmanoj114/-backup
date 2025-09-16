package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendBatchFTPConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String ipaddress;

	private String port;

	private String user;

	private String password;
	
	private String targetDirectory;

	private String targetFileName;	
	
	
	
	
}
