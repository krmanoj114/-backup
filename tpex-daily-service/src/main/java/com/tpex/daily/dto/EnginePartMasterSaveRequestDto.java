package com.tpex.daily.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.tpex.daily.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnginePartMasterSaveRequestDto implements Serializable{

	private static final long serialVersionUID = 1L;

	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String crFmlyCode;

	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String importerCode;

	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String exporterCode;

	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String partNo;

	private String lotModuleCode;

	private String quantity;

	private String engineFlag;

	private String updateBy;

	private String updateDate;

	private String companyCode;

}
