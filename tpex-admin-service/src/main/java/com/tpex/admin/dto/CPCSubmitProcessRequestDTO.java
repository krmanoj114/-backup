package com.tpex.admin.dto;

import java.util.HashMap;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpex.admin.util.ConstantUtils;

import lombok.Data;

@Data
public class CPCSubmitProcessRequestDTO {

	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String processId;
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String processName;
	private HashMap<String, String> parameters;
	
	@JsonIgnore
	private boolean hasParameter;
	
	private String userId;
	
}
