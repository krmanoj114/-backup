package com.tpex.admin.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerDestinationPlantCodeDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private List<ContainerDestinationWithCodeDTO> containerDestinationWithCodeDTO;

	private List<PlantMasterDTO> plantMasterDTO;

}
