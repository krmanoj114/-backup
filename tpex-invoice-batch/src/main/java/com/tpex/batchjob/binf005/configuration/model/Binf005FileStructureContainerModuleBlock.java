package com.tpex.batchjob.binf005.configuration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tpex.batchjob.common.configuration.model.LineConfig;

import lombok.Data;

@Data
public class Binf005FileStructureContainerModuleBlock implements Serializable  {

	private static final long serialVersionUID = 6619802690211887462L;

	private LineConfig moduleHeader;

	private LineConfig moduleDetail;
	
	private List <Binf005FileStructureModulePartBlock> moduleParts;
	
	private LineConfig moduleFooter;

	public Binf005FileStructureContainerModuleBlock() {
		moduleParts = new ArrayList <> ();
	}
	
}
