package com.tpex.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpex.util.ConstantUtils;

import lombok.Data;

@Data
public class CPCSubmitProcessRequestDTO {

	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String processId;
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String processName;
	private List<ProcessControlParameter> parameters;
	
	@JsonIgnore
	private boolean hasParameter;
}
