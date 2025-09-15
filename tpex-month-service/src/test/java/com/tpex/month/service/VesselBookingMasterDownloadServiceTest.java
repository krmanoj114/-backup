package com.tpex.month.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.tpex.month.model.dto.CommonPaginationRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearchRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearch;
import com.tpex.month.model.entity.FinalDestinationMasterEntity;
import com.tpex.month.model.repository.FinalDestinationMasterRepository;
import com.tpex.month.util.ConstantUtil;
import com.tpex.month.util.TpexConfigUtils;

@ExtendWith(MockitoExtension.class)
class VesselBookingMasterDownloadServiceTest {
	
	@InjectMocks
	private VesselBookingMasterDownloadService vesselBookingMasterDownloadService;
	
	@Mock
	private VesselBookingMasterSearchService vesselBookingMasterSearchService;
	@Mock
	private FinalDestinationMasterRepository finalDestinationMasterRepository;
	@Mock
	private TpexConfigUtils tpexConfigUtils;
	
	private static Stream<Arguments> downloadVesselBookingMasterTestParam() {
	    return Stream.of(
	      Arguments.of("5", true),
	      Arguments.of("2", false)
	    );
	}
	
	@ParameterizedTest
	@MethodSource("downloadVesselBookingMasterTestParam")
	void downloadVesselBookingMasterOneFileTest(String limitRecord, boolean isOnline) {
		VesselBookingMasterSearchRequest reqBody = new VesselBookingMasterSearchRequest();
		reqBody.setDestinationCode("709B");
		reqBody.setVanningMonth("2022/09");
		reqBody.setEtdFrom("01/09/2022");
		reqBody.setEtdTo("30/09/2022");
		reqBody.setShippingCompanyCode("ONE");
		
		List<VesselBookingMasterSearch> list = new ArrayList<>();  
		list.add(getMockRespone("709B", "14/09/2022", "25/09/2022", "F,N", "0", "10", "ONE", "Not Maintained"));
		list.add(getMockRespone("709B", "21/09/2022", "02/10/2022", "F,N", "1", "8", "ONE", "No. of container change, booking not update."));
		list.add(getMockRespone("709B", "21/09/2022", "02/10/2022", "Y,Z", "0", "16", "ONE", "Not Maintained"));
		 
		FinalDestinationMasterEntity dest = new FinalDestinationMasterEntity();
		dest.setFdDstCd("709B");
		dest.setFdDstNm("INDIA");
		
		Page<VesselBookingMasterSearch> page = new PageImpl<>(list);
		
		when(tpexConfigUtils.getConfigValue(ConstantUtil.VESSEL_BOOK_LIMIT)).thenReturn(limitRecord);
		when(vesselBookingMasterSearchService.searchVesselBookingMaster(
				ArgumentMatchers.<CommonPaginationRequest<VesselBookingMasterSearchRequest>>any())).thenReturn(page);
		when(finalDestinationMasterRepository.findById(Mockito.anyString())).thenReturn(Optional.of(dest));
		when(tpexConfigUtils.getConfigValue(ConstantUtil.VESSEL_BOOK_PATH)).thenReturn("C:\\TPEXConfig\\VesselBookingMaster\\Download");
		
		Map<String, Object> map = vesselBookingMasterDownloadService.downloadVesselBookingMaster(reqBody);
		
		assertDownload(isOnline, map, ".zip");
	}

	private void assertDownload(boolean isOnline, Map<String, Object> map, String ext) {
		if(isOnline) {
			assertNotNull(map.get("outStream"));
			assertTrue(map.get("outStream") instanceof byte[]);
			assertNotNull(map.get("filename"));
			String fileName = (String) map.get("filename");
			assertEquals(ext, fileName.substring(fileName.lastIndexOf(".")));
		} else {
			assertTrue(map.containsKey(ConstantUtil.STATUS));
			assertTrue(map.containsKey(ConstantUtil.MSG));
			assertEquals(ConstantUtil.INFO_MN_4001, map.get(ConstantUtil.MSG));
		}
	}

	@ParameterizedTest
	@MethodSource("downloadVesselBookingMasterTestParam")
	void downloadVesselBookingMasterZipFileTest(String limitRecord, boolean isOnline) {
		VesselBookingMasterSearchRequest reqBody = new VesselBookingMasterSearchRequest();
		reqBody.setVanningMonth("2022/09");
		reqBody.setEtdFrom("01/09/2022");
		reqBody.setEtdTo("30/09/2022");
		
		List<VesselBookingMasterSearch> list = new ArrayList<>();
		list.add(getMockRespone("900A", "18/09/2022", "30/09/2022", "F,N", "1", "4", "ONE", "Not Maintained"));
		list.add(getMockRespone("709B", "21/09/2022", "02/10/2022", "F,N", "1", "8", "EGN", "No. of container change, booking not update."));
		list.add(getMockRespone("722B", "21/09/2022", "02/10/2022", "Y,Z", "0", "16", "ONE", "Not Maintained"));
		
		FinalDestinationMasterEntity dest1 = new FinalDestinationMasterEntity();
		dest1.setFdDstCd("709B");
		dest1.setFdDstNm("INDIA");
		FinalDestinationMasterEntity dest2 = new FinalDestinationMasterEntity();
		dest2.setFdDstCd("900A");
		dest2.setFdDstNm("JAPAN");
		FinalDestinationMasterEntity dest3 = new FinalDestinationMasterEntity();
		dest3.setFdDstCd("722B");
		dest3.setFdDstNm("THAILAND");
		
		Page<VesselBookingMasterSearch> page = new PageImpl<>(list);
		
		when(tpexConfigUtils.getConfigValue(ConstantUtil.VESSEL_BOOK_LIMIT)).thenReturn(limitRecord);
		when(vesselBookingMasterSearchService.searchVesselBookingMaster(
				ArgumentMatchers.<CommonPaginationRequest<VesselBookingMasterSearchRequest>>any())).thenReturn(page);
		when(finalDestinationMasterRepository.findById("709B")).thenReturn(Optional.of(dest1));
		when(finalDestinationMasterRepository.findById("900A")).thenReturn(Optional.of(dest2));
		when(finalDestinationMasterRepository.findById("722B")).thenReturn(Optional.of(dest3));
		when(tpexConfigUtils.getConfigValue(ConstantUtil.VESSEL_BOOK_PATH)).thenReturn("C:\\TPEXConfig\\VesselBookingMaster\\Download");
		
		Map<String, Object> map = vesselBookingMasterDownloadService.downloadVesselBookingMaster(reqBody);
		
		assertDownload(isOnline, map, ".zip");
	}

	private VesselBookingMasterSearch getMockRespone(String destinationCode, String etd1
			, String finalEta, String renbanCode, String noOfContainer20ft, String noOfContainer40ft,
			String shippingCompany, String status) {
		VesselBookingMasterSearch vbmsr = new VesselBookingMasterSearch(); 
		vbmsr.setDestinationCode(destinationCode);
		vbmsr.setFinalEta(finalEta);
		vbmsr.setRenbanCode(renbanCode);
		vbmsr.setEtd1(etd1);
		vbmsr.setVanningMonth("202209"); 
		vbmsr.setNoOfContainer20ft(noOfContainer20ft);
		vbmsr.setNoOfContainer40ft(noOfContainer40ft);
		vbmsr.setShippingCompany(shippingCompany);
		vbmsr.setCustomBrokerCode(null);
		vbmsr.setCustomBrokerName(null);
		vbmsr.setBookingNo("");
		vbmsr.setVessel1("");
		vbmsr.setBookingStatus(status);
		return vbmsr;
	}
	
}
