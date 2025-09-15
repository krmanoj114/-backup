package com.tpex.dto;

import java.io.Serializable;

import com.tpex.entity.OemProcessCtrlEntity;

import lombok.Data;

/**
 * Instantiates a new upload pxp price master job dto.
 */
@Data
public class UploadPxpPriceMasterJobDto implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The user id. */
	private String userId;
	
	/** The batch name. */
	private String batchName;
	
	/** The file name. */
	private String fileName;
	
	/** The company code. */
	private String companyCode;
	
	/** The effective from date. */
	private String effectiveFromDate;
	
	/** The effective to date. */
	private String effectiveToDate;
	
	/** The oem process ctrl entity. */
	private OemProcessCtrlEntity oemProcessCtrlEntity;
}