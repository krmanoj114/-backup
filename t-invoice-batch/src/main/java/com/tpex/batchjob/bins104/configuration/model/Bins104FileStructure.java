package com.tpex.batchjob.bins104.configuration.model;

import java.io.Serializable;

import com.tpex.batchjob.common.configuration.model.LineConfig;

import lombok.Data;

@Data
public class Bins104FileStructure implements Serializable{
	
	private static final long serialVersionUID = 8535749326302424804L;
	
	private LineConfig fileHeader;

	private LineConfig fileFooter;
	
	private LineConfig fileRecord;
	
		
	
	

}
