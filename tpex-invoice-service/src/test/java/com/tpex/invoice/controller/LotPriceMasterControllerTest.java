package com.tpex.invoice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.dto.CarFmlyMstDto;
import com.tpex.dto.DestinationAndCarFamilyDTO;
import com.tpex.dto.OemFnlDstMstDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.invoice.dto.LotPartPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPartPriceUpdateRequestDTO;
import com.tpex.invoice.dto.LotPartWarningsDto;
import com.tpex.invoice.dto.LotPriceMasterFinalResponseDTO;
import com.tpex.invoice.dto.LotPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPriceMasterResponseDTO;
import com.tpex.invoice.dto.LotPriceUpdateRequestDTO;
import com.tpex.invoice.dto.PartPricePopupDetailsDto;
import com.tpex.invoice.dto.PartPricePopupFinalDetailsDto;
import com.tpex.invoice.service.InvGenWorkPlanMstService;
import com.tpex.invoice.service.LotPriceMasterService;

@ExtendWith(MockitoExtension.class)
class LotPriceMasterControllerTest {

	@InjectMocks
	LotPriceMasterController lotPriceMasterController;

	@Mock
	LotPriceMasterService lotPriceMasterService;

	@Mock
	InvGenWorkPlanMstService invGenWorkPlanMsteService;

	private static String destCode = "DestCode";
	private static String effDate = "201104";

	@Test
	void testOnLoadDestinationAndCarMaster() {
		DestinationAndCarFamilyDTO destinationAndCarFamilyDTO = new DestinationAndCarFamilyDTO();
		OemFnlDstMstEntity oemFnlDstMstEntity = new OemFnlDstMstEntity();
		List<OemFnlDstMstEntity> list = new ArrayList<>();

		;
		oemFnlDstMstEntity.setFdBuyCd("fdBuyCd");
		oemFnlDstMstEntity.setFdDstCd(destCode);
		oemFnlDstMstEntity.setFdCntryOrg("CountryOrigin");
		oemFnlDstMstEntity.setFdDstNm("DestName");
		list.add(oemFnlDstMstEntity);

		OemFnlDstMstDto oemFnlDstMstDto = new OemFnlDstMstDto();
		oemFnlDstMstDto.setFdDstCd(destCode);
		oemFnlDstMstDto.setFdDstNm("DestName");
		CarFmlyMstDto carFmlyMstDto = new CarFmlyMstDto();
		carFmlyMstDto.setCarFmlyCode("CarFmlyCode");
		carFmlyMstDto.setCarFmlyName("CarFmlyName");
		List<OemFnlDstMstDto> destinations = new ArrayList<>();
		CarFamilyMasterEntity carFamilyMasterEntity = new CarFamilyMasterEntity();
		carFamilyMasterEntity.setCarFmlyCode("CarFmlyCode");
		carFamilyMasterEntity.setCarFmlyRep("CarFmlyRepo");
		carFamilyMasterEntity.setCarFmlySrsName("SrsName");
		carFamilyMasterEntity.setCarFmlyStatus("Status");
		carFamilyMasterEntity.setCarFmlyTiRep("FamlyTip");
		carFamilyMasterEntity.setCarFmlyUpdateBy("UpdatedBy");
		CarFamilyMasterEntity carFamilyMasterEntity1 = new CarFamilyMasterEntity();
		carFamilyMasterEntity.setCarFmlyCode("CarFmlyCode2");
		carFamilyMasterEntity.setCarFmlyRep("CarFmlyRepo2");
		carFamilyMasterEntity.setCarFmlySrsName("SrsName2");
		List<CarFmlyMstDto> carFmly = new ArrayList<>();
		List<CarFamilyMasterEntity> carFamilyMasterEntityList = new ArrayList<>();
		carFamilyMasterEntityList.add(carFamilyMasterEntity1);
		carFamilyMasterEntityList.add(carFamilyMasterEntity);
		carFmly.add(carFmlyMstDto);
		destinations.add(oemFnlDstMstDto);

		destinationAndCarFamilyDTO.setCarFmly(carFmly);
		destinationAndCarFamilyDTO.setDestinations(destinations);

		Mockito.when(invGenWorkPlanMsteService.destinationCodeList()).thenReturn(list);
		Mockito.when(lotPriceMasterService.getCarCodeList()).thenReturn(carFamilyMasterEntityList);

		ResponseEntity<DestinationAndCarFamilyDTO> destCodeAndCarFmly = lotPriceMasterController
				.onLoadDestinationAndCarMaster();
		assertNotNull(destCodeAndCarFmly.getBody());
		assertEquals("CarFmlyCode2", destCodeAndCarFmly.getBody().getCarFmly().get(1).getCarFmlyCode());
		assertEquals(destCode, destCodeAndCarFmly.getBody().getDestinations().get(0).getFdDstCd());
	}

	@Test
	void testSearchLotPriceDetails() throws ParseException {

		List<LotPriceMasterResponseDTO> listOfLotPriceMasterResponseDTOs = new ArrayList<>();

		LotPriceMasterRequestDTO request = new LotPriceMasterRequestDTO();
		request.setCarFamily("272W");
		request.setFinalDestination("717A");
		request.setEffectiveFromMonth("2011/04");

		LotPriceMasterResponseDTO lotPriceMasterResponseDTO = new LotPriceMasterResponseDTO();
		lotPriceMasterResponseDTO.setCurrency("USD");
		lotPriceMasterResponseDTO.setEffectiveFromMonth(effDate);
		lotPriceMasterResponseDTO.setEffectiveToMonth(effDate);
		lotPriceMasterResponseDTO.setLotCode("QA");
		lotPriceMasterResponseDTO.setCurreDesc("US Dollor");
		lotPriceMasterResponseDTO.setLotPrice(BigDecimal.valueOf(3.2));
		LotPriceMasterResponseDTO lotPriceMasterResponseDTO1 = new LotPriceMasterResponseDTO();
		lotPriceMasterResponseDTO1.setCurrency("USD");
		lotPriceMasterResponseDTO1.setEffectiveFromMonth(effDate);
		lotPriceMasterResponseDTO1.setEffectiveToMonth(effDate);
		lotPriceMasterResponseDTO1.setLotCode("DT");
		lotPriceMasterResponseDTO1.setLotPrice(BigDecimal.valueOf(3.2));
		lotPriceMasterResponseDTO1.setCurreDesc("US Dollor");

		listOfLotPriceMasterResponseDTOs.add(lotPriceMasterResponseDTO);
		listOfLotPriceMasterResponseDTOs.add(lotPriceMasterResponseDTO1);

		Mockito.when(lotPriceMasterService.getLotPriceDetails(any(LotPriceMasterRequestDTO.class)))
				.thenReturn(listOfLotPriceMasterResponseDTOs);
		ResponseEntity<List<LotPriceMasterFinalResponseDTO>> searchLotPriceDetails = lotPriceMasterController
				.searchLotPriceDetails(request);
		assertNotNull(searchLotPriceDetails.getBody());
		assertFalse(searchLotPriceDetails.getBody().isEmpty());
		assertEquals("USD", searchLotPriceDetails.getBody().get(0).getCurrency());
		assertEquals("2011/04", searchLotPriceDetails.getBody().get(1).getEffectiveFromMonth());

	}

	@Test
	void testGetPartPriceDetails() throws ParseException {

		LotPartPriceMasterRequestDTO lotPartPriceMasterRequestDTO = new LotPartPriceMasterRequestDTO();
		lotPartPriceMasterRequestDTO.setCarFamily("520W");
		lotPartPriceMasterRequestDTO.setCurrency("USD");
		lotPartPriceMasterRequestDTO.setEffectiveFromMonth("2011/12");
		lotPartPriceMasterRequestDTO.setEffectiveToMonth("2011/12");
		lotPartPriceMasterRequestDTO.setFinalDestination("812B");
		lotPartPriceMasterRequestDTO.setLotCode("YA");

		PartPricePopupDetailsDto partPricePopupDetailsDto = new PartPricePopupDetailsDto();
		partPricePopupDetailsDto.setPartName("JACK SUB-ASSY, SCREW");
		partPricePopupDetailsDto.setPartNumber("091110K07100");
		partPricePopupDetailsDto.setPartPrice(13.97);
		partPricePopupDetailsDto.setPartUsage(1.0);

		PartPricePopupDetailsDto partPricePopupDetailsDto1 = new PartPricePopupDetailsDto();
		partPricePopupDetailsDto1.setPartName("SEAL, FLYWHEEL HOUSING DUST");
		partPricePopupDetailsDto1.setPartNumber("113540C01000");
		partPricePopupDetailsDto1.setPartPrice(0.98);
		partPricePopupDetailsDto1.setPartUsage(1.0);
		List<PartPricePopupDetailsDto> response = new ArrayList<>();
		response.add(partPricePopupDetailsDto);
		response.add(partPricePopupDetailsDto1);

		Mockito.when(lotPriceMasterService.getPartPricePopupDetails(any(LotPartPriceMasterRequestDTO.class)))
				.thenReturn(response);
		ResponseEntity<List<PartPricePopupFinalDetailsDto>> partPriceDetails = lotPriceMasterController
				.getPartPriceDetails(lotPartPriceMasterRequestDTO);
		assertNotNull(partPriceDetails.getBody());
		assertFalse(partPriceDetails.getBody().isEmpty());
		assertEquals("11354-0C010-00", partPriceDetails.getBody().get(1).getPartNumber());
		assertEquals("JACK SUB-ASSY, SCREW", partPriceDetails.getBody().get(0).getPartName());
	}

	@Test
	void testUpdateLotPriceAndPartPriceDetails() throws Exception {

		LotPriceUpdateRequestDTO lotPriceUpdateRequestDTO = new LotPriceUpdateRequestDTO();
		List<LotPriceUpdateRequestDTO> listLotPriceUpdateRequestDTO = new ArrayList<>();

		lotPriceUpdateRequestDTO.setPartName("PartName");
		lotPriceUpdateRequestDTO.setPartNumber("12345-12345-00");
		lotPriceUpdateRequestDTO.setPartPrice("33.1");
		lotPriceUpdateRequestDTO.setPartUsage(1.0);
		lotPriceUpdateRequestDTO.setUpdateBy("Test User");
		lotPriceUpdateRequestDTO.setUpdateDate("2011/22");

		listLotPriceUpdateRequestDTO.add(lotPriceUpdateRequestDTO);

		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = new LotPartPriceUpdateRequestDTO();
		lotPartPriceUpdateRequestDTO.setCarFamily("232A");
		lotPartPriceUpdateRequestDTO.setCurrency("USD");
		lotPartPriceUpdateRequestDTO.setData(listLotPriceUpdateRequestDTO);
		lotPartPriceUpdateRequestDTO.setEffectiveFromMonth("2011/11");
		lotPartPriceUpdateRequestDTO.setEffectiveToMonth("2011/11");
		lotPartPriceUpdateRequestDTO.setFinalDestination("434F");
		lotPartPriceUpdateRequestDTO.setLotCode("QA");

		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(false);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(false);

		List<String> missMatchPartNumbers = new ArrayList<>();
		missMatchPartNumbers.add("12345-12345-00");

		LotPartWarningsDto lotPartWarnings = new LotPartWarningsDto();
		lotPartWarnings.setPartNameList(missMatchPartNumbers);

		Mockito.when(lotPriceMasterService.updateLotPartDetails(lotPartPriceUpdateRequestDTO))
				.thenReturn(lotPartWarnings);
		ResponseEntity<?> message = lotPriceMasterController
				.updateLotPriceAndPartPriceDetails(lotPartPriceUpdateRequestDTO);
		assertNotNull(message.getStatusCode());

		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(false);
		Mockito.when(lotPriceMasterService.updateLotPartDetails(lotPartPriceUpdateRequestDTO))
				.thenReturn(lotPartWarnings);
		ResponseEntity<?> usageMissMatch = lotPriceMasterController
				.updateLotPriceAndPartPriceDetails(lotPartPriceUpdateRequestDTO);
		assertNotNull(usageMissMatch.getBody());

		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(true);

		Mockito.when(lotPriceMasterService.updateLotPartDetails(lotPartPriceUpdateRequestDTO))
				.thenReturn(lotPartWarnings);
		ResponseEntity<?> response1 = lotPriceMasterController
				.updateLotPriceAndPartPriceDetails(lotPartPriceUpdateRequestDTO);
		assertEquals(HttpStatus.OK, response1.getStatusCode());

	}
}
