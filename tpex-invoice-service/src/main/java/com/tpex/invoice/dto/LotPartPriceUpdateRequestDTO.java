package com.tpex.invoice.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.tpex.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotPartPriceUpdateRequestDTO implements Serializable{

	
	/**

	 * 

	 */

	private static final long serialVersionUID = 1L;

	
	
	private String confirmed;

	
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private List<LotPriceUpdateRequestDTO> data;

	
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	private String effectiveFromMonth;

	
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String effectiveToMonth;

	
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String carFamily;


	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String finalDestination;


	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String lotCode;

	
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String currency;

	
	private boolean partNameConfirmation;
	private boolean partusageConfirmation;


}
