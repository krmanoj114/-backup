package com.tpex.dto;

import java.io.Serializable;

import com.tpex.entity.OemProcessCtrlEntity;

import lombok.Data;

@Data
public class UploadwrkPlanMasterJobDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String userId;
	private String batchName;
	private String fileName;
	private String invoiceDateFrom;
	private OemProcessCtrlEntity oemProcessCtrlEntity;

}
