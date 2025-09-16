package com.tpex.dto;

import java.util.List;

import lombok.Data;

@Data
public class DestinationAndCarFamilyDTO {
	
	private List<OemFnlDstMstDto> destinations;
	private List<CarFmlyMstDto> carFmly;

}
