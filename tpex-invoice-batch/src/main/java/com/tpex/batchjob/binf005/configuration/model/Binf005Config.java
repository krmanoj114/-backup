package com.tpex.batchjob.binf005.configuration.model;

import java.io.Serializable;

import com.tpex.batchjob.common.configuration.model.ReceiveBatchConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class Binf005Config extends ReceiveBatchConfig implements Serializable {

	private static final long serialVersionUID = -5054886755499024323L;
	
	private Binf005FileStructure fileStructure;
	
	public Binf005Config() {
		super();
	}

}
