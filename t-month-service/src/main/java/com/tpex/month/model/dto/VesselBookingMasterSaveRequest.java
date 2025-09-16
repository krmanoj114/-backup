package com.tpex.month.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.tpex.month.util.ConstantUtil;

import lombok.Data;

@Data
public class VesselBookingMasterSaveRequest {

	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String destinationCode;
	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String etd1;
	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String finalEta;
	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String renbanCode;
	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String noOfContainer20ft;
	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String noOfContainer40ft;
	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String shippingCompany;
	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String groupId;
	@NotBlank(message = ConstantUtil.ERR_CM_3001)
	private String vanningMonth;
	private String vanEndDate;
	private String cbFlag;
	
	private String customBrokerCode;
	@Size(max = 15)
	private String bookingNo;
	@Size(max = 30)
	private String vessel1;
	
	//for check input and old value
	private String oldCustomBrokerCode;
	private String oldBookingNo;
	private String oldVessel1;
	
	private boolean isUpdate;
}
