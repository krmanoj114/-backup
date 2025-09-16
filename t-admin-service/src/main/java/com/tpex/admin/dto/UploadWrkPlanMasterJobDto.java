package com.tpex.admin.dto;

import java.io.Serializable;

import com.tpex.admin.entity.OemProcessCtrlEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadWrkPlanMasterJobDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String userId;
	private String batchName;
	private String fileName;
	private OemProcessCtrlEntity oemProcessCtrlEntity;

}
