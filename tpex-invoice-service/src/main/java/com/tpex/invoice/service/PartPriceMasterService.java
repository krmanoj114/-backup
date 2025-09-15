package com.tpex.invoice.service;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.tpex.dto.FinalDestinationAndCarFamilyCodesDTO;
import com.tpex.dto.PartPriceMasterDeleteRequestDto;
import com.tpex.dto.PartPriceMasterRequestDto;
import com.tpex.dto.PartPriceMasterResponseDto;
import com.tpex.dto.PartPriceMasterDto;

import net.sf.jasperreports.engine.JRException;

/**
 * The Interface PartPriceMasterService.
 */
public interface PartPriceMasterService {

	/**
	 * Part price master list.
	 *
	 * @param partPriceMasterRequestDto the part price master request dto
	 * @return the part price master response dto
	 * @throws ParseException the parse exception
	 */
	PartPriceMasterResponseDto partPriceMasterList(PartPriceMasterRequestDto partPriceMasterRequestDto) throws ParseException;

	/**
	 * Destination and carfamily codes.
	 *
	 * @param userId the user id
	 * @return the final destination and car family codes DTO
	 */
	FinalDestinationAndCarFamilyCodesDTO destinationAndCarfamilyCodes(String userId);

	Map<String, Object> downloadPartPriceMasterDetails(String carFamilyCode, String importerCode, String partNo,
			String effectiveMonth, String userId) throws ParseException, FileNotFoundException, JRException;

	List<PartPriceMasterDto> deletePartPriceMasterDetails(List<PartPriceMasterDto> deleteRequest) throws ParseException;

	boolean savePxpPartPriceMaster(PartPriceMasterDeleteRequestDto requestDto) throws ParseException;

	boolean updatePxpPartPriceMaster(PartPriceMasterDeleteRequestDto requestDto) throws ParseException;

	String partNameByPartNo(String partNo);

}
