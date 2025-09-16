package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.SearchInvHaisenDetailResponse;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.OrderType;
import com.tpex.invoice.dto.SearchByInvNoResponseDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailRequestDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailResponseDto;
import com.tpex.invoice.dto.UpdateInvDetailsRequestDTO;
import com.tpex.invoice.serviceimpl.InvSearchServiceImpl;
import com.tpex.repository.NoemHaisenDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.validator.SearchInvHaisenDetailRequestDtoValidation;

@Import(SearchInvHaisenDetailRequestDtoValidation.class)
@SuppressWarnings("squid:S5778")
@ExtendWith(MockitoExtension.class)
class InvoiceSearchControllerTest {

	private static final String ETA_DATE = "19/06/2022";
	private static final String KR22102701 = "KR22102701";
	private static final String sDate = "14/11/2004";
	private static final String eta = "08/12/2004";
	@InjectMocks
	InvSearchController invSearchController;
	@Mock
	InvSearchServiceImpl invSearchServiceImpl;

	@Mock
	NoemHaisenDtlsRepository noemHaisenDtlsRepository;

	DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

	@Test
	void searchByInvoiceNoTest() throws Exception {

		when(invSearchServiceImpl.searchByInvNo(KR22102701)).thenReturn(searchByInvNoDumy());
		ResponseEntity<SearchByInvNoResponseDto> response = invSearchController.getByInvoiceNo(KR22102701);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals(ETA_DATE, response.getBody().getEtaDate());
	}

	@Test
	void searchByInvoiceNoNullTest() throws Exception {
		when(invSearchServiceImpl.searchByInvNo(KR22102701)).thenReturn(null);
		Assertions.assertThrows(MyResourceNotFoundException.class,
				() -> invSearchController.getByInvoiceNo(KR22102701));
	}

	SearchByInvNoResponseDto searchByInvNoDumy() {
		SearchByInvNoResponseDto dumyObj = new SearchByInvNoResponseDto();
		dumyObj.setInvDate("12/05/2022");
		dumyObj.setEtdDate("17/06/2022");
		dumyObj.setEtaDate(ETA_DATE);
		return dumyObj;
	}

	/**
	 * Test case for All field entered
	 * 
	 * @throws Exception
	 */
	@Test
	void fetchHaisenDetailsTest() throws Exception {

		when(noemHaisenDtlsRepository.getDetailsByBuyer("TSM")).thenReturn(1);
		when(invSearchServiceImpl.fetchHaisenDetail(getSearchHaisenValue(), "R", "C", "S"))
				.thenReturn(searchHaisenDetailRespDumy());
		ResponseEntity<SearchInvHaisenDetailResponse> response = invSearchController
				.fetchHaisenDetails(getSearchHaisenValue());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	/**
	 * Test case for no record found in DB
	 * 
	 * @throws Exception
	 */
	@Test
	void fetchHaisenDetailsNullTest() throws Exception {

		when(noemHaisenDtlsRepository.getDetailsByBuyer("TSM")).thenReturn(1);
		when(invSearchServiceImpl.fetchHaisenDetail(getSearchHaisenValue(), "R", "C", "S")).thenReturn(null);
		Assertions.assertThrows(MyResourceNotFoundException.class,
				() -> invSearchController.fetchHaisenDetails(getSearchHaisenValue()));
	}


	/**
	 * Test case for input wrong buyer details
	 * 
	 * @throws Exception
	 */
	@Test
	void fetchHaisenDetailsWrongBuyer() throws Exception {

		when(noemHaisenDtlsRepository.getDetailsByBuyer("TSMS")).thenReturn(0);
		Assertions.assertThrows(MyResourceNotFoundException.class,
				() -> invSearchController.fetchHaisenDetails(getSearchHaisenWrongBuyerValue()));
	}

	/**
	 * Test case for input regular and cpo/spo both are false
	 * 
	 * @throws Exception
	 */
	@Test
	void fetchHaisenDetailsMandatoryFieldNotCheck() throws Exception {

		Assertions.assertThrows(MyResourceNotFoundException.class,
				() -> invSearchController.fetchHaisenDetails(getSearchHaisenBothRegularCopUncheck()));
	}

	/**
	 * Test case for Etd from date greater than eta date
	 * 
	 * @throws Exception
	 */
	@Test
	void fetchHaisenDetailsEtdGraterthanEta() throws Exception {

		Assertions.assertThrows(MyResourceNotFoundException.class,
				() -> invSearchController.fetchHaisenDetails(getSearchHaisenEtdGreaterthanEta()));
	}

	// Request method for all field
	SearchInvHaisenDetailRequestDto getSearchHaisenValue() throws Exception {
		String sDate1 = sDate;
		String etdTodate = eta;
		java.util.Date date = format.parse(sDate1);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto = new SearchInvHaisenDetailRequestDto();
		searchInvHaisenDetailRequestDto.setEtdFrom(sqlDate);
		searchInvHaisenDetailRequestDto.setEtdTo(new java.sql.Date(format.parse(etdTodate).getTime()));
		searchInvHaisenDetailRequestDto.setBuyer("TSM");
		OrderType orderType = new OrderType();
		orderType.setRegular(true);
		orderType.setCpoOrspo(true);
		searchInvHaisenDetailRequestDto.setOrderType(orderType);
		return searchInvHaisenDetailRequestDto;
	}

//Request method for mandatory field etdFrom not entered
	SearchInvHaisenDetailRequestDto getSearchHaisenEtdFromNotEntered() throws Exception {
		SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto = new SearchInvHaisenDetailRequestDto();
		OrderType orderType = new OrderType();
		orderType.setRegular(true);
		orderType.setCpoOrspo(true);
		searchInvHaisenDetailRequestDto.setOrderType(orderType);
		return searchInvHaisenDetailRequestDto;
	}

//Request method for Wrong buyer value
	SearchInvHaisenDetailRequestDto getSearchHaisenWrongBuyerValue() throws Exception {
		String sDate1 = sDate;
		java.util.Date date = format.parse(sDate1);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto = new SearchInvHaisenDetailRequestDto();
		searchInvHaisenDetailRequestDto.setEtdFrom(sqlDate);
		searchInvHaisenDetailRequestDto.setBuyer("TSMS");
		OrderType orderType = new OrderType();
		orderType.setRegular(true);
		orderType.setCpoOrspo(true);
		searchInvHaisenDetailRequestDto.setOrderType(orderType);
		return searchInvHaisenDetailRequestDto;
	}

//Request method for both regular and cpo/spo uncheck
	SearchInvHaisenDetailRequestDto getSearchHaisenBothRegularCopUncheck() throws Exception {
		String sDate1 = sDate;
		java.util.Date date = format.parse(sDate1);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto = new SearchInvHaisenDetailRequestDto();
		searchInvHaisenDetailRequestDto.setEtdFrom(sqlDate);
		OrderType orderType = new OrderType();
		orderType.setRegular(false);
		orderType.setCpoOrspo(false);
		searchInvHaisenDetailRequestDto.setOrderType(orderType);
		return searchInvHaisenDetailRequestDto;
	}

	// Request method for etd date greather than eta date
	SearchInvHaisenDetailRequestDto getSearchHaisenEtdGreaterthanEta() throws Exception {
		String etdFrom = "14/11/2023";
		String etdTodate = eta;
		java.util.Date date = format.parse(etdFrom);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto = new SearchInvHaisenDetailRequestDto();
		searchInvHaisenDetailRequestDto.setEtdFrom(sqlDate);
		searchInvHaisenDetailRequestDto.setEtdTo(new java.sql.Date(format.parse(etdTodate).getTime()));
		OrderType orderType = new OrderType();
		orderType.setRegular(true);
		orderType.setCpoOrspo(true);
		searchInvHaisenDetailRequestDto.setOrderType(orderType);
		return searchInvHaisenDetailRequestDto;
	}

	/**
	 * Response method
	 * 
	 * @return javed
	 */
	SearchInvHaisenDetailResponse searchHaisenDetailRespDumy() {

		SearchInvHaisenDetailResponse resp = new SearchInvHaisenDetailResponse();

		List<SearchInvHaisenDetailResponseDto> list = new ArrayList<>();
		SearchInvHaisenDetailResponseDto searchInvHaisenDetailResponseDto = new SearchInvHaisenDetailResponseDto();
		searchInvHaisenDetailResponseDto.setHaisenYearMonth("2004/11");
		searchInvHaisenDetailResponseDto.setHaisenNo("TH136");
		searchInvHaisenDetailResponseDto.setBuyer("TSM");
		searchInvHaisenDetailResponseDto.setPortOfDischarge("LCH-LAEM CHABANG");
		searchInvHaisenDetailResponseDto.setPortOfLoading("LCH-LAEM CHABANG");
		searchInvHaisenDetailResponseDto.setEtd(sDate);
		searchInvHaisenDetailResponseDto.setEta(eta);
		searchInvHaisenDetailResponseDto.setOceanVessel("DAINTY RIVER");
		searchInvHaisenDetailResponseDto.setOceanVoyage("137W");
		list.add(searchInvHaisenDetailResponseDto);
		resp.setListInvHaisenDetailResponse(list);
		return resp;
	}
	
	@Test
	void updateInvoiceSearchResponseAndSaveTest() throws Exception {
		
		UpdateInvDetailsRequestDTO updateRequest=new UpdateInvDetailsRequestDTO();
		updateRequest.setInvNo("KR22106826");
		updateRequest.setCustCode("TMC");
		updateRequest.setConsineeName("PT.TOYOTA MOTOR MANUFACTURING INDONESIA");
		updateRequest.setNotifyPartyName("MS. EXPRESS  S.A.  (PTY)  LTD.");
		updateRequest.setPaymentTermObj("71");
		updateRequest.setScAuthorize("MR. CHAVALIT VESARNSEATTAPUN");
		updateRequest.setProductGrpObj("RHA-Description");
		
		updateRequest.setIndMark1("UMW");
		updateRequest.setIndMark2("722E/812B");
		updateRequest.setIndMark3("SERIES : HILUX");
		updateRequest.setIndMark4("EW0047-0052,EX0084-0089");
		updateRequest.setIndMark5("GA,GB,SA,SB");
		updateRequest.setIndMark7("PORT KLANG, MALAYSIA");
		updateRequest.setIndMark8("Test Mark8");
		updateRequest.setIndGoodsDesc1("COMPONENT PARTS FOR");
		updateRequest.setIndGoodsDesc2("TOYOTA HILUX");
		updateRequest.setIndGoodsDesc3("For Detail See Attachment");
		updateRequest.setIndGoodsDesc4("Goods Desc 4");
		updateRequest.setIndGoodsDesc5("Goods Desc 5");
		updateRequest.setIndGoodsDesc6("Goods Desc 6");
		
		
		ResponseEntity<ApiResponseMessage> updateInvoiceSearchResponseAndSave = invSearchController.updateInvoiceSearchResponseAndSave(updateRequest);
		assertThat(updateInvoiceSearchResponseAndSave.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals(ConstantUtils.INFO_IN_1002, updateInvoiceSearchResponseAndSave.getBody().getStatusMessage());
		verify(invSearchServiceImpl, times(1)).updateInvDetailsByInvNo(any(UpdateInvDetailsRequestDTO.class));
	}
}
