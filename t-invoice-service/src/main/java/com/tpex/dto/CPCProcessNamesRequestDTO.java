package com.tpex.dto;

import javax.validation.constraints.NotBlank;

import com.tpex.util.ConstantUtils;

import lombok.Data;

@Data
public class CPCProcessNamesRequestDTO {
	
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String userId;

}
