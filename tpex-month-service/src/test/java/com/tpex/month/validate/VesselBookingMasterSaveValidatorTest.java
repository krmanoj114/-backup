package com.tpex.month.validate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import com.tpex.month.model.dto.MixedVesselBooking;
import com.tpex.month.model.dto.ValidList;
import com.tpex.month.model.dto.VesselBookingMasterSaveRequest;
import com.tpex.month.model.repository.CustomRenbanBookMasterRepository;

@ExtendWith(MockitoExtension.class)
class VesselBookingMasterSaveValidatorTest {

	@InjectMocks
	private VesselBookingMasterSaveValidator vesselBookingMasterSaveValidator;
	@Mock
	private Errors errors;
	@Mock
	private CustomRenbanBookMasterRepository customRenbanBookMasterRepository;
	
	@Test
	void supportsTest() {
		ValidList<VesselBookingMasterSaveRequest> request = new ValidList<>();
		VesselBookingMasterSaveRequest req = new VesselBookingMasterSaveRequest();
		req.setUpdate(false);
		request.add(req);
		assertTrue(vesselBookingMasterSaveValidator.supports(request.getClass()));
	}
	
	/*@Test
	void validateTest() {
		ValidList<VesselBookingMasterSaveRequest> request = new ValidList<>();
		VesselBookingMasterSaveRequest req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("14/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("ABC");
		req.setBookingNo("ABC12345678901");
		req.setVessel1("ABCVesselMOL01");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("MOL");
		req.setOldBookingNo("MOL12345678901");
		req.setOldVessel1("testVesselMOL01");
		req.setUpdate(false);
		request.add(req);
		req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("15/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("MOL");
		req.setBookingNo("dupBookNo1");
		req.setVessel1("testVesselMOL01");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("");
		req.setOldBookingNo("");
		req.setOldVessel1("");
		req.setUpdate(false);
		request.add(req);
		req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("15/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("MOL");
		req.setBookingNo("ABC12345678901");
		req.setVessel1("testVesselMOL01");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("");
		req.setOldBookingNo("");
		req.setOldVessel1("");
		req.setUpdate(false);
		request.add(req);
		req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("15/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("MOL");
		req.setBookingNo("noChange");
		req.setVessel1("testVesselMOL01");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("");
		req.setOldBookingNo("noChange");
		req.setOldVessel1("");
		req.setUpdate(false);
		request.add(req);
		req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("15/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("");
		req.setBookingNo("");
		req.setVessel1("");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("MOL");
		req.setOldBookingNo("hasChange");
		req.setOldVessel1("hasChange");
		req.setUpdate(true);
		request.add(req);
		req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("15/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("TTK");
		req.setBookingNo("");
		req.setVessel1("");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("TTK");
		req.setOldBookingNo("hasChange");
		req.setOldVessel1("hasChange");
		req.setUpdate(true);
		request.add(req);
		req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("15/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("TTK");
		req.setBookingNo("bookNo");
		req.setVessel1("vessel1");
		req.setGroupId("1");
		req.setVanningMonth("202209");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("TTK");
		req.setOldBookingNo("bookNo");
		req.setOldVessel1("vessel1");
		req.setUpdate(true);
		request.add(req);
		req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setEtd1("15/09/2022");
		req.setFinalEta("25/09/2022");
		req.setRenbanCode("F,N");
		req.setNoOfContainer20ft("0");
		req.setNoOfContainer40ft("10");
		req.setShippingCompany("ONE");
		req.setCustomBrokerCode("TTK");
		req.setBookingNo("bookNo");
		req.setVessel1(null);
		req.setGroupId("1");
		req.setVanningMonth("202210");
		req.setVanEndDate("20220912000000");
		req.setCbFlag("N");
		req.setOldCustomBrokerCode("TTK");
		req.setOldBookingNo("bookNo1");
		req.setOldVessel1("vessel1");
		req.setUpdate(false);
		request.add(req);
		
		List<MixedVesselBooking> dupList = new ArrayList<>();
		MixedVesselBooking dup = new MixedVesselBooking();
		dup.setBookingNo("dupBookNo1");
		dup.setVanningMonth("202209");
		dupList.add(dup);
		
		when(customRenbanBookMasterRepository.findDuplicateBookNo(ArgumentMatchers.<List<MixedVesselBooking>>any())).thenReturn(dupList);
		vesselBookingMasterSaveValidator.validate(request, errors);
		
		verify(errors, atLeastOnce()).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}*/

}
