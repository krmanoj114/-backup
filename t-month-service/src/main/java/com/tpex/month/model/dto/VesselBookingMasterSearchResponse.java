package com.tpex.month.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class VesselBookingMasterSearchResponse {

	private List<VesselBookingMasterSearch> data;
	private List<CustomBrokerMasterDropdown> customBrokerMaster;
}
