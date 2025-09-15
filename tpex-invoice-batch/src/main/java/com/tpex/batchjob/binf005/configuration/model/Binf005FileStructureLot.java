package com.tpex.batchjob.binf005.configuration.model;

import java.io.Serializable;
import java.util.List;

import com.tpex.batchjob.common.configuration.model.LineConfig;

import lombok.Data;

@Data
public class Binf005FileStructureLot implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	private LineConfig lotHeader;
	
	private List <LineConfig> lotDetails;
	
	private LineConfig lotFooter;

}
