package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.CommonDropdownDto;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.dto.ConsigneeAndNotifyPartyDto;
import com.tpex.dto.OemShippingCtrlMstDeleteRequestDto;
import com.tpex.dto.OemShippingCtrlMstSaveRequestDto;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.OemShippingCtrlMstDto;
import com.tpex.invoice.dto.ShipCtrlMstDto;
import com.tpex.invoice.serviceimpl.InvShippingCtrlMstServiceImpl;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantProperties;

@ExtendWith(MockitoExtension.class)
class InvShippingCtrlMstControllerTest {
	
	@InjectMocks
	private InvShippingCtrlMstController invShippingCtrlMstController;
	
	@Mock
	private InvShippingCtrlMstServiceImpl invShippingCtrlMstServiceImpl;
	
	@Mock
	private ConstantProperties constantProperties;
	
	@Mock
	private TpexConfigRepository tpexConfigRepository;
	
	private static String SERIES="TOYOTA";
	
	@Test
	void fetchInvShippingCtrlMstTest() throws Exception {
		OemShippingCtrlMstDto oemShippingCtrlMstDto = new OemShippingCtrlMstDto();
		
		List<ShipCtrlMstDto> shipCtrlMstDtos = new ArrayList<>();
		ShipCtrlMstDto shipCtrlMstDto = new ShipCtrlMstDto();
		shipCtrlMstDto.setBuyer("TMT");
		shipCtrlMstDto.setImpCode("722E");
		shipCtrlMstDto.setExpCode("722E");
		shipCtrlMstDto.setCfcCode("000A");
		shipCtrlMstDto.setSeries(SERIES);
		shipCtrlMstDto.setSetPartCode("L");
		shipCtrlMstDto.setPortOfDischarge("BTG");
		shipCtrlMstDto.setProductGroup("CL");
		shipCtrlMstDto.setFolderName("Non-IMV");
		shipCtrlMstDto.setConsignee("1");
		List<CommonMultiSelectDropdownDto> shipCtrlConsigneeDtos = new ArrayList<>();
		shipCtrlConsigneeDtos.add(new CommonMultiSelectDropdownDto("1", "1-Toyota Motor Thailand Co., Ltd."));
		shipCtrlMstDto.setConsigneeList(shipCtrlConsigneeDtos);
		shipCtrlMstDto.setNotifyParty("1");
		List<CommonMultiSelectDropdownDto> shipCtrlNotifyPartyDtos = new ArrayList<>();
		shipCtrlNotifyPartyDtos.add(new CommonMultiSelectDropdownDto("1", "1-TOYOTA MOTOR ASIA PACIFIC PTE, LTD."));
		shipCtrlMstDto.setNotifyPartyList(shipCtrlNotifyPartyDtos);
		shipCtrlMstDto.setGoodDesc1("Toyota Component Parts");
		shipCtrlMstDto.setGoodDesc2("For Model : VIOS");
		shipCtrlMstDto.setGoodDesc3("For Detail See Attachment");
		shipCtrlMstDto.setCertificationOfOriginReport("Y");
		shipCtrlMstDto.setTradeTerm("CIF");
		shipCtrlMstDto.setSoldToMessrs("TMX");
		shipCtrlMstDto.setPlsFlag("Y");
		shipCtrlMstDto.setUpdateDateTime(null);
		shipCtrlMstDtos.add(shipCtrlMstDto);
		
		oemShippingCtrlMstDto.setMasterList(shipCtrlMstDtos);
		
		List<CommonDropdownDto> shipCtrlBuyerDtos = new ArrayList<>();
		shipCtrlBuyerDtos.add(new CommonDropdownDto("TMT", "TMT"));
		shipCtrlBuyerDtos.add(new CommonDropdownDto("TMI", "TMI"));
		oemShippingCtrlMstDto.setBuyerList(shipCtrlBuyerDtos);
		
		List<CommonDropdownDto> shipCtrlImpCodeDtos = new ArrayList<>();
		shipCtrlImpCodeDtos.add(new CommonDropdownDto("465K", "465K"));
		shipCtrlImpCodeDtos.add(new CommonDropdownDto("722E", "722E"));
		oemShippingCtrlMstDto.setImpCodeList(shipCtrlImpCodeDtos);
		
		List<CommonDropdownDto> shipCtrlExpCodeDtos = new ArrayList<>();
		shipCtrlExpCodeDtos.add(new CommonDropdownDto("465K", "465K"));
		shipCtrlExpCodeDtos.add(new CommonDropdownDto("722E", "722E"));
		oemShippingCtrlMstDto.setExpCodeList(shipCtrlExpCodeDtos);
		
		List<CommonMultiSelectDropdownDto> shipCtrlCfcCodeDtos = new ArrayList<>();
		shipCtrlCfcCodeDtos.add(new CommonMultiSelectDropdownDto("000A", "000A"));
		shipCtrlCfcCodeDtos.add(new CommonMultiSelectDropdownDto("436L", "436L"));
		oemShippingCtrlMstDto.setCfcCodeList(shipCtrlCfcCodeDtos);
		
		List<CommonMultiSelectDropdownDto> shipCtrlSeriesDtos = new ArrayList<>();
		shipCtrlSeriesDtos.add(new CommonMultiSelectDropdownDto(SERIES, SERIES));
		shipCtrlSeriesDtos.add(new CommonMultiSelectDropdownDto("FORTUNER", "FORTUNER"));
		oemShippingCtrlMstDto.setSeriesList(shipCtrlSeriesDtos);
		
		List<CommonDropdownDto> shipCtrlSetPartCodeDtos = new ArrayList<>();
		shipCtrlSetPartCodeDtos.add(new CommonDropdownDto("L", "L Lot"));
		shipCtrlSetPartCodeDtos.add(new CommonDropdownDto("P", "P PxP"));
		oemShippingCtrlMstDto.setSetPartCodeList(shipCtrlSetPartCodeDtos);
		
		List<CommonMultiSelectDropdownDto> shipCtrlPortOfDischargeDtos = new ArrayList<>();
		shipCtrlPortOfDischargeDtos.add(new CommonMultiSelectDropdownDto("BTG", "BTG-BATANGAS"));
		shipCtrlPortOfDischargeDtos.add(new CommonMultiSelectDropdownDto("LAG", "LAG-LA GUAIRA"));
		oemShippingCtrlMstDto.setPortOfDischargeList(shipCtrlPortOfDischargeDtos);
		
		List<CommonMultiSelectDropdownDto> shipCtrlProductGroupDtos = new ArrayList<>();
		shipCtrlProductGroupDtos.add(new CommonMultiSelectDropdownDto("CL", "CL-COROLLA"));
		shipCtrlProductGroupDtos.add(new CommonMultiSelectDropdownDto("SL", "SL-VIOS"));
		oemShippingCtrlMstDto.setProductGroupList(shipCtrlProductGroupDtos);
		
		List<CommonDropdownDto> certificationOfOriginReportList = new ArrayList<>();
		certificationOfOriginReportList.add(new CommonDropdownDto("Y", "Y"));
		certificationOfOriginReportList.add(new CommonDropdownDto("N", "N"));
		oemShippingCtrlMstDto.setCertificationOfOriginReportList(certificationOfOriginReportList);
		
		List<CommonDropdownDto> shipCtrlTradeTermDtos = new ArrayList<>();
		shipCtrlTradeTermDtos.add(new CommonDropdownDto("FOB", "FOB"));
		shipCtrlTradeTermDtos.add(new CommonDropdownDto("CIF", "CIF"));
		oemShippingCtrlMstDto.setTradeTermList(shipCtrlTradeTermDtos);
		
		List<CommonMultiSelectDropdownDto> shipCtrlSoldToMessrsDtos = new ArrayList<>();
		shipCtrlSoldToMessrsDtos.add(new CommonMultiSelectDropdownDto("TMX", "TMX-TOYOTA MOTOR ASIA PACIFIC PTE, LTD."));
		shipCtrlSoldToMessrsDtos.add(new CommonMultiSelectDropdownDto("TMI", "TMI-PT.TOYOTA MOTOR MANUFACTURING INDONESIA"));
		oemShippingCtrlMstDto.setSoldToMessrsList(shipCtrlSoldToMessrsDtos);
		
		when(invShippingCtrlMstServiceImpl.fetchInvShippingCtrlMst()).thenReturn(oemShippingCtrlMstDto);

		ResponseEntity<OemShippingCtrlMstDto> result = invShippingCtrlMstController.fetchInvShippingCtrlMst();
		assertThat(result.getBody()).isNotNull();
		assertEquals(oemShippingCtrlMstDto, result.getBody());
		
	}
	
	@Test
	void fetchConsigneeAndNotifyPartyByBuyerTest() {

		List<CommonMultiSelectDropdownDto> shipCtrlConsigneeDtos = new ArrayList<>();
		shipCtrlConsigneeDtos.add(new CommonMultiSelectDropdownDto("1", "1-Toyota Motor Thailand Co., Ltd."));
		
		List<CommonMultiSelectDropdownDto> shipCtrlNotifyPartyDtos = new ArrayList<>();
		shipCtrlNotifyPartyDtos.add(new CommonMultiSelectDropdownDto("1", "1-TOYOTA MOTOR ASIA PACIFIC PTE, LTD."));
		
		ConsigneeAndNotifyPartyDto consigneeAndNotifyPartyDto = new ConsigneeAndNotifyPartyDto();
		consigneeAndNotifyPartyDto.setConsigneeList(shipCtrlConsigneeDtos);
		consigneeAndNotifyPartyDto.setNotifyPartyList(shipCtrlNotifyPartyDtos);
		
		when(invShippingCtrlMstServiceImpl.fetchConsigneeAndNotifyPartyByBuyer(anyString())).thenReturn(consigneeAndNotifyPartyDto);

		ResponseEntity<ConsigneeAndNotifyPartyDto> result = invShippingCtrlMstController.fetchConsigneeAndNotifyPartyByBuyer("TMT");
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(consigneeAndNotifyPartyDto, result.getBody());

	}
	
	@Test
	void saveShippingControlMasterTest() {
		//Test success scenario
		when(invShippingCtrlMstServiceImpl.saveShippingControlMaster(anyList())).thenReturn(true);
		
		List<OemShippingCtrlMstSaveRequestDto> oemShippingCtrlMstSaveRequestDtoList = new ArrayList<>();
		OemShippingCtrlMstSaveRequestDto oemShippingCtrlMstSaveRequestDto = new OemShippingCtrlMstSaveRequestDto();
		oemShippingCtrlMstSaveRequestDto.setBuyer("TMT");
		oemShippingCtrlMstSaveRequestDto.setImpCode("722E");
		oemShippingCtrlMstSaveRequestDto.setExpCode("465K");
		oemShippingCtrlMstSaveRequestDto.setCfcCode("000A");
		oemShippingCtrlMstSaveRequestDto.setSeries(SERIES);
		oemShippingCtrlMstSaveRequestDto.setSetPartCode("P");
		oemShippingCtrlMstSaveRequestDto.setPortOfDischarge("BTG");
		oemShippingCtrlMstSaveRequestDto.setProductGroup("CL");
		oemShippingCtrlMstSaveRequestDto.setFolderName("Non-IMV");
		oemShippingCtrlMstSaveRequestDto.setConsignee("1");
		oemShippingCtrlMstSaveRequestDto.setNotifyParty("1");
		oemShippingCtrlMstSaveRequestDto.setGoodDesc1("Toyota Component Parts");
		oemShippingCtrlMstSaveRequestDto.setGoodDesc2("For Model : VIOS");
		oemShippingCtrlMstSaveRequestDto.setGoodDesc3("For Detail See Attachment");
		oemShippingCtrlMstSaveRequestDto.setTradeTerm("CIF");
		oemShippingCtrlMstSaveRequestDto.setCertificationOfOriginReport("Y");
		oemShippingCtrlMstSaveRequestDto.setSoldToMessrs("TMX");
		oemShippingCtrlMstSaveRequestDto.setPlsFlag("Y");
		oemShippingCtrlMstSaveRequestDto.setUpdateByUserId("Test");
		oemShippingCtrlMstSaveRequestDto.setIsNewRow("Y");
		
		oemShippingCtrlMstSaveRequestDtoList.add(oemShippingCtrlMstSaveRequestDto);
		
		ResponseEntity<ApiResponseMessage> result = invShippingCtrlMstController.saveShippingControlMaster(oemShippingCtrlMstSaveRequestDtoList);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		
		when(invShippingCtrlMstServiceImpl.saveShippingControlMaster(anyList())).thenReturn(false);
		oemShippingCtrlMstSaveRequestDto.setSetPartCode("PPPP");
		oemShippingCtrlMstSaveRequestDtoList.add(oemShippingCtrlMstSaveRequestDto);
		Assertions.assertThrows(MyResourceNotFoundException.class,
				() -> invShippingCtrlMstController.saveShippingControlMaster(oemShippingCtrlMstSaveRequestDtoList));
	}
	
	@Test
	void deleteShippingControlMasterTest() {
		Mockito.doNothing().when(invShippingCtrlMstServiceImpl).deleteShippingControlMaster(anyList());
		
		List<OemShippingCtrlMstDeleteRequestDto> oemShippingCtrlMstDeleteRequestDtoList = new ArrayList<>();
		OemShippingCtrlMstDeleteRequestDto oemShippingCtrlMstDeleteRequestDto = new OemShippingCtrlMstDeleteRequestDto();
		oemShippingCtrlMstDeleteRequestDto.setImpCode("722E");
		oemShippingCtrlMstDeleteRequestDto.setExpCode("465K");
		oemShippingCtrlMstDeleteRequestDto.setCfcCode("000A");
		oemShippingCtrlMstDeleteRequestDto.setSeries(SERIES);
		oemShippingCtrlMstDeleteRequestDto.setSetPartCode("P");
		oemShippingCtrlMstDeleteRequestDto.setPortOfDischarge("BTG");
		
		oemShippingCtrlMstDeleteRequestDtoList.add(oemShippingCtrlMstDeleteRequestDto);
		
		ResponseEntity<ApiResponseMessage> result = invShippingCtrlMstController.deleteShippingControlMaster(oemShippingCtrlMstDeleteRequestDtoList);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

	}
}
