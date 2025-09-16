package com.tpex.invoice.dto;

import java.io.Serializable;

import javax.validation.constraints.Digits;
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
public class LotPriceUpdateRequestDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotNull(message = ConstantUtils.ERR_CM_3011)
	@NotEmpty(message = ConstantUtils.ERR_CM_3011)
	@NotBlank(message = ConstantUtils.ERR_CM_3011)
	private String partNumber;

	@NotNull(message = ConstantUtils.ERR_CM_3011)
	@NotEmpty(message = ConstantUtils.ERR_CM_3011)
	@NotBlank(message = ConstantUtils.ERR_CM_3011)
	private String partName;
	
	@Digits(integer = 13, fraction = 2, message = ConstantUtils.ERR_CM_3011)
	@NotNull(message = ConstantUtils.ERR_CM_3011)
	@NotBlank(message = ConstantUtils.ERR_CM_3011)
	private String partPrice;
	
	@NotNull(message = ConstantUtils.ERR_CM_3011)
	@NotBlank(message = ConstantUtils.ERR_CM_3011)
	@Digits(integer = 3, fraction =0, message = ConstantUtils.ERR_CM_3011)
	private Double partUsage;
	
	@NotBlank(message = ConstantUtils.ERR_CM_3011)
	private String updateDate;
	
	@NotBlank(message = ConstantUtils.ERR_CM_3011)
	private String updateBy;
	
	
}
