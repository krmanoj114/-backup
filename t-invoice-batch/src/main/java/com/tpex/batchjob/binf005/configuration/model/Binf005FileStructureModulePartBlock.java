package com.tpex.batchjob.binf005.configuration.model;

import java.io.Serializable;

import com.tpex.batchjob.common.configuration.model.LineConfig;

import lombok.Data;

@Data
public class Binf005FileStructureModulePartBlock implements Serializable  {

	private static final long serialVersionUID = -7179610173623202891L;

	private LineConfig partHeader;

	private LineConfig partDetail;
	
	private LineConfig partFooter;

}
