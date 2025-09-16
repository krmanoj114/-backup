package com.tpex.admin.dto;

import javax.validation.constraints.NotBlank;

import com.tpex.admin.util.ConstantUtils;

import lombok.Data;

@Data
public class CPCProcessNameRequestDTO {
	
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String processId;
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String processName;
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String userId;

}
