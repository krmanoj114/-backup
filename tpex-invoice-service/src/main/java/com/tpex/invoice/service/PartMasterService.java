package com.tpex.invoice.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;

import com.tpex.invoice.dto.InhouseDropdownResponseDTO;
import com.tpex.invoice.dto.PartMasterModifyRequestDTO;
import com.tpex.invoice.dto.PartMasterRequestDTO;
import com.tpex.invoice.dto.PartMasterSearchRequestDto;
import com.tpex.invoice.dto.PartMasterSearchResponseDto;

/**
 * The Interface PartPriceMasterService.
 */
public interface PartMasterService {

	/**
	 * Part price master list.
	 *
	 * @param PartMasterSearchRequestDto the part master request dto
	 * @return the PartMasterSearchResponseDto
	 * @throws ParseException the parse exception
	 */
	List<PartMasterSearchResponseDto> search(PartMasterSearchRequestDto partPriceMasterRequestDto)
			throws IllegalAccessException, InvocationTargetException;

	byte[] download(HttpServletResponse reponse, PartMasterSearchRequestDto partMasterSearchRequestDto)
			throws IOException, DecoderException, ParseException, NullPointerException;

	int deletePartMaster(PartMasterRequestDTO partMasterRequestDTO);

	void savePartMaster(PartMasterModifyRequestDTO partMasterSaveRequestDTO);

	void updatePartMaster(PartMasterModifyRequestDTO partMasterModifyRequestDTO);

	InhouseDropdownResponseDTO getInhouseDropdownData(String companyCode); 
}
