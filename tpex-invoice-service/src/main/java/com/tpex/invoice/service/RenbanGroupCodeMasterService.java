package com.tpex.invoice.service;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tpex.invoice.dto.RenbanGroupCodeMasterRequestDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterResponseFinalDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterSaveRequestDto;
import com.tpex.invoice.dto.RenbancodeMasterDeleteDto;

public interface RenbanGroupCodeMasterService {

	RenbanGroupCodeMasterResponseFinalDto getRenbanGroupCodeMasterDetails(
			@Valid RenbanGroupCodeMasterRequestDto request) throws ParseException;

	void deleteRenbanGroupCodeMasterDetails(@Valid List<RenbancodeMasterDeleteDto> deleteRequest) throws ParseException;

	boolean saveRenbanCodeMaster(List<RenbanGroupCodeMasterSaveRequestDto> renbanGroupCodeMasterSaveRequestDtoList)
			throws ParseException;

	void updateRenbanCodeMaster(List<RenbanGroupCodeMasterSaveRequestDto> renbanGroupCodeMasterSaveRequestDtoList)
			throws ParseException;

}
