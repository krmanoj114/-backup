package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class PartMasterUploadBatchDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String partNo;
	private String partName;
	private String partType;
	private String inhouseShop;
	private String partWeight;

}
