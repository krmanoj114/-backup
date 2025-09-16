package com.tpex.batchjob.binf009.configuration.model;

import java.io.Serializable;

import com.tpex.batchjob.common.configuration.model.ReceiveBatchConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class Binf009Config extends ReceiveBatchConfig implements Serializable {

	private static final long serialVersionUID = -5054886755499024323L;
	
	private Binf009FileStructure fileStructure;

	public Binf009Config() {
		super();
	}

}
