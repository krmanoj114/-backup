package com.tpex.month.model.dto;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.tpex.month.util.ConstantUtil;
import com.tpex.month.validate.DateRangeFromTo;
import com.tpex.month.validate.group.AfterDefaultValidate;

import lombok.Data;

@GroupSequence({ VesselBookingMasterSearchRequest.class, AfterDefaultValidate.class })
@Data
@DateRangeFromTo(fromDate = "etdFrom", toDate = "etdTo"
, message = ConstantUtil.ERR_CM_3007
, groups = {AfterDefaultValidate.class})
public class VesselBookingMasterSearchRequest {

	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	@Pattern(regexp = ConstantUtil.YEAR_SLASH_MONTH_PATTERN, message = ConstantUtil.ERR_MN_4001)
	private String vanningMonth;
	private String destinationCode;
	private String etdFrom;
	private String etdTo;
	private String shippingCompanyCode;

}
