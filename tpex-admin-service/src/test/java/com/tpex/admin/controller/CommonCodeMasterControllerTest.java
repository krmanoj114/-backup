package com.tpex.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;

import com.tpex.admin.service.CommonCodeMasterServiceImpl;
import com.tpex.admin.commonfiles.ApiResponseMessage;
import com.tpex.admin.dto.BuyerMasterDTO;
import com.tpex.admin.dto.CodeMasterDTO;
import com.tpex.admin.dto.CodeMasterRequestDTO;
import com.tpex.admin.dto.CommonCodeMasterListDTO;
import com.tpex.admin.dto.CurrencyMasterDTO;
import com.tpex.admin.dto.FinalDestinationMasterResponseDto;
import com.tpex.admin.dto.ColumnsDTO;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.TpexConfigurationUtil;

@ExtendWith(MockitoExtension.class)
class CommonCodeMasterControllerTest {

	@InjectMocks
	CommonCodeMasterController commonCodeMasterController;

	@Mock
	TpexConfigurationUtil tpexConfigurationUtil;

	@Mock
	CommonCodeMasterServiceImpl commonCodeMasterService;

	@Test
	void codeMasterNamesTest() throws Exception {
		CodeMasterRequestDTO request = new CodeMasterRequestDTO();
		request.setUserId("test");

		String path = ResourceUtils.getFile("classpath:codeMasterNames.json").getPath();

		when(tpexConfigurationUtil.getFilePath(anyString())).thenReturn(path);

		ResponseEntity<List<CodeMasterDTO>> codeMasterNames = commonCodeMasterController.codeMasterNames(request);

		assertThat(codeMasterNames.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(codeMasterNames.getBody()).isNotEmpty();
	}

	@Test
	void codeMasterNameTest() throws Exception {
		CodeMasterRequestDTO request = new CodeMasterRequestDTO();
		request.setUserId("test");
		request.setCodeMasterId(1);
		request.setCodeMasterName("master");
		request.setJsonFileName("jsonFile");
		request.setCmpCd("TMT");
		ColumnsDTO columnsDTO = new ColumnsDTO();
		columnsDTO.setUserId("test");
		List<Map<String, String>> list = new ArrayList<>();

		when(tpexConfigurationUtil.readDataFromJson(anyString())).thenReturn(columnsDTO);
		when(commonCodeMasterService.codeMasterData(any(ColumnsDTO.class),anyString())).thenReturn(list);

		ResponseEntity<CommonCodeMasterListDTO> codeMasterName = commonCodeMasterController.codeMasterName(request);

		assertThat(codeMasterName.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(codeMasterName.getBody()).isNotNull();
	}

	@Test
	void saveCodeMasterTest() throws Exception {
		CodeMasterRequestDTO request = new CodeMasterRequestDTO();
		request.setUserId("test");

		ResponseEntity<ApiResponseMessage> saveCodeMaster = commonCodeMasterController.saveCodeMaster(request);

		assertThat(saveCodeMaster.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("INFO_CM_3003", saveCodeMaster.getBody().getStatusMessage());
		verify(commonCodeMasterService, times(1)).saveCodeMaster(any(CodeMasterRequestDTO.class));
	}

	@Test
	void savePaymentTermMasterTest() throws Exception {
		CodeMasterRequestDTO request = new CodeMasterRequestDTO();
		request.setUserId("test");
		request.setCodeMasterName(ConstantUtils.PAYMENT_TERM_CODE);
		ResponseEntity<ApiResponseMessage> saveCodeMaster = commonCodeMasterController.saveCodeMaster(request);

		assertThat(saveCodeMaster.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("INFO_CM_3003", saveCodeMaster.getBody().getStatusMessage());
		verify(commonCodeMasterService, times(1)).savePaymentTermMaster(any(CodeMasterRequestDTO.class), anyBoolean());
	}

	@Test
	void deleteCodeMasterTest() throws Exception {
		CodeMasterRequestDTO request = new CodeMasterRequestDTO();
		request.setUserId("test");

		ResponseEntity<ApiResponseMessage> result = commonCodeMasterController.deleteCodeMaster(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("INFO_CM_3006", result.getBody().getStatusMessage());
		verify(commonCodeMasterService, times(1)).deleteCodeMaster(any(CodeMasterRequestDTO.class));
	}

	@Test
	void updateCodeMasterTest() throws Exception {
		CodeMasterRequestDTO request = new CodeMasterRequestDTO();
		request.setUserId("test");

		ResponseEntity<ApiResponseMessage> result = commonCodeMasterController.updateCodeMaster(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("INFO_CM_3007", result.getBody().getStatusMessage());
		verify(commonCodeMasterService, times(1)).updateCodeMaster(any(CodeMasterRequestDTO.class));
	}

	@Test
	void updatePaymentTermMasterTest() throws Exception {
		CodeMasterRequestDTO request = new CodeMasterRequestDTO();
		request.setUserId("test");
		request.setCodeMasterName(ConstantUtils.PAYMENT_TERM_CODE);
		ResponseEntity<ApiResponseMessage> result = commonCodeMasterController.updateCodeMaster(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("INFO_CM_3007", result.getBody().getStatusMessage());
		verify(commonCodeMasterService, times(1)).savePaymentTermMaster(any(CodeMasterRequestDTO.class), anyBoolean());
	}

	@Test
	void finalDestinationControllerTest() throws Exception {

		FinalDestinationMasterResponseDto finalDestinationMaster = new FinalDestinationMasterResponseDto();

		List<BuyerMasterDTO> buyerMasterDTO = new ArrayList<>();
		buyerMasterDTO.add(new BuyerMasterDTO("001", "HONG KONG"));
		buyerMasterDTO.add(new BuyerMasterDTO("AMI", "AUSTRALIA"));

		List<CurrencyMasterDTO> currencyMasterDTO = new ArrayList<>();
		currencyMasterDTO.add(new CurrencyMasterDTO("YEN", "JAPAN YEN"));
		currencyMasterDTO.add(new CurrencyMasterDTO("USD", "DOLLARS"));

		finalDestinationMaster.setBuyer(buyerMasterDTO);
		finalDestinationMaster.setCurrency(currencyMasterDTO);

		String cmpCd = "TMT";
		when(commonCodeMasterService.finalDestinationDtls(cmpCd)).thenReturn(finalDestinationMaster);
		ResponseEntity<FinalDestinationMasterResponseDto> result = commonCodeMasterController.finalDestination(cmpCd);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(finalDestinationMaster, result.getBody());
	}

}
