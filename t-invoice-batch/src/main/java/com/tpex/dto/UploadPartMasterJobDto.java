package com.tpex.dto;

import java.io.Serializable;

import com.tpex.entity.OemProcessCtrlEntity;

import lombok.Data;

@Data
public class UploadPartMasterJobDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String userId;
	private String batchName;
	private String fileName;
	private String companyCode;
	private OemProcessCtrlEntity oemProcessCtrlEntity;

}
