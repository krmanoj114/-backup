package com.tpex.month.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartException;

import com.tpex.month.exception.InvalidFileException;
import com.tpex.month.exception.MyResourceNotFoundException;
import com.tpex.month.model.dto.CommonPaginationRequest;
import com.tpex.month.model.dto.CommonPaginationResponse;
import com.tpex.month.model.dto.CommonResponse;
import com.tpex.month.model.dto.CustomBrokerMasterDropdown;
import com.tpex.month.model.dto.PaginationRequest;
import com.tpex.month.model.dto.ValidList;
import com.tpex.month.model.dto.VesselBookingMasterSaveRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearchRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearchResponse;
import com.tpex.month.model.dto.VesselBookingMasterSearch;
import com.tpex.month.service.DropdownService;
import com.tpex.month.service.VesselBookingMasterDownloadService;
import com.tpex.month.service.VesselBookingMasterSaveService;
import com.tpex.month.service.VesselBookingMasterSearchService;
import com.tpex.month.service.VesselBookingMasterUploadService;
import com.tpex.month.util.ConstantUtil;
import com.tpex.month.validate.VesselBookingMasterSaveValidator;

@ExtendWith(MockitoExtension.class)
class VesselBookingMasterControllerTest {

	@InjectMocks
	private VesselBookingMasterController vesselBookingMasterController;

	@Mock
	private VesselBookingMasterSearchService vesselBookingMasterSearchService;
	@Mock
	private VesselBookingMasterDownloadService vessBookingMasterDownloadService;
	@Mock
	private VesselBookingMasterSaveValidator vesselBookingMasterSaveValidator;
	@Mock
	private VesselBookingMasterSaveService vesselBookingMasterSaveService;
	@Mock
	private DropdownService dropdownService;
	@Mock
	private BindingResult bindingResult;

	
	@Mock
	private VesselBookingMasterUploadService vesselBookingMasterUploadService;
	private static String textPlain = "text/plain";
	private static String someXlsx = "some xlsx";
	private static String vesselBookingMasterExcelName = "vesselBookingMasterExcelName.xlsx";

	@Test
	void searchVesselBookingMasterTest() {

		CommonPaginationRequest<VesselBookingMasterSearchRequest> req = new CommonPaginationRequest<>();
		VesselBookingMasterSearchRequest reqBody = new VesselBookingMasterSearchRequest();
		reqBody.setDestinationCode("709B");
		reqBody.setVanningMonth("2022/09");
		reqBody.setEtdFrom("01/09/2022");
		reqBody.setEtdTo("30/09/2022");
		reqBody.setShippingCompanyCode("ONE");
		req.setRequestBody(reqBody);

		VesselBookingMasterSearch resBody = new VesselBookingMasterSearch();
		resBody.setDestinationCode("709B");
		resBody.setEtd1("28/09/2022");
		resBody.setFinalEta("09/10/2022");
		resBody.setRenbanCode("F,N");
		resBody.setNoOfContainer20ft("1");
		resBody.setNoOfContainer40ft("7");
		resBody.setShippingCompany("ONE");
		resBody.setBookingStatus("No. of container change, booking not update.");
		resBody.setCustomBrokerCode(null);
		resBody.setCustomBrokerName(null);
		resBody.setBookingNo("");
		resBody.setVessel1("");

		List<VesselBookingMasterSearch> list = new ArrayList<>();
		list.add(resBody);

		List<CustomBrokerMasterDropdown> ddList = new ArrayList<>();
		CustomBrokerMasterDropdown dd = new CustomBrokerMasterDropdown();
		dd.setCustomBrokerCode("TTK");
		dd.setCustomBrokerDisplay("TTK TTK");
		ddList.add(dd);

		PaginationRequest pagination = req.getPagination();

		Page<VesselBookingMasterSearch> page = new PageImpl<>(list,
				PageRequest.of(pagination.getPageNo(), pagination.getPageSize()), list.size());

		when(vesselBookingMasterSearchService.searchVesselBookingMaster(
				ArgumentMatchers.<CommonPaginationRequest<VesselBookingMasterSearchRequest>>any())).thenReturn(page);
		when(dropdownService.getCustomBrokerMasterDropDown()).thenReturn(ddList);

		ResponseEntity<CommonPaginationResponse<VesselBookingMasterSearchResponse>> res = vesselBookingMasterController
				.searchVesselBookingMaster(req);

		assertEquals(HttpStatus.OK, res.getStatusCode());
		assertNotNull(res.getBody());
		assertEquals(1, res.getBody().getPagination().getPageNo());
		assertEquals(1, res.getBody().getPagination().getTotalPages());
		assertEquals("709B", res.getBody().getResponseBody().getData().get(0).getDestinationCode());
	}

	@SuppressWarnings("unchecked")
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void downloadVesselBookingMasterTest(boolean isOnline) {
		VesselBookingMasterSearchRequest reqBody = new VesselBookingMasterSearchRequest();
		reqBody.setDestinationCode("709B");
		reqBody.setVanningMonth("2022/09");
		reqBody.setEtdFrom("01/09/2022");
		reqBody.setEtdTo("30/09/2022");
		reqBody.setShippingCompanyCode("ONE");

		Map<String, Object> map = new HashMap<>();
		if (isOnline) {
			map.put("outStream", "testByte".getBytes());
		} else {
			map.put(ConstantUtil.MSG, ConstantUtil.INFO_MN_4001);
			map.put(ConstantUtil.STATUS, ConstantUtil.OFFLINE);
		}

		when(vessBookingMasterDownloadService
				.downloadVesselBookingMaster(Mockito.any(VesselBookingMasterSearchRequest.class))).thenReturn(map);

		ResponseEntity<Object> res = vesselBookingMasterController.downloadVesselBookingMaster(reqBody);

		assertNotNull(res.getBody());
		if (isOnline) {
			assertTrue(res.getBody() instanceof byte[]);
		} else {
			assertTrue(res.getBody() instanceof HashMap);
			assertEquals(ConstantUtil.OFFLINE, ((HashMap<String, String>) res.getBody()).get(ConstantUtil.STATUS));
		}
	}

	@Test
	void saveVesselBookingMasterTest() throws Exception {
		ValidList<VesselBookingMasterSaveRequest> request = new ValidList<>();
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
		CommonResponse res = new CommonResponse();
		res.setStatus("Success");

		when(bindingResult.hasErrors()).thenReturn(false);
		when(vesselBookingMasterSaveService
				.saveVesselBookingMaster(ArgumentMatchers.<List<VesselBookingMasterSaveRequest>>any())).thenReturn(res);
		ResponseEntity<CommonResponse> response = vesselBookingMasterController.saveVesselBookingMaster(request,
				bindingResult);

		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void saveVesselBookingMasterInvalidTest() throws Exception {
		ValidList<VesselBookingMasterSaveRequest> request = new ValidList<>();
		VesselBookingMasterSaveRequest req = new VesselBookingMasterSaveRequest();
		req.setDestinationCode("709B");
		req.setUpdate(false);
		request.add(req);
		CommonResponse res = new CommonResponse();
		res.setStatus("Success");

		when(bindingResult.hasErrors()).thenReturn(true);
		assertThrows(MethodArgumentNotValidException.class,
				() -> vesselBookingMasterController.saveVesselBookingMaster(request, bindingResult));

	}

	
	@Test
	void uploadVesselBookingMasterTestFile() throws IOException, MultipartException {

		String batchName = "BINS294";
		String userId = "Test USER";

		MockMultipartFile file = new MockMultipartFile("file", "vesselBookingMaster.xlsxs", textPlain,
				someXlsx.getBytes());
		String filename = file.getOriginalFilename();

		if (filename != null && (!(filename.endsWith(".xls") || filename.endsWith(".xlsx")))) {
			Assertions.assertThrows(InvalidFileException.class,
					() -> vesselBookingMasterController.uploadVesselBookingMaster(batchName, userId, file));
		}
	}


	@Test
	void uploadVesselBookingMasterTestEmpty() throws IOException, MultipartException {

		String batchName = "";
		String userId = "Test";

		MockMultipartFile file = new MockMultipartFile("file", vesselBookingMasterExcelName, textPlain,
				someXlsx.getBytes());

		if (batchName.isBlank() || batchName.isEmpty() || file.isEmpty()) {
			Assertions.assertThrows(MyResourceNotFoundException.class,
					() -> vesselBookingMasterController.uploadVesselBookingMaster(batchName, userId, file));
		}
	}


	@Test
	void uploadVesselBookingMasterTestData() throws IOException, MultipartException {

		String batchName = "BINS294";
		String userId = "Test";

		MockMultipartFile file = new MockMultipartFile("file", vesselBookingMasterExcelName, textPlain,
				someXlsx.getBytes());

		when(vesselBookingMasterUploadService.uploadVesselBookingMaster(batchName, file, userId))
				.thenReturn(new ResponseEntity<Object>(HttpStatus.OK));
		vesselBookingMasterController.uploadVesselBookingMaster(batchName, userId, file);
		assertEquals(vesselBookingMasterExcelName, file.getOriginalFilename());
	}
}
