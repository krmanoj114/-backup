package com.tpex.admin.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CommonUploadAndDownloadDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private ReportNamesDTO reportNames;
	private transient List<ProcessBatchDTO> listofProcessCtrl;
	private String userId;
	private String jsonFileName;

}
