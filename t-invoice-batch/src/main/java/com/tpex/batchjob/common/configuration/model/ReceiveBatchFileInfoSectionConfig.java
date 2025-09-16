package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ReceiveBatchFileInfoSectionConfig implements Serializable {

	private static final long serialVersionUID = 7002578307534733405L;

	private List <InputFileName> inputFileNames;
	
	private String inputFileFolder;
	private String processingFolder;
	private String archiveFolderName;
	private String errorFolderName;
	
	public ReceiveBatchFileInfoSectionConfig() {
		inputFileNames = new ArrayList <> ();
	}
	
}
