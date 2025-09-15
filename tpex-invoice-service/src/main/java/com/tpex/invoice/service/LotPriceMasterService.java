package com.tpex.invoice.service;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.invoice.dto.LotPartPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPartPriceUpdateRequestDTO;
import com.tpex.invoice.dto.LotPartWarningsDto;
import com.tpex.invoice.dto.LotPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPriceMasterResponseDTO;
import com.tpex.invoice.dto.PartPricePopupDetailsDto;

public interface LotPriceMasterService {

	List<LotPriceMasterResponseDTO> getLotPriceDetails(@Valid LotPriceMasterRequestDTO lotPriceMasterRequestDTO) throws ParseException;

	List<CarFamilyMasterEntity> getCarCodeList();

	List<PartPricePopupDetailsDto> getPartPricePopupDetails(@Valid LotPartPriceMasterRequestDTO request) throws ParseException;

	LotPartWarningsDto updateLotPartDetails(@Valid LotPartPriceUpdateRequestDTO request) throws ParseException, Exception;

}