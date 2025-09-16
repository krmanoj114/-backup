package com.tpex.batchjob.binf005.configuration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tpex.batchjob.common.configuration.model.LineConfig;

import lombok.Data;

@Data
public class Binf005FileStructure implements Serializable {

	private static final long serialVersionUID = 7241633765877936419L;

	private LineConfig gatewayHeader;

	private LineConfig fileHeader;

	private LineConfig fileFooter;
	
	private List <Binf005FileStructureContainerBlock> containers;

	private Binf005FileStructureLot lot;

	public Binf005FileStructure() {
		containers = new ArrayList <> ();
	}
	

}
