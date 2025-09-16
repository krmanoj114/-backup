package com.tpex.batchjob.binf005.configuration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tpex.batchjob.common.configuration.model.LineConfig;

import lombok.Data;

@Data
public class Binf005FileStructureContainerBlock implements Serializable  {

	private static final long serialVersionUID = -3992558280924633417L;

	private LineConfig containerHeader;
	
	private LineConfig containerDetail;

	private List <Binf005FileStructureContainerModuleBlock> containerModules;
	
	private LineConfig containerFooter;

	public Binf005FileStructureContainerBlock() {
		containerModules = new ArrayList <> ();
	}
	
}
