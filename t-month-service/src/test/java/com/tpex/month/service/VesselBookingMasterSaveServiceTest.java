package com.tpex.month.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.month.model.dto.CommonResponse;
import com.tpex.month.model.dto.VesselBookingMasterSaveRequest;
import com.tpex.month.model.entity.RenbanBookMasterEntity;
import com.tpex.month.model.entity.RenbanBookMasterId;
import com.tpex.month.model.repository.RenbanBookDetailRespository;
import com.tpex.month.model.repository.RenbanBookMasterHistoryRepository;
import com.tpex.month.model.repository.RenbanBookMasterRespository;
import com.tpex.month.util.ConstantUtil;
import com.tpex.month.validate.VesselBookingMasterSaveValidator;

@ExtendWith(MockitoExtension.class)
class VesselBookingMasterSaveServiceTest {
	
	@InjectMocks
	private VesselBookingMasterSaveService vesselBookingMasterSaveService;
	@Mock
	private VesselBookingMasterSaveValidator vesselBookingMasterSaveValidator;
	@Mock
	private RenbanBookMasterRespository renbanBookMasterRespository;
	@Mock
	private RenbanBookDetailRespository renbanBookDetailRespository;
	@Mock
	private RenbanBookMasterHistoryRepository renbanBookMasterHistoryRepository;

	@Test
	void saveVesselBookingMasterUpdateTest() {
		List<VesselBookingMasterSaveRequest> request = new ArrayList<>();
		VesselBookingMasterSaveRequest req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("15/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("MOL");
		req.setBookingNo("updateBookNo");
		req.setVessel1("updateVessel");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("TTK");
		req.setOldBookingNo("existBookNo");
		req.setOldVessel1("existVessel");
		req.setUpdate(false);
		request.add(req);
		
		RenbanBookMasterEntity ent = new RenbanBookMasterEntity();
		ent.setDestinationCode("709B");
		ent.setEtd1(LocalDate.now());
		ent.setEta(LocalDate.now());
		ent.setCont20(1);
		ent.setCont40(9);
		ent.setShippingCompany("ONE");
		ent.setCustomBrokerCode("MOL");
		ent.setBookingNo("updateBookNo");
		ent.setVessel1("updateVessel");
		ent.setGroupId("1");
		ent.setVanningMonth("202209");
		ent.setCbFlag("N");
		
		Optional<RenbanBookMasterEntity> opt = Optional.of(ent);
		
		when(vesselBookingMasterSaveValidator.isChange(Mockito.any(VesselBookingMasterSaveRequest.class))).thenCallRealMethod();
		when(renbanBookMasterRespository.findById(Mockito.any(RenbanBookMasterId.class))).thenReturn(opt);
		
		CommonResponse response = vesselBookingMasterSaveService.saveVesselBookingMaster(request);
		
		assertEquals(ConstantUtil.INFO_CM_3003, response.getMessage());
	}
	
	@Test
	void saveVesselBookingMasterInsertTest() {
		List<VesselBookingMasterSaveRequest> request = new ArrayList<>();
		VesselBookingMasterSaveRequest req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("14/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("ABC");
		req.setBookingNo("insertBookNo");
		req.setVessel1("insertVessel");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("MOL");
		req.setOldBookingNo("MOL12345678901");
		req.setOldVessel1("testVesselMOL01");
		req.setUpdate(false);
		request.add(req);
		
		Optional<RenbanBookMasterEntity> opt = Optional.empty();
		
		when(vesselBookingMasterSaveValidator.isChange(Mockito.any(VesselBookingMasterSaveRequest.class))).thenCallRealMethod();
		when(renbanBookMasterRespository.findById(Mockito.any(RenbanBookMasterId.class))).thenReturn(opt);
		
		CommonResponse response = vesselBookingMasterSaveService.saveVesselBookingMaster(request);
		
		assertEquals(ConstantUtil.INFO_CM_3003, response.getMessage());
	}
	
	@Test
	void saveVesselBookingMasterNoChangeTest() {
		List<VesselBookingMasterSaveRequest> request = new ArrayList<>();
		VesselBookingMasterSaveRequest req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("14/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("MOL");
		req.setBookingNo("MOL12345678901");
		req.setVessel1("testVesselMOL01");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("MOL");
		req.setOldBookingNo("MOL12345678901");
		req.setOldVessel1("testVesselMOL01");
		req.setUpdate(false);
		request.add(req);
		
		when(vesselBookingMasterSaveValidator.isChange(Mockito.any(VesselBookingMasterSaveRequest.class))).thenCallRealMethod();
		
		CommonResponse response = vesselBookingMasterSaveService.saveVesselBookingMaster(request);
		
		assertEquals(ConstantUtil.INFO_CM_3008, response.getMessage());
	}
	
}
