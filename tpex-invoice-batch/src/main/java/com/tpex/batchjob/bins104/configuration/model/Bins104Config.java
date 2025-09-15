package com.tpex.batchjob.bins104.configuration.model;

import java.io.Serializable;

import com.tpex.batchjob.common.configuration.model.ReceiveBatchConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class Bins104Config extends ReceiveBatchConfig implements Serializable {
	
	private static final long serialVersionUID = -1676220978088287621L;
	
	private Bins104FileStructure fileStructure;
	
	public Bins104Config() {
		super();
	}
	 

}
