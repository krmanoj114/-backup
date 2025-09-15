package com.tpex.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
@Data
public class CommonUploadAndDownloadDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReportNamesDTO reportNames;
	
	private List<ProcessBatchDTO> listofProcessCtrl = new ArrayList<>();
	
	private String userId;
	private String jsonFileName;
	

}
