package com.tpex.invoice.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.tpex.dto.InvoiceSetupMasterDto;
import com.tpex.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceSetupMasterSaveRequestDto {
	
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String setupType;
	
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String importerCode;
	
	
	@Valid
	private List<InvoiceSetupMasterDto> invSetupMasterList;
	private String companyCode;
	
}
