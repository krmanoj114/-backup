package com.tpex.daily.service;

import java.text.ParseException;
import java.util.List;

import com.tpex.daily.dto.ContainerDestinationPlantCodeDTO;
import com.tpex.daily.dto.IsoContainerFinalResponseDTO;
import com.tpex.daily.dto.IsoContainerMasterDto;
import com.tpex.daily.dto.IsoContainerRequestDTO;

public interface IsoConatinerNoService {

	ContainerDestinationPlantCodeDTO vanningPlantAndContainerDtls();

	public IsoContainerFinalResponseDTO vanningPlantAndContainerSearch(IsoContainerRequestDTO containerRequestDTO) throws ParseException;

	Boolean save(List<IsoContainerMasterDto> isoContainerMasterDto) throws ParseException;	

}
