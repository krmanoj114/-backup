package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class InputFileName implements Serializable {

	private static final long serialVersionUID = -9076715957135856314L;
	private String beginWith;
	private String company;
	
}
