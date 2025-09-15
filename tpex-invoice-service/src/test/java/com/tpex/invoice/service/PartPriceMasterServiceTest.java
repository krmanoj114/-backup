package com.tpex.invoice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.dto.FinalDestinationAndCarFamilyCodesDTO;
import com.tpex.dto.PartPriceMasterDeleteRequestDto;
import com.tpex.dto.PartPriceMasterDto;
import com.tpex.dto.PartPriceMasterRequestDto;
import com.tpex.dto.PartPriceMasterResponseDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemCurrencyMstEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PartMasterEntity;
import com.tpex.entity.PartPriceMasterEntity;
import com.tpex.entity.PartPriceMasterIdEntity;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.entity.TpexConfigEntity;
import com.tpex.invoice.serviceImpl.PartPriceMasterServiceImpl;
import com.tpex.jasperreport.service.JasperReportServiceImpl;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.InsInvPartsDetailsRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.repository.PartPriceMasterRepository;
import com.tpex.repository.RddDownLocDtlRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.TpexConfigurationUtil;

import javassist.bytecode.ByteArray;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;

@ExtendWith(MockitoExtension.class)
class PartPriceMasterServiceTest {
	
	@Mock
	private PartPriceMasterRepository partPriceMasterRepository;
	
	@Mock
	private OemCurrencyMstRepository oemCurrencyMstRepository;
	
	@Mock
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	@Mock
	private CarFamilyMastRepository carFamilyMastRepository;
	
	@Mock
	TpexConfigRepository tpexConfigRepository;
	
	@Mock
	private JasperReportServiceImpl reportServiceImpl;
	
	@Mock
	private RddDownLocDtlRepository rddDownLocDtlRepository;
	
	@Mock
	private TpexConfigurationUtil tpexConfigurationUtil;
	
	@Mock
	private InsInvPartsDetailsRepository insInvPartsDetailsRepository;
	
	@Mock
	private PartMasterRespository partMasterRespository;
	
	@InjectMocks
	private PartPriceMasterServiceImpl partPriceMasterServiceImpl;
	
	private List<PartPriceMasterEntity> partPriceMasterEntities = new ArrayList<>();
	private List<OemCurrencyMstEntity> currencyMstEntityList = new ArrayList<>();
	private List<CommonMultiSelectDropdownDto> currencyList = new ArrayList<>();
    
    @BeforeEach 
    void init() {
    	PartPriceMasterEntity partPriceMasterEntity = new PartPriceMasterEntity();
		partPriceMasterEntity.setId(new PartPriceMasterIdEntity("481W", "303D", "USD", "174510M02000", "202202"));
		partPriceMasterEntity.setCmpCd("TMT");
		partPriceMasterEntity.setEffToMonth("202202");
		partPriceMasterEntity.setPartName("GASKET, EXHAUST PIPE");
		partPriceMasterEntity.setPartPrice(2.55);
		partPriceMasterEntities.add(partPriceMasterEntity);
		
		currencyMstEntityList.add(new OemCurrencyMstEntity("USD", "US DOLLARS", "USD", null, null, "TMT"));
		currencyMstEntityList.add(new OemCurrencyMstEntity("BHT", "THAI BAHT", "BHT", null, null, "TMT"));
		
		currencyList.add(new CommonMultiSelectDropdownDto("USD", "USD-US DOLLARS"));
		currencyList.add(new CommonMultiSelectDropdownDto("BHT", "BHT-THAI BAHT"));
    }

    @AfterEach
    void teardown() {
    	partPriceMasterEntities.clear();
    	currencyMstEntityList.clear();
    	currencyList.clear();
    }

    @Test
	void partPriceMasterListTest() throws Exception {
		
		Mockito.when(partPriceMasterRepository.findPartPriceDetails(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyString())).thenReturn(partPriceMasterEntities);
		Mockito.when(oemCurrencyMstRepository.findAllByCompanyCodeOrderByCrmCdAsc(Mockito.anyString())).thenReturn(currencyMstEntityList);
		
		List<PartPriceMasterDto> partPriceMasterList = new ArrayList<>();
		partPriceMasterList.add(new PartPriceMasterDto("481W303D202202174510M02000USD", "481W", "303D", "2022/02", "2022/02", "17451-0M020-00", "GASKET, EXHAUST PIPE", BigDecimal.valueOf(2.55), "USD"));
		
		PartPriceMasterResponseDto partPriceMasterResponseDto = new PartPriceMasterResponseDto();
		partPriceMasterResponseDto.setCurrencyList(currencyList);
		partPriceMasterResponseDto.setPartPriceMasterList(partPriceMasterList);
		
		//All param given
		PartPriceMasterResponseDto partPriceMasterResponseDtoResult = partPriceMasterServiceImpl.partPriceMasterList(new PartPriceMasterRequestDto("481W", "303D", "17451-0M020-00", "2022/02", "TMT"));  
		assertThat(partPriceMasterResponseDtoResult).isNotNull();
        assertEquals(partPriceMasterResponseDto, partPriceMasterResponseDtoResult);
        
        //No effective month
        PartPriceMasterResponseDto partPriceMasterNoEffMnthResponseDtoResult = partPriceMasterServiceImpl.partPriceMasterList(new PartPriceMasterRequestDto("481W", "303D", "17451-0M020-00", "", "TMT"));
        assertThat(partPriceMasterNoEffMnthResponseDtoResult).isNotNull();
        assertEquals(partPriceMasterResponseDto, partPriceMasterNoEffMnthResponseDtoResult);
        
        //No part no.
        PartPriceMasterResponseDto partPriceMasterResponseNoPartNoDtoResult = partPriceMasterServiceImpl.partPriceMasterList(new PartPriceMasterRequestDto("481W", "303D", "", "2022/02", "TMT"));
        assertThat(partPriceMasterResponseNoPartNoDtoResult).isNotNull();
        assertEquals(partPriceMasterResponseDto, partPriceMasterResponseNoPartNoDtoResult);

	}

	@Test
	void partPriceMasterListGivenAllReqParamTest() throws Exception {
		
		PartPriceMasterEntity partPriceMasterEntity = new PartPriceMasterEntity();
		partPriceMasterEntity.setId(new PartPriceMasterIdEntity("481W", "303D", "USD", "174510M01000", "202202"));
		partPriceMasterEntity.setCmpCd("TMT");
		partPriceMasterEntity.setEffToMonth("202203");
		partPriceMasterEntity.setPartName("GASKET, EXHAUST PIPE");
		partPriceMasterEntity.setPartPrice(2.55);
		partPriceMasterEntities.add(partPriceMasterEntity);
		
		Mockito.when(partPriceMasterRepository.findPartPriceDetails(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyString())).thenReturn(partPriceMasterEntities);
		Mockito.when(oemCurrencyMstRepository.findAllByCompanyCodeOrderByCrmCdAsc(Mockito.anyString())).thenReturn(currencyMstEntityList);
		
		List<PartPriceMasterDto> partPriceMasterList = new ArrayList<>();
		partPriceMasterList.add(new PartPriceMasterDto("481W303D202202174510M02000USD", "481W", "303D", "2022/02", "2022/02", "17451-0M020-00", "GASKET, EXHAUST PIPE", BigDecimal.valueOf(2.55), "USD"));
		partPriceMasterList.add(new PartPriceMasterDto("481W303D202202174510M01000USD", "481W", "303D", "2022/02", "2022/03", "17451-0M010-00", "GASKET, EXHAUST PIPE", BigDecimal.valueOf(2.55), "USD"));
		
		PartPriceMasterResponseDto partPriceMasterResponseDto = new PartPriceMasterResponseDto();
		partPriceMasterResponseDto.setCurrencyList(currencyList);
		partPriceMasterResponseDto.setPartPriceMasterList(partPriceMasterList);
		PartPriceMasterResponseDto partPriceMasterResponseDtoResult = partPriceMasterServiceImpl.partPriceMasterList(new PartPriceMasterRequestDto("481W", "303D", "", "", "TMT"));

        assertThat(partPriceMasterResponseDtoResult).isNotNull();
        assertEquals(partPriceMasterResponseDto, partPriceMasterResponseDtoResult);

	}
	
	@Test
	void destinationAndCarfamilyCodesTest() throws Exception {
		
		List<OemFnlDstMstEntity> dstMstEntities = new ArrayList<>();
		dstMstEntities.add(new OemFnlDstMstEntity("053L", "CAMRY", null, null, null, null, null, null, "TMT"));
		dstMstEntities.add(new OemFnlDstMstEntity("060B", "EFC", null, null, null, null, null, null, "TMT"));
		
		List<CarFamilyMasterEntity> carFamilyMasterEntities = new ArrayList<>();
		carFamilyMasterEntities.add(new CarFamilyMasterEntity("0000", "CHINA", null, null, null, null, null, "TMT"));
		carFamilyMasterEntities.add(new CarFamilyMasterEntity("0001", "GERMANY", null, null, null, null, null, "TMT"));
		
		Mockito.when(oemFnlDstMstRepository.findAllByCompanyCodeOrderByFdDstCdAsc(Mockito.anyString())).thenReturn(dstMstEntities);
		Mockito.when(carFamilyMastRepository.findAllByCompanyCodeOrderByCarFmlyCodeAsc(Mockito.anyString())).thenReturn(carFamilyMasterEntities);
		
		List<CommonMultiSelectDropdownDto> carFamilyList = new ArrayList<>();
		carFamilyList.add(new CommonMultiSelectDropdownDto("053L", "053L-CAMRY"));
		carFamilyList.add(new CommonMultiSelectDropdownDto("060B", "060B-EFC"));
		
		List<CommonMultiSelectDropdownDto> destinationList = new ArrayList<>();
		destinationList.add(new CommonMultiSelectDropdownDto("0000", "0000-CHINA"));
		destinationList.add(new CommonMultiSelectDropdownDto("0001", "0001-GERMANY"));
		
		FinalDestinationAndCarFamilyCodesDTO finalDestinationAndCarFamilyCodesDtoResponse = partPriceMasterServiceImpl.destinationAndCarfamilyCodes("TMT");
        assertThat(finalDestinationAndCarFamilyCodesDtoResponse).isNotNull();
        assertEquals(new FinalDestinationAndCarFamilyCodesDTO(carFamilyList, destinationList), finalDestinationAndCarFamilyCodesDtoResponse);
        
	}
	
	@Test
	void downloadPartPriceMasterDetailsOnlineTest() throws FileNotFoundException, ParseException, JRException {
		TpexConfigEntity config = new TpexConfigEntity();
		config.setId(1);
		config.setName("part.price.master.details.download.file.template.name");
		config.setValue("2");

		Mockito.when(tpexConfigRepository.findByName(Mockito.anyString())).thenReturn(config);
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put("headingFontSize", "2");
		parameters.put("detailFontSize", "2");
		parameters.put("headingFontColor", "2");
		parameters.put("detailFontColor", "2");
		parameters.put("headingBGColor", "2");
		parameters.put("detailBGColor", "2");
		parameters.put("detailVAlign", "2");
		Mockito.when(tpexConfigurationUtil.getReportDynamicPrameters()).thenReturn(parameters);

		HashMap<String, Object> map = new HashMap<>();
		map.put("outStream", new ByteArray());
		map.put("fileName", "PxP Price_481W_303D");
		
		Mockito.when(partPriceMasterRepository.findPartPriceMasterCount(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(1);
		Mockito.when(reportServiceImpl.getJasperReportDownloadOnline(Mockito.anyList(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(map);

		Map<String, Object> downloadPartPriceMasterDetailsResultMap = partPriceMasterServiceImpl.downloadPartPriceMasterDetails("481W", "303D", "17451-0M020-00", "2022/02", "TMT");

		map.put("status", "online");
		assertThat(downloadPartPriceMasterDetailsResultMap).isNotNull();
		assertEquals(map, downloadPartPriceMasterDetailsResultMap);

	}
	
	@Test
	void downloadPartPriceMasterDetailsOfflineTest() throws FileNotFoundException, ParseException, JRException {
		TpexConfigEntity config = new TpexConfigEntity();
		config.setId(1);
		config.setName("part.price.master.details.download.file.template.name");
		config.setValue("2");

		Mockito.when(tpexConfigRepository.findByName(Mockito.anyString())).thenReturn(config);
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put("headingFontSize", "2");
		parameters.put("detailFontSize", "2");
		parameters.put("headingFontColor", "2");
		parameters.put("detailFontColor", "2");
		parameters.put("headingBGColor", "2");
		parameters.put("detailBGColor", "2");
		parameters.put("detailVAlign", "2");
		Mockito.when(tpexConfigurationUtil.getReportDynamicPrameters()).thenReturn(parameters);

		HashMap<String, Object> map = new HashMap<>();
		map.put("message", "INFO_IN_1004");
		map.put("status", "offline");
		
		Mockito.when(partPriceMasterRepository.findPartPriceMasterCount(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(3);
		Mockito.doNothing().when(reportServiceImpl).getJasperReportDownloadOffline(Mockito.anyList(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyInt(), Mockito.any());

		RddDownLocDtlEntity saveRddDownLocDtlEntity = new RddDownLocDtlEntity();
		saveRddDownLocDtlEntity.setReportId(0);
		saveRddDownLocDtlEntity.setStatus("Success");
		Mockito.when(reportServiceImpl.saveOfflineDownloadDetail(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(saveRddDownLocDtlEntity);

		Map<String, Object> downloadPartPriceMasterDetailsResultMap = partPriceMasterServiceImpl.downloadPartPriceMasterDetails("481W", "303D", "17451-0M020-00", "2022/02", "TMT");

		assertThat(downloadPartPriceMasterDetailsResultMap).isNotNull();
		assertEquals(map, downloadPartPriceMasterDetailsResultMap);

	}
	
	@Test
	void deletePartPriceMasterTest() {
		
		Mockito.when(insInvPartsDetailsRepository.countInvoiceGeneratedForPartPrice(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(0);

		doNothing().when(partPriceMasterRepository).deleteByIdCfCodeAndIdDestCodeAndIdCurrencyCodeAndIdPartNoAndIdEffFromMonth(
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		List<PartPriceMasterDto> partPriceMasterDeleteRequestDtoList = new ArrayList<>();
		
		partPriceMasterDeleteRequestDtoList.add(new PartPriceMasterDto("481W303D202308", "481W", "303D", "2023/08", "2024/06", "17451-0M020-04", "GASKET, EXHAUST PIPE", BigDecimal.valueOf(2.55), "USD"));
		
        Assertions.assertDoesNotThrow(() -> partPriceMasterServiceImpl.deletePartPriceMasterDetails(partPriceMasterDeleteRequestDtoList));

	}
	
	@Test
	void saveShippingControlMasterTest() throws ParseException {

		Mockito.when(partPriceMasterRepository.countByIdCfCodeAndIdDestCodeAndIdPartNoAndIdEffFromMonthAndEffToMonth(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0L);
		Mockito.when(partPriceMasterRepository.countByIdCfCodeAndIdDestCodeAndIdPartNo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0L);

		List<PartPriceMasterEntity> partPriceMasterEntities = new ArrayList<>();
		PartPriceMasterIdEntity partPriceMasterIdEntity = new PartPriceMasterIdEntity("481W", "303D", "USD", "174510M02000", "202308");
		PartPriceMasterEntity partPriceMasterEntity = new PartPriceMasterEntity();
		partPriceMasterEntity.setId(partPriceMasterIdEntity);
		partPriceMasterEntity.setCmpCd("TMT");
		partPriceMasterEntity.setEffToMonth("202309");
		partPriceMasterEntity.setPartName("GASKET, EXHAUST PIPE");
		partPriceMasterEntity.setPartPrice(2.55);
		partPriceMasterEntities.add(partPriceMasterEntity);
		Mockito.when(partPriceMasterRepository.saveAll(Mockito.anyIterable())).thenReturn(partPriceMasterEntities);
		
		List<PartPriceMasterDto> partPriceMasterDtos = new ArrayList<>();
		partPriceMasterDtos.add(new PartPriceMasterDto("481W303D202202174510M02000USD", "481W", "303D", "2023/08", "2023/09", "174510M02000", "GASKET, EXHAUST PIPE", BigDecimal.valueOf(2.55), "USD"));
		PartPriceMasterDeleteRequestDto partPriceMasterDeleteRequestDto = new PartPriceMasterDeleteRequestDto("TestUser", partPriceMasterDtos);
        boolean isSaved = partPriceMasterServiceImpl.savePxpPartPriceMaster(partPriceMasterDeleteRequestDto);
        assertEquals(Boolean.TRUE, isSaved);
	}
	
	@Test
	void updateShippingControlMasterTest() throws ParseException {

		List<PartPriceMasterEntity> partPriceMasterEntities = new ArrayList<>();
		PartPriceMasterIdEntity partPriceMasterIdEntity = new PartPriceMasterIdEntity("481W", "303D", "USD", "174510M02000", "202202");
		PartPriceMasterEntity partPriceMasterEntity = new PartPriceMasterEntity(partPriceMasterIdEntity, "GASKET, EXHAUST PIPE", 2.55, null, null, "202309", "TMT");
		partPriceMasterEntities.add(partPriceMasterEntity);
		Mockito.when(partPriceMasterRepository.findById(Mockito.any(PartPriceMasterIdEntity.class))).thenReturn(Optional.of(partPriceMasterEntity));
		Mockito.when(partPriceMasterRepository.saveAll(Mockito.anyIterable())).thenReturn(partPriceMasterEntities);
		
		List<PartPriceMasterDto> partPriceMasterDtos = new ArrayList<>();
		partPriceMasterDtos.add(new PartPriceMasterDto("481W303D202202174510M02000USD", "481W", "303D", "2023/08", "2023/09", "174510M02000", "GASKET, EXHAUST PIPE", BigDecimal.valueOf(2.55), "USD"));
		PartPriceMasterDeleteRequestDto partPriceMasterDeleteRequestDto = new PartPriceMasterDeleteRequestDto("TestUser", partPriceMasterDtos);
        boolean isSaved = partPriceMasterServiceImpl.updatePxpPartPriceMaster(partPriceMasterDeleteRequestDto);
        assertEquals(Boolean.TRUE, isSaved);
	}
	
	@Test
	void partNameByPartNoTest() throws ParseException {
		PartMasterEntity partMasterEntity = new PartMasterEntity();
		partMasterEntity.setPartNo("174510M02000");
		partMasterEntity.setCmpCode("TMT");
		partMasterEntity.setPartName("GASKET, EXHAUST PIPE");
		Mockito.when(partMasterRespository.findById(Mockito.any())).thenReturn(Optional.of(partMasterEntity));
        String partName = partPriceMasterServiceImpl.partNameByPartNo("17451-0M020-00");
        assertEquals("GASKET, EXHAUST PIPE", partName);
	}

}
