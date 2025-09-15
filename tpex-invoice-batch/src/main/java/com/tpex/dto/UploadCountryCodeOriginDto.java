package com.tpex.dto;

import java.io.Serializable;

import com.tpex.entity.OemProcessCtrlEntity;

import lombok.Data;

@Data
public class UploadCountryCodeOriginDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String userId;
	private String batchName;
	private String fileName;
	private OemProcessCtrlEntity oemProcessCtrlEntity;

}
