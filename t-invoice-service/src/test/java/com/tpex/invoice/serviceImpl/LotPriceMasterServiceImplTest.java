package com.tpex.invoice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Tuple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemLotPartPrcMstEntity;
import com.tpex.entity.OemLotPrcMstEntity;
import com.tpex.entity.OemLotSizeMstEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.LotPartPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPartPriceUpdateRequestDTO;
import com.tpex.invoice.dto.LotPartWarningsDto;
import com.tpex.invoice.dto.LotPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPriceMasterResponseDTO;
import com.tpex.invoice.dto.LotPriceUpdateRequestDTO;
import com.tpex.invoice.dto.PartPricePopupDetailsDto;
import com.tpex.invoice.serviceimpl.LotPriceMasterServiceImpl;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.LotPartPriceMasterRepository;
import com.tpex.repository.LotPriceMasterRepository;
import com.tpex.repository.LotSizeMasterRepository;
import com.tpex.repository.NoemPackSpecRepository;

@ExtendWith(MockitoExtension.class)
class LotPriceMasterServiceImplTest {

	@InjectMocks
	LotPriceMasterServiceImpl lotPriceMasterServiceImpl;

	@Mock
	LotPriceMasterRepository lotPriceMasterRepository;

	@Mock
	LotPartPriceMasterRepository lotPartPriceMasterRepository;

	@Mock
	CarFamilyMastRepository carFamilyMastRepository;

	@Mock
	NoemPackSpecRepository noemPackSpecRepository;

	@Mock
	LotSizeMasterRepository lotSizeMasterRepository;

	@Test
	void testGetCarCodeList() {

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
		List<CarFamilyMasterEntity> carFamilyMasterEntityList = new ArrayList<>();
		carFamilyMasterEntityList.add(carFamilyMasterEntity1);
		carFamilyMasterEntityList.add(carFamilyMasterEntity);
		Mockito.when(carFamilyMastRepository.findAllByOrderByCarFmlyCodeAsc()).thenReturn(carFamilyMasterEntityList);
		List<CarFamilyMasterEntity> carCodeList = lotPriceMasterServiceImpl.getCarCodeList();
		assertNotNull(carCodeList);
	}

	//@Test
	void testGetLotPriceDetails() throws ParseException {

		LotPriceMasterRequestDTO request = new LotPriceMasterRequestDTO();
		request.setCarFamily("carfamlit");
		request.setFinalDestination("dest");
		request.setEffectiveFromMonth("2011/04");
		List<Tuple> entity = new ArrayList<>();
		OemLotPrcMstEntity oemLotPrcMstEntity = new OemLotPrcMstEntity();
		oemLotPrcMstEntity.setEffFromMonth("2011/04");
		oemLotPrcMstEntity.setEffFromToMonth("2011/04");
		oemLotPrcMstEntity.setLotPrice(987.2);
		oemLotPrcMstEntity.setLotPriceStatus("status");
		oemLotPrcMstEntity.setPriceCFCode("402W");
		oemLotPrcMstEntity.setPriceCurrCode("USD");
		oemLotPrcMstEntity.setPriceDestCode("DestCode");
		oemLotPrcMstEntity.setPriceLotCode("LotCode");
		oemLotPrcMstEntity.setUpdateBy("updateBy");
		entity.addAll( (Collection<? extends Tuple>) oemLotPrcMstEntity);
		Mockito.when(lotPriceMasterRepository.findLotPriceDetails(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(entity);
		List<LotPriceMasterResponseDTO> lotPriceDetails = lotPriceMasterServiceImpl.getLotPriceDetails(request);
		assertNotNull(lotPriceDetails);

	}

	@Test
	void testGetPartPricePopupDetails() throws ParseException {

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
		List<OemLotPartPrcMstEntity> listofPartPriceDetails = new ArrayList<>();
		OemLotPartPrcMstEntity oemLotPartPrcMstEntity = new OemLotPartPrcMstEntity();
		oemLotPartPrcMstEntity.setPartPriceCurrCode("USD");
		oemLotPartPrcMstEntity.setPartPriceDestCode("DestCode");
		oemLotPartPrcMstEntity.setPartPriceEffFromMonth("2011/12");
		oemLotPartPrcMstEntity.setPartPriceEffToMonth("2011/02");
		oemLotPartPrcMstEntity.setPartPriceLotCode("YA");
		Mockito.when(lotPartPriceMasterRepository.findLotPartPriceDetails(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
		.thenReturn(listofPartPriceDetails);
		List<PartPricePopupDetailsDto> partPricePopupDetails = lotPriceMasterServiceImpl
				.getPartPricePopupDetails(lotPartPriceMasterRequestDTO);
		assertNotNull(partPricePopupDetails);
	}

	@Test
	void testUpdateLotPartDetails() throws Exception {

		OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();

		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();
		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(true);
		List<LotPriceUpdateRequestDTO> data = lotPartPriceUpdateRequestDTO.getData();

		Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
				"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);
 
		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceName("PartName", "201111", "201111", "316B",
				"408W", "USD", "QA", "657470K01000", "Test User", LocalDate.now())).thenReturn(1);

		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceInPartMst(2.1, "201111", "201111", "316B",
				"408W", "USD", "QA", "657470K01000", "Test User", LocalDate.now())).thenReturn(1);

		Mockito.lenient().when(lotPriceMasterRepository.updateLotPrice("408W", "316B", "QA", "USD", "201111", "201111",
				2.1, "Test User", LocalDate.now())).thenReturn(1);

		assertNotNull(oemLotPartPrcMstEntityList);

	}
	
	@Test()
	void testUpdateLotPartDetailsWithException() throws Exception {
		
			  
		  OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();
		  
		  LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();
		  lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		  lotPartPriceUpdateRequestDTO.setPartusageConfirmation(true);

			List<LotPriceUpdateRequestDTO> data = lotPartPriceUpdateRequestDTO.getData();

			Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
					"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);
	 
			//Mockito.lenient().when(ERR_IN_1009)
			Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceName("PartName", "201111", "201111", "316B",
					"408W", "USD", "QA", "657470K01000", "Test User", LocalDate.now())).thenReturn(0);

			Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceInPartMst(2.1, "201111", "201111", "316B",
					"408W", "USD", "QA", "657470K01000", "Test User", LocalDate.now())).thenReturn(0);

			Mockito.lenient().when(lotPriceMasterRepository.updateLotPrice("408W", "316B", "QA", "USD", "201111", "201111",
					2.1, "Test User", LocalDate.now())).thenReturn(0);

			assertNotNull(oemLotPartPrcMstEntityList);
		
	}

	
	@Test
	void testPartNameMissMatch() throws Exception {

		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();

		lotPartPriceUpdateRequestDTO.getData().get(0).setPartName("PartaName");
		OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();
		oemLotPartPrcMstEntityList.setPartPriceUsage(1.0);

		Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
				"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);

		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceName("PartName", "201111", "201111", "316B",
				"408W", "USD", "QA", "65747-0K010-00", "Test User", LocalDate.now())).thenReturn(1);

		LotPartWarningsDto updateLotPartDetails = lotPriceMasterServiceImpl.updateLotPartDetails(lotPartPriceUpdateRequestDTO);
		
		assertNotNull(updateLotPartDetails);
	}

	@Test 
	void testPartNameMissMatchException() throws Exception {

		OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();
		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();
		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(true);
		lotPartPriceUpdateRequestDTO.getData().get(0).setPartName("askdjf");
		
		Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
				"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);

		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceName("PartName", "201111", "201111", "316B", "408W", "USD", "QA", "", "Test User",
				LocalDate.now())).thenReturn(0);

		Exception assertThrows = Assertions.assertThrows(Exception.class, () -> lotPriceMasterServiceImpl.updateLotPartDetails(lotPartPriceUpdateRequestDTO));
		assertThrows.getMessage(); 
		}

	@Test 
	void testPartNameMissMatchWithTildaException() throws Exception {

		OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();
		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();
		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(true);
		lotPartPriceUpdateRequestDTO.getData().get(0).setPartName("ask~djf");
		
		Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
				"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);

		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceName("PartName", "201111", "201111", "316B", "408W", "USD", "QA", "", "Test User",
				LocalDate.now())).thenReturn(0);

		Exception assertThrows = Assertions.assertThrows(Exception.class, () -> lotPriceMasterServiceImpl.updateLotPartDetails(lotPartPriceUpdateRequestDTO));
		assertThrows.getMessage(); 
		}


	@Test
	void testPartPriceMissMatchAndUpdate() throws Exception {

		OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();

		oemLotPartPrcMstEntityList.setPartPriceUsage(1.0);
		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();
		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(true);

		lotPartPriceUpdateRequestDTO.getData().get(0).setPartPrice("2.2");

		Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
				"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);
		
		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceInPartMst(2.2, "201111", "201111", "316B", "408W",
				"USD", "QA", "657470K01000", "Test User", LocalDate.now())).thenReturn(1);

		Mockito.lenient().when(lotPriceMasterRepository.updateLotPrice("408W", "316B", "QA", "USA", "201111", "201111", 1.1, null,
				null)).thenReturn(1);

		LotPartWarningsDto updateLotPartDetails = lotPriceMasterServiceImpl.updateLotPartDetails(requestDto());

		assertNotNull(updateLotPartDetails);
	}

	@Test
	void testPartPriceMissMatchAndError() throws Exception {
		
		OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();

		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();
		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(true);

		lotPartPriceUpdateRequestDTO.getData().get(0).setPartPrice("0");
		
		Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
				"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);

		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceInPartMst(2.2, "201111", "201111", "316B", "408W",
				"USD", "QA", "657470K01000", "Test User", LocalDate.now())).thenReturn(0);

		Mockito.lenient().when(lotPriceMasterRepository.updateLotPrice("408W", "316B", "QA", "USA", "201111", "201111", 2.2, null,
				null)).thenReturn(0);

		
		  Exception assertThrows = Assertions.assertThrows(Exception.class, () ->
		  lotPriceMasterServiceImpl.updateLotPartDetails(lotPartPriceUpdateRequestDTO));
		  assertThrows.getMessage();
		 
	}

	@Test
	void testPartUsageMissMatchAndUpdate() throws Exception {

		OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();
		oemLotPartPrcMstEntityList.setPartPriceUsage(1.2);
		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();

		List<Integer> findSpecDetails = Arrays.asList(1, 2, 3, 4, 5);

		List<OemLotSizeMstEntity> findLotSize1 = new ArrayList<>();
		OemLotSizeMstEntity entity=new OemLotSizeMstEntity();
		entity.setLotSizeCode(3.3);

		findLotSize1.add(entity);

		lotPartPriceUpdateRequestDTO.getData().get(0).setPartUsage(8.5);
		
		Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
				"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);

		Mockito.lenient().when(noemPackSpecRepository.findSpecDetails("408", "316B", "QA", "657470K01000", "201111", "201111"))
		.thenReturn(findSpecDetails);

		assertNotNull(findLotSize1);

		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceUsage(8.8, 2.2, "201111", "201111", "316B", "408W",
				"USD", "QA", "657470K01000", "Test User", LocalDate.now())).thenReturn(1);


		Mockito.lenient().when(lotPriceMasterRepository.updateLotPrice("408W", "316B", "QA", "USA", "201111", "201111", 1.1, null,
				null)).thenReturn(1);
		Mockito.lenient().when(lotSizeMasterRepository.findByCarFamilyCodeAndLotModImpAndLotCodeAndPartNumber("408W", "316B", "QA", "657470K01000"))
		.thenReturn(findLotSize1);

		LotPartWarningsDto updateLotPartDetails = lotPriceMasterServiceImpl.updateLotPartDetails(requestDto());

		assertNotNull(updateLotPartDetails);
	}

	@Test
	void testPartUsageMissMatchAndError() throws Exception {

		OemLotPartPrcMstEntity oemLotPartPrcMstEntityList = requestEntityData();
		
		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = requestDto();
		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(true);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(true);
		
		lotPartPriceUpdateRequestDTO.getData().get(0).setPartUsage(8.5);
		
		Mockito.lenient().when(lotPartPriceMasterRepository.findNameByReq("408W", "316B", "QA", "USD", "201111",
				"201111", "657470K01000")).thenReturn(oemLotPartPrcMstEntityList);

		Mockito.lenient().when(lotPartPriceMasterRepository.updatePartPriceUsage(8.8, 2.2, "201111", "201111", "316B", "408W",
				"USD", "QA", "657470K01000", "Test User", LocalDate.now())).thenReturn(0);

		Mockito.lenient().when(lotPriceMasterRepository.updateLotPrice("408W", "316B", "QA", "USA", "201111", "201111", 1.1, null,
				null));

		Exception assertThrows = Assertions.assertThrows(Exception.class,
				() -> lotPriceMasterServiceImpl.updateLotPartDetails(lotPartPriceUpdateRequestDTO));
		
		lotPartPriceUpdateRequestDTO.getData().get(0).setPartUsage(-1.0);
		
		Exception assertThrows1 = Assertions.assertThrows(InvalidInputParametersException.class,
				() -> lotPriceMasterServiceImpl.updateLotPartDetails(lotPartPriceUpdateRequestDTO));
				
		assertThrows.getMessage();
		assertThrows1.getMessage();
		
		Mockito.lenient().when(lotSizeMasterRepository.findByCarFamilyCodeAndLotModImpAndLotCodeAndPartNumber("408W", "316B", "QA", "657470K01000"))
		.thenReturn(null);
		
		Mockito.lenient().when(lotSizeMasterRepository.findByCarFamilyCodeAndLotModImpAndLotCode("408W", "316B", "QA"))
		.thenReturn(null);

		
	}

	private LotPartPriceUpdateRequestDTO requestDto() {

		LotPriceUpdateRequestDTO lotPriceUpdateRequestDTO = new LotPriceUpdateRequestDTO();
		List<LotPriceUpdateRequestDTO> listLotPriceUpdateRequestDTO = new ArrayList<>();

		lotPriceUpdateRequestDTO.setPartName("PartName");
		lotPriceUpdateRequestDTO.setPartNumber("657470K01000");
		lotPriceUpdateRequestDTO.setPartPrice("33.1");
		lotPriceUpdateRequestDTO.setPartUsage(1.0);
		lotPriceUpdateRequestDTO.setUpdateBy("Test User");
		lotPriceUpdateRequestDTO.setUpdateDate("2011/22");

		listLotPriceUpdateRequestDTO.add(lotPriceUpdateRequestDTO);

		LotPartPriceUpdateRequestDTO lotPartPriceUpdateRequestDTO = new LotPartPriceUpdateRequestDTO();
		lotPartPriceUpdateRequestDTO.setCarFamily("408W");
		lotPartPriceUpdateRequestDTO.setCurrency("USD");
		lotPartPriceUpdateRequestDTO.setData(listLotPriceUpdateRequestDTO);
		lotPartPriceUpdateRequestDTO.setEffectiveFromMonth("2011/11");
		lotPartPriceUpdateRequestDTO.setEffectiveToMonth("2011/11");
		lotPartPriceUpdateRequestDTO.setFinalDestination("316B");
		lotPartPriceUpdateRequestDTO.setLotCode("QA");
		lotPartPriceUpdateRequestDTO.setPartNameConfirmation(false);
		lotPartPriceUpdateRequestDTO.setPartusageConfirmation(false);

		return lotPartPriceUpdateRequestDTO;
	}

	private OemLotPartPrcMstEntity requestEntityData() {

		OemLotPartPrcMstEntity oemLotPartPrcMstEntity = new OemLotPartPrcMstEntity();

		oemLotPartPrcMstEntity.setPartPriceNo("657470K01000");
		oemLotPartPrcMstEntity.setPartPriceName("PartName");
		oemLotPartPrcMstEntity.setPartPriceUsage(2.1);
		oemLotPartPrcMstEntity.setPartPricePrc(2.1);
		oemLotPartPrcMstEntity.setPartPriceCurrCode("USD");
		oemLotPartPrcMstEntity.setPartPriceEffFromMonth("201111");
		oemLotPartPrcMstEntity.setPartPriceEffToMonth("201111");
		oemLotPartPrcMstEntity.setPartPriceDestCode("316B");
		oemLotPartPrcMstEntity.setPartPriceLotCode("QA");
		oemLotPartPrcMstEntity.setPartPricericeCFCode("408W");

		return oemLotPartPrcMstEntity;
	}

}
