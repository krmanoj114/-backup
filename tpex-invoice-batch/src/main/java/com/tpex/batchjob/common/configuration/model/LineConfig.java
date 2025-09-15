package com.tpex.batchjob.common.configuration.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LineConfig implements Serializable {

	private static final long serialVersionUID = 4044560333838345710L;

	private Integer recordLength;
	
	private List <LineTokenConfig> tokens;

	public LineConfig () {
		tokens = new ArrayList <> ();
	}
	
}
