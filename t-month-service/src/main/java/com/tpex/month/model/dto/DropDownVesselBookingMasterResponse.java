package com.tpex.month.model.dto;

import java.util.List;

import com.tpex.month.model.projection.FinalDestinationMasterCodeAndName;
import com.tpex.month.model.projection.ShippingCompanyMasterCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DropDownVesselBookingMasterResponse {

	private List<FinalDestinationMasterCodeAndName> finalDestination;
	private List<ShippingCompanyMasterCode> shippingCompany;
	
}
