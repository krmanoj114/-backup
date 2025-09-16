package com.tpex.daily.service;

import java.util.List;

import javax.validation.Valid;

import com.tpex.daily.dto.DestinationCodeFinalResponseDTO;
import com.tpex.daily.dto.EnginePartMasterResponseDTO;
import com.tpex.daily.dto.EnginePartMasterSaveRequestDto;
import com.tpex.daily.dto.EnginePartMstRequestDTO;

public interface EnginePartMasterService {
	
	public DestinationCodeFinalResponseDTO destinationCodeandImporterDtls();

	public EnginePartMasterResponseDTO enginePartMasterSearchDtls(EnginePartMstRequestDTO enginePartMstRequestDTO);
    
	void deleteEnginePartMaster(List<EnginePartMasterSaveRequestDto> enginePartMasterRequestDtoList);

	boolean saveEnginePartMaster(List<@Valid EnginePartMasterSaveRequestDto> saveRequestList, String userId);

}
