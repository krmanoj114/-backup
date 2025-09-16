package com.tpex.month.service;

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

import com.tpex.month.model.dto.CustomBrokerMasterDropdown;
import com.tpex.month.model.dto.DropDownVesselBookingMasterResponse;
import com.tpex.month.model.projection.CustomBrokerMasterCodeAndName;
import com.tpex.month.model.projection.FinalDestinationMasterCodeAndName;
import com.tpex.month.model.projection.ShippingCompanyMasterCode;
import com.tpex.month.model.repository.CustomBrokerMasterRepository;
import com.tpex.month.model.repository.FinalDestinationMasterRepository;
import com.tpex.month.model.repository.ShippingCompanyMasterRepository;

@ExtendWith(MockitoExtension.class)
class DropdownServiceTest {
	
	@InjectMocks
	private DropdownService dropdownService;
	@Mock
	private FinalDestinationMasterRepository finalDestinationMasterRepository;
	@Mock
	private ShippingCompanyMasterRepository shippingCompanyMasterRepository;
	@Mock
	private CustomBrokerMasterRepository customBrokerMasterRepository;

	class CustomBrokerMasterCodeAndNameImp implements CustomBrokerMasterCodeAndName {
		@Override
		public String getCbmCbCd() {
			return "CustomBrokerCode";
		}

		@Override
		public String getCbmCbNm() {
			return "CustomBrokerName";
		}
	}
	
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
		
		List<FinalDestinationMasterCodeAndName> listDest = new ArrayList<>();
		FinalDestinationMasterCodeAndName dest = new FinalDestinationMasterCodeAndNameImp();
		listDest.add(dest);
		List<ShippingCompanyMasterCode> listShip = new ArrayList<>();
		listShip.add(() -> "ShippingCompanyCode");
		
		when(finalDestinationMasterRepository.findAllProjectedCodeAndNameByOrderByFdDstCd()).thenReturn(listDest);
		when(shippingCompanyMasterRepository.findAllProjectedCodeByOrderByScmCd()).thenReturn(listShip);
		
		DropDownVesselBookingMasterResponse res = dropdownService.getDropDownForVesselBookingMaster();
		
		assertNotNull(res);
		assertEquals("ShippingCompanyCode", res.getShippingCompany().get(0).getScmCd());
		assertEquals("DestinationCode", res.getFinalDestination().get(0).getFdDstCd());
		assertEquals("DestinationName", res.getFinalDestination().get(0).getFdDstNm());
	}
	
	@Test
	void getCustomBrokerMasterDropDownTest() {
        List<CustomBrokerMasterCodeAndName> listCust = new ArrayList<>();
        CustomBrokerMasterCodeAndName cust = new CustomBrokerMasterCodeAndNameImp();
        listCust.add(cust);
        
        when(customBrokerMasterRepository.findAllProjectedCodeAndNameByOrderByCbmCbCd()).thenReturn(listCust);
        
        List<CustomBrokerMasterDropdown> res = dropdownService.getCustomBrokerMasterDropDown();
        
        assertEquals("CustomBrokerCode", res.get(0).getCustomBrokerCode());
        assertEquals("CustomBrokerCode CustomBrokerName", res.get(0).getCustomBrokerDisplay());
	}

}
