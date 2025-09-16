package com.tpex.batchjob.binf301.configuration.model;

import java.io.Serializable;

import com.tpex.batchjob.common.configuration.model.LineConfig;

import lombok.Data;

@Data
public class Binf301FileStructure implements Serializable {

	private static final long serialVersionUID = 7241633765877936419L;

	private LineConfig fileHeader;

	private LineConfig fileFooter;

	private LineConfig engVinMstDetails;
	
}
