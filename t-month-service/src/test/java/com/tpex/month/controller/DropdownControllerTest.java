package com.tpex.month.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.month.model.dto.DropDownVesselBookingMasterResponse;
import com.tpex.month.model.projection.FinalDestinationMasterCodeAndName;
import com.tpex.month.model.projection.ShippingCompanyMasterCode;
import com.tpex.month.service.DropdownService;

@ExtendWith(MockitoExtension.class)
class DropdownControllerTest {
	
	@InjectMocks
	private DropdownController dropdownController;
	
	@Mock
	private DropdownService dropdownService;
	
	class FinalDestinationMasterCodeAndNameImp implements FinalDestinationMasterCodeAndName {

		@Override
		public String getFdDstCd() {
			return "DestinationCode";
		}

		@Override
		public String getFdDstNm() {
			return "DestinationName";
		}
	}
	
	@Test
	void getDropDownForVesselBookingMasterTest() {
		
		DropDownVesselBookingMasterResponse mockRes = new DropDownVesselBookingMasterResponse();
		List<FinalDestinationMasterCodeAndName> finalDestination = new ArrayList<>();
		List<ShippingCompanyMasterCode> shippingCompany = new ArrayList<>();
		
		finalDestination.add(new FinalDestinationMasterCodeAndNameImp());
		shippingCompany.add(() -> "ShippingCompanyCode");
		
		mockRes.setFinalDestination(finalDestination);
		mockRes.setShippingCompany(shippingCompany);
		
		when(dropdownService.getDropDownForVesselBookingMaster()).thenReturn(mockRes);
		
		ResponseEntity<DropDownVesselBookingMasterResponse> res = dropdownController.getDropDownForVesselBookingMaster();
		
		assertNotNull(res);
		assertEquals(HttpStatus.OK, res.getStatusCode());
		assertEquals("ShippingCompanyCode", res.getBody().getShippingCompany().get(0).getScmCd());
		assertEquals("DestinationCode", res.getBody().getFinalDestination().get(0).getFdDstCd());
		assertEquals("DestinationName", res.getBody().getFinalDestination().get(0).getFdDstNm());
	}

}
