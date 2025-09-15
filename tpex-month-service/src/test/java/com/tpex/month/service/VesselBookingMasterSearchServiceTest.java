package com.tpex.month.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.tpex.month.model.dto.CommonPaginationRequest;
import com.tpex.month.model.dto.MixedVesselBooking;
import com.tpex.month.model.dto.PaginationRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearchRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearch;
import com.tpex.month.model.repository.CustomVesselBookingMasterRepository;

@ExtendWith(MockitoExtension.class)
class VesselBookingMasterSearchServiceTest {

	@InjectMocks
	private VesselBookingMasterSearchService vesselBookingMasterSearchService;
	
	@Mock
	private CustomVesselBookingMasterRepository customVesselBookingMasterRepository;
	
	private CommonPaginationRequest<VesselBookingMasterSearchRequest> req;
	private List<VesselBookingMasterSearch> listMst;
	
	private final String RENBAN = "RENBAN";
	private final String MONTHLY = "MONTHLY";
	private final String KEIHEN = "KEIHEN";
	private final String DAILY = "DAILY";
	
	@BeforeEach
	void init() {
		this.req = new CommonPaginationRequest<>();
		VesselBookingMasterSearchRequest reqBody = new VesselBookingMasterSearchRequest();
		reqBody.setDestinationCode("709B");
		reqBody.setVanningMonth("2022/09");
		reqBody.setEtdFrom("01/09/2022");
		reqBody.setEtdTo("30/09/2022");
		reqBody.setShippingCompanyCode("ONE");
		req.setRequestBody(reqBody);
		
		this.listMst = new ArrayList<>();
		VesselBookingMasterSearch resBody = new VesselBookingMasterSearch();
		resBody.setDestinationCode("709B");
		resBody.setEtd1("24/09/2022");
		resBody.setFinalEta("09/10/2022");
		resBody.setRenbanCode(null);
		resBody.setNoOfContainer20ft("1");
		resBody.setNoOfContainer40ft("7");
		resBody.setShippingCompany("ONE");
		resBody.setBookingStatus("Not Maintained");
		resBody.setCustomBrokerCode(null);
		resBody.setCustomBrokerName(null);
		resBody.setBookingNo("");
		resBody.setVessel1("");
		resBody.setGroupId("1");
		resBody.setRenbanCode("X,Y,Z");
		listMst.add(resBody);
		resBody = new VesselBookingMasterSearch();
		resBody.setDestinationCode("709B");
		resBody.setEtd1("26/09/2022");
		resBody.setFinalEta("09/10/2022");
		resBody.setRenbanCode(null);
		resBody.setNoOfContainer20ft("1");
		resBody.setNoOfContainer40ft("7");
		resBody.setShippingCompany("ONE");
		resBody.setBookingStatus("Not Maintained");
		resBody.setCustomBrokerCode(null);
		resBody.setCustomBrokerName(null);
		resBody.setBookingNo("");
		resBody.setVessel1("");
		resBody.setGroupId("1");
		resBody.setRenbanCode("A,B,C");
		listMst.add(resBody);
		resBody = new VesselBookingMasterSearch();
		resBody.setDestinationCode("709B");
		resBody.setEtd1("26/09/2022");
		resBody.setFinalEta("09/10/2022");
		resBody.setRenbanCode(null);
		resBody.setNoOfContainer20ft("1");
		resBody.setNoOfContainer40ft("7");
		resBody.setShippingCompany("ONE");
		resBody.setBookingStatus("Not Maintained");
		resBody.setCustomBrokerCode(null);
		resBody.setCustomBrokerName(null);
		resBody.setBookingNo("");
		resBody.setVessel1("");
		resBody.setGroupId("2");
		listMst.add(resBody);
		resBody = new VesselBookingMasterSearch();
		resBody.setDestinationCode("709B");
		resBody.setEtd1("28/09/2022");
		resBody.setFinalEta("09/10/2022");
		resBody.setRenbanCode(null);
		resBody.setNoOfContainer20ft("1");
		resBody.setNoOfContainer40ft("7");
		resBody.setShippingCompany("ONE");
		resBody.setBookingStatus("Not Maintained");
		resBody.setCustomBrokerCode(null);
		resBody.setCustomBrokerName(null);
		resBody.setBookingNo("");
		resBody.setVessel1("");
		resBody.setGroupId("1");
		listMst.add(resBody);
	}
	
	@Test
	void searchVesselBookingMasterAllValueTest() {
		
		List<MixedVesselBooking> listShipComp = new ArrayList<>();
		MixedVesselBooking mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("26/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("28/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		
		List<MixedVesselBooking> listAll = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(MONTHLY);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(MONTHLY);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(2);
		mix.setCont40(7);
		mix.setListType(KEIHEN);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(2);
		mix.setCont40(7);
		mix.setListType(KEIHEN);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(3);
		mix.setCont40(8);
		mix.setListType(DAILY);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(DAILY);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(RENBAN);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(2);
		mix.setCont40(7);
		mix.setListType(RENBAN);
		listAll.add(mix);
		
		PaginationRequest pagination = req.getPagination();
		
		Page<VesselBookingMasterSearch> pageRet = new PageImpl<>(listMst,
				PageRequest.of(pagination.getPageNo(), pagination.getPageSize()), listMst.size());

		when(customVesselBookingMasterRepository.findRenbanBookingData(
				ArgumentMatchers.<VesselBookingMasterSearchRequest>any(), ArgumentMatchers.<Pageable>any()))
				.thenReturn(pageRet);
		when(customVesselBookingMasterRepository.findKeihenRev(ArgumentMatchers.<String>any())).thenReturn("1");
		when(customVesselBookingMasterRepository.findShipCompETD(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listShipComp);
		when(customVesselBookingMasterRepository.getAllTypeGrpCnt(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listAll);
		
		Page<VesselBookingMasterSearch> page = vesselBookingMasterSearchService.searchVesselBookingMaster(req);
		
		List<VesselBookingMasterSearch> content = page.getContent();
		
		assertFalse(content.isEmpty());
		assertEquals("X,Y,Z", content.get(0).getRenbanCode());
		assertEquals("No. of container change, booking not update.", content.get(0).getBookingStatus());
		assertEquals("A,B,C", content.get(1).getRenbanCode());
		assertEquals("No. of container change, booking not update.", content.get(1).getBookingStatus());
		assertTrue(StringUtils.isBlank(content.get(2).getRenbanCode()));
		assertEquals("Not Maintained", content.get(2).getBookingStatus());
	}
	
	@Test
	void searchVesselBookingMasterKeihenDailyTest() {
		
		List<MixedVesselBooking> listShipComp = new ArrayList<>();
		MixedVesselBooking mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("26/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("28/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		
		List<MixedVesselBooking> listAll = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(2);
		mix.setCont40(7);
		mix.setListType(KEIHEN);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(KEIHEN);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(DAILY);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(DAILY);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(2);
		mix.setCont40(7);
		mix.setListType(RENBAN);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(RENBAN);
		listAll.add(mix);
		
		PaginationRequest pagination = req.getPagination();
		
		Page<VesselBookingMasterSearch> pageRet = new PageImpl<>(listMst,
				PageRequest.of(pagination.getPageNo(), pagination.getPageSize()), listMst.size());

		when(customVesselBookingMasterRepository.findRenbanBookingData(
				ArgumentMatchers.<VesselBookingMasterSearchRequest>any(), ArgumentMatchers.<Pageable>any()))
				.thenReturn(pageRet);
		when(customVesselBookingMasterRepository.findKeihenRev(ArgumentMatchers.<String>any())).thenReturn("1");
		when(customVesselBookingMasterRepository.findShipCompETD(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listShipComp);
		when(customVesselBookingMasterRepository.getAllTypeGrpCnt(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listAll);
		
		Page<VesselBookingMasterSearch> page = vesselBookingMasterSearchService.searchVesselBookingMaster(req);
		
		List<VesselBookingMasterSearch> content = page.getContent();
		
		assertFalse(content.isEmpty());
		assertEquals("X,Y,Z", content.get(0).getRenbanCode());
		assertEquals("Not Maintained", content.get(0).getBookingStatus());
		assertEquals("A,B,C", content.get(1).getRenbanCode());
		assertEquals("Not Maintained", content.get(1).getBookingStatus());
		assertTrue(StringUtils.isBlank(content.get(2).getRenbanCode()));
		assertEquals("Not Maintained", content.get(2).getBookingStatus());
	}
	
	@Test
	void searchVesselBookingMasterMonthlyKeihenTest() {
		
		List<MixedVesselBooking> listShipComp = new ArrayList<>();
		MixedVesselBooking mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("26/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("28/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		
		List<MixedVesselBooking> listAll = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(2);
		mix.setCont40(7);
		mix.setListType(MONTHLY);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(MONTHLY);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(3);
		mix.setCont40(8);
		mix.setListType(KEIHEN);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(KEIHEN);
		listAll.add(mix);
		
		PaginationRequest pagination = req.getPagination();
		
		Page<VesselBookingMasterSearch> pageRet = new PageImpl<>(listMst,
				PageRequest.of(pagination.getPageNo(), pagination.getPageSize()), listMst.size());

		when(customVesselBookingMasterRepository.findRenbanBookingData(
				ArgumentMatchers.<VesselBookingMasterSearchRequest>any(), ArgumentMatchers.<Pageable>any()))
				.thenReturn(pageRet);
		when(customVesselBookingMasterRepository.findKeihenRev(ArgumentMatchers.<String>any())).thenReturn("1");
		when(customVesselBookingMasterRepository.findShipCompETD(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listShipComp);
		when(customVesselBookingMasterRepository.getAllTypeGrpCnt(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listAll);
		
		Page<VesselBookingMasterSearch> page = vesselBookingMasterSearchService.searchVesselBookingMaster(req);
		
		List<VesselBookingMasterSearch> content = page.getContent();
		
		assertFalse(content.isEmpty());
		assertEquals("X,Y,Z", content.get(0).getRenbanCode());
		assertEquals("No. of container change, booking not update.", content.get(0).getBookingStatus());
		assertEquals("A,B,C", content.get(1).getRenbanCode());
		assertEquals("No. of container change, booking not update.", content.get(1).getBookingStatus());
		assertTrue(StringUtils.isBlank(content.get(2).getRenbanCode()));
		assertEquals("Not Maintained", content.get(2).getBookingStatus());
	}
	
	@Test
	void searchVesselBookingMasterMonthlyDailyTest() {
		
		List<MixedVesselBooking> listGrpCont = new ArrayList<>();
		MixedVesselBooking mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setEtd("24/09/2022");
		mix.setRenbanCode("X,Y,Z");
		listGrpCont.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setEtd("26/09/2022");
		mix.setRenbanCode("A,B,C");
		listGrpCont.add(mix);
		
		List<MixedVesselBooking> listNewGrp = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setGroupId("1");
		mix.setShippingCompanyCode("ONE");
		mix.setContGroupCode("X");
		listNewGrp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setGroupId("1");
		mix.setShippingCompanyCode("ONE");
		mix.setContGroupCode("Y");
		listNewGrp.add(mix);
		
		List<MixedVesselBooking> listShipComp = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("26/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("28/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		
		List<MixedVesselBooking> listAll = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(2);
		mix.setCont40(7);
		mix.setListType(MONTHLY);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(MONTHLY);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(3);
		mix.setCont40(8);
		mix.setListType(DAILY);
		listAll.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("2");
		mix.setCont20(1);
		mix.setCont40(5);
		mix.setListType(DAILY);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(3);
		mix.setCont40(8);
		mix.setListType(RENBAN);
		listAll.add(mix);
		
		PaginationRequest pagination = req.getPagination();
		
		Page<VesselBookingMasterSearch> pageRet = new PageImpl<>(listMst,
				PageRequest.of(pagination.getPageNo(), pagination.getPageSize()), listMst.size());

		when(customVesselBookingMasterRepository.findRenbanBookingData(
				ArgumentMatchers.<VesselBookingMasterSearchRequest>any(), ArgumentMatchers.<Pageable>any()))
				.thenReturn(pageRet);
		when(customVesselBookingMasterRepository.findKeihenRev(ArgumentMatchers.<String>any())).thenReturn("1");
		when(customVesselBookingMasterRepository.findShipCompETD(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listShipComp);
		when(customVesselBookingMasterRepository.getAllTypeGrpCnt(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listAll);
		
		Page<VesselBookingMasterSearch> page = vesselBookingMasterSearchService.searchVesselBookingMaster(req);
		
		List<VesselBookingMasterSearch> content = page.getContent();
		
		assertFalse(content.isEmpty());
		assertEquals("X,Y,Z", content.get(0).getRenbanCode());
		assertEquals("Not Maintained", content.get(0).getBookingStatus());
		assertEquals("A,B,C", content.get(1).getRenbanCode());
		assertEquals("Not Maintained", content.get(1).getBookingStatus());
		assertTrue(StringUtils.isBlank(content.get(2).getRenbanCode()));
		assertEquals("Not Maintained", content.get(2).getBookingStatus());
	}
	
	@Test
	void searchVesselBookingMasterKeihenTest() {
		
		List<MixedVesselBooking> listGrpCont = new ArrayList<>();
		MixedVesselBooking mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setEtd("24/09/2022");
		mix.setRenbanCode("X,Y,Z");
		listGrpCont.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setEtd("26/09/2022");
		mix.setRenbanCode("A,B,C");
		listGrpCont.add(mix);
		
		List<MixedVesselBooking> listNewGrp = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setGroupId("1");
		mix.setShippingCompanyCode("ONE");
		mix.setContGroupCode("X");
		listNewGrp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setGroupId("1");
		mix.setShippingCompanyCode("ONE");
		mix.setContGroupCode("Y");
		listNewGrp.add(mix);
		
		List<MixedVesselBooking> listShipComp = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("26/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("28/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		
		List<MixedVesselBooking> listAll = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(3);
		mix.setCont40(8);
		mix.setListType(KEIHEN);
		listAll.add(mix);
		
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(3);
		mix.setCont40(8);
		mix.setListType(RENBAN);
		listAll.add(mix);
		
		PaginationRequest pagination = req.getPagination();
		
		Page<VesselBookingMasterSearch> pageRet = new PageImpl<>(listMst,
				PageRequest.of(pagination.getPageNo(), pagination.getPageSize()), listMst.size());

		when(customVesselBookingMasterRepository.findRenbanBookingData(
				ArgumentMatchers.<VesselBookingMasterSearchRequest>any(), ArgumentMatchers.<Pageable>any()))
				.thenReturn(pageRet);
		when(customVesselBookingMasterRepository.findKeihenRev(ArgumentMatchers.<String>any())).thenReturn("1");
		when(customVesselBookingMasterRepository.findShipCompETD(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listShipComp);
		when(customVesselBookingMasterRepository.getAllTypeGrpCnt(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listAll);
		
		Page<VesselBookingMasterSearch> page = vesselBookingMasterSearchService.searchVesselBookingMaster(req);
		
		List<VesselBookingMasterSearch> content = page.getContent();
		
		assertFalse(content.isEmpty());
		assertEquals("X,Y,Z", content.get(0).getRenbanCode());
		assertEquals("Not Maintained", content.get(0).getBookingStatus());
		assertEquals("A,B,C", content.get(1).getRenbanCode());
		assertEquals("Not Maintained", content.get(1).getBookingStatus());
		assertTrue(StringUtils.isBlank(content.get(2).getRenbanCode()));
		assertEquals("Not Maintained", content.get(2).getBookingStatus());
	}
	
	@Test
	void searchVesselBookingMasterDailyTest() {
		
		List<MixedVesselBooking> listGrpCont = new ArrayList<>();
		MixedVesselBooking mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setEtd("24/09/2022");
		mix.setRenbanCode("X,Y,Z");
		listGrpCont.add(mix);
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setEtd("26/09/2022");
		mix.setRenbanCode("A,B,C");
		listGrpCont.add(mix);
		
		List<MixedVesselBooking> listNewGrp = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setGroupId("1");
		mix.setShippingCompanyCode("ONE");
		mix.setContGroupCode("X");
		listNewGrp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setGroupId("1");
		mix.setShippingCompanyCode("ONE");
		mix.setContGroupCode("Y");
		listNewGrp.add(mix);
		
		List<MixedVesselBooking> listShipComp = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setEtd("24/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("26/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		mix = new MixedVesselBooking();
		mix.setEtd("28/09/2022");
		mix.setKeihenRevNo("1");
		mix.setShippingCompanyCode("ONE");
		mix.setDestinationCode("709B");
		listShipComp.add(mix);
		
		List<MixedVesselBooking> listAll = new ArrayList<>();
		mix = new MixedVesselBooking();
		mix.setGroupId("1");
		mix.setCont20(0);
		mix.setCont40(0);
		mix.setListType(DAILY);
		listAll.add(mix);
		
		PaginationRequest pagination = req.getPagination();
		
		Page<VesselBookingMasterSearch> pageRet = new PageImpl<>(listMst,
				PageRequest.of(pagination.getPageNo(), pagination.getPageSize()), listMst.size());

		when(customVesselBookingMasterRepository.findRenbanBookingData(
				ArgumentMatchers.<VesselBookingMasterSearchRequest>any(), ArgumentMatchers.<Pageable>any()))
				.thenReturn(pageRet);
		when(customVesselBookingMasterRepository.findKeihenRev(ArgumentMatchers.<String>any())).thenReturn("1");
		when(customVesselBookingMasterRepository.findShipCompETD(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listShipComp);
		when(customVesselBookingMasterRepository.getAllTypeGrpCnt(ArgumentMatchers.<MixedVesselBooking>any())).thenReturn(listAll);
		
		Page<VesselBookingMasterSearch> page = vesselBookingMasterSearchService.searchVesselBookingMaster(req);
		
		List<VesselBookingMasterSearch> content = page.getContent();
		
		assertFalse(content.isEmpty());
		assertEquals("X,Y,Z", content.get(0).getRenbanCode());
		assertEquals("No. of container change, booking not update.", content.get(0).getBookingStatus());
		assertEquals("A,B,C", content.get(1).getRenbanCode());
		assertEquals("No. of container change, booking not update.", content.get(1).getBookingStatus());
		assertTrue(StringUtils.isBlank(content.get(2).getRenbanCode()));
		assertEquals("Not Maintained", content.get(2).getBookingStatus());
	}

}
