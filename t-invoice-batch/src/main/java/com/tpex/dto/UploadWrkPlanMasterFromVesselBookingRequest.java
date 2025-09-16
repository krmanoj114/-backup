package com.tpex.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.tpex.util.ConstantUtils;

import lombok.Data;

@Data
public class UploadWrkPlanMasterFromVesselBookingRequest {

	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	@Pattern(regexp = ConstantUtils.YEAR_SLASH_MONTH_PATTERN, message = ConstantUtils.ERR_MN_4001)
	private String vanningMonth;
	private String destinationCode;
	private String etdFrom;
	private String etdTo;
	private String shippingCompanyCode;
	private String userId;
}
