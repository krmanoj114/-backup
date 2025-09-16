package com.tpex.invoice.service;

import java.text.ParseException;
import java.util.List;

import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.MixPrivilegeDetailsListResponseDto;
import com.tpex.invoice.dto.MixPrivilegeMasterSaveRequestDto;

public interface MixPrivilegeMasterService {

	MixPrivilegeDetailsListResponseDto fetchMixPrivilegeDetails(String carFmlyCode, String destCode);

	boolean saveMixPrivilegeMaster(List<MixPrivilegeMasterSaveRequestDto> mixPrivilegeMstSaveRequestDtoList,
			String userId) throws InvalidInputParametersException;

	void deleteMixPrivilegeMaster(List<Integer> ids) throws ParseException;

}
