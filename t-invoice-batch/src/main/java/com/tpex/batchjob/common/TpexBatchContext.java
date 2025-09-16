package com.tpex.batchjob.common;

import lombok.Data;

@Data
public class TpexBatchContext <C> {
	
	private C config;
	
	public TpexBatchContext() {
		
	}
	
	
	public TpexBatchContext(C config) {
		this.config = config;
	}

}
