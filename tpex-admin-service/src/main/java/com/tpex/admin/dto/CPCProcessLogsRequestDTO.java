package com.tpex.admin.dto;

import javax.validation.constraints.NotBlank;

import com.tpex.admin.util.ConstantUtils;

import lombok.Data;

@Data
public class CPCProcessLogsRequestDTO {

	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String processControlId;
	
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String processId;
}
