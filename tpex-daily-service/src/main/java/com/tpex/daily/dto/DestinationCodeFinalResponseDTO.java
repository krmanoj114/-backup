package com.tpex.daily.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DestinationCodeFinalResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<DestinationNameWithCodeDTO> destinationNameWithCodeDTO;
	
	private List<CarFamilyNameWithCodeDTO> carFamilyCodeWithCodeDTOs;

}
