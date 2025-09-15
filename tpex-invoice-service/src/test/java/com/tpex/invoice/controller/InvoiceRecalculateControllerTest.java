package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InvoiceNumberResponseDTO;
import com.tpex.invoice.dto.InvoiceReCalculateRequestDto;
import com.tpex.invoice.dto.InvoiceReCalculationRequestDTO;
import com.tpex.invoice.dto.InvoiceReCalculationResponseDTO;
import com.tpex.invoice.dto.InvoiceRecalculatePart;
import com.tpex.invoice.service.InvoiceRecalculateService;

@ExtendWith(MockitoExtension.class)
class InvoiceRecalculateControllerTest {
	
	@InjectMocks
	InvoiceRecalculateController invoiceRecalculateController;
	
	@Mock
	InvoiceRecalculateService invoiceRecalculateService;
	

	private static String invoiceNo = "KC22102079";
	private static String partNo = "426520K85300";
	private static String companyCode = "TMT";
	private static String privilege = "PR";
	@Test
	void getInvDetailsByInvNumber() throws ParseException {
		
		  InvoiceNumberResponseDTO invoiceNumberResponseDTO = new InvoiceNumberResponseDTO();
		  
		  List<CommonMultiSelectDropdownDto> partNumbers = new ArrayList();
		  CommonMultiSelectDropdownDto commonMultiSelectDropdownDto = new CommonMultiSelectDropdownDto(partNo, "42652-0K853-00TIRE, TUBELESS");
		  
		  partNumbers.add(commonMultiSelectDropdownDto);
		  
		  invoiceNumberResponseDTO.setBuyerCode("UMW");
		  invoiceNumberResponseDTO.setCurrencyCode("USD");
		  invoiceNumberResponseDTO.setEta("16/12/2022");
		  invoiceNumberResponseDTO.setEtd("12/12/2022");
		  invoiceNumberResponseDTO.setImporterCode("812B");
		  invoiceNumberResponseDTO.setInvoiceNumber(invoiceNo);
		  invoiceNumberResponseDTO.setInvoiceType("4");
		  invoiceNumberResponseDTO.setPartNumber(partNumbers);
		  invoiceNumberResponseDTO.setPrivilegeCode(null);
		  
		  Mockito.when(invoiceRecalculateService.getDetailsByInvNo(invoiceNo, "TMT")).thenReturn(invoiceNumberResponseDTO);
		  ResponseEntity<InvoiceNumberResponseDTO> invoiceNumberResponseDTOResponse = invoiceRecalculateController.getInvDetailsByInvNumber(invoiceNo, "TMT");
		  
		  assertNotNull(invoiceNumberResponseDTOResponse.getBody());
			assertEquals("USD", invoiceNumberResponseDTOResponse.getBody().getCurrencyCode());
		}
	
	
	@Test
	void testSearchInvRecalulateDetails() throws ParseException {
		
		InvoiceReCalculationResponseDTO invoiceReCalculationResponseDTO = new InvoiceReCalculationResponseDTO();
		InvoiceReCalculationRequestDTO request = new InvoiceReCalculationRequestDTO();
		List<String> partNumber = new ArrayList<>();
		partNumber.add(partNo);
		request.setInvoiceNumber(invoiceNo);
		request.setPartNumber(partNumber);
		request.setCompanyCode(companyCode);
		request.setPrivilege(privilege);
		
		invoiceReCalculationResponseDTO.setFlag("Y");
		invoiceReCalculationResponseDTO.setCfCode("889W");
		invoiceReCalculationResponseDTO.setCfSeries("HILUX");
		invoiceReCalculationResponseDTO.setPartNo("1");
		invoiceReCalculationResponseDTO.setPartName("TIRE, TUBELESS");
		
		List<InvoiceReCalculationResponseDTO> response = new ArrayList<>();
		
		response.add(invoiceReCalculationResponseDTO);
		
		Mockito.when(invoiceRecalculateService.getInvRecalculateDetails(invoiceNo, partNumber, privilege, companyCode)).thenReturn(response);

		ResponseEntity<List<InvoiceReCalculationResponseDTO>> finalResponse = invoiceRecalculateController.searchInvRecalulateDetails(request);
		
		 assertNotNull(finalResponse.getBody());
		 assertThat(finalResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		
	}
	
	@Test
	void testRecalculateInvoice() {
		
		Mockito.doNothing().when(invoiceRecalculateService).recalculateInvoice(Mockito.any());

		InvoiceReCalculateRequestDto invoiceReCalculateRequestDto = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto.setInvoiceNumber("KR22102686");
		invoiceReCalculateRequestDto.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto.setUserId("TestUser");
		invoiceReCalculateRequestDto.setPrivilege("PP");
		List<InvoiceRecalculatePart> invoiceRecalculateParts = new ArrayList<>();
		invoiceRecalculateParts.add(new InvoiceRecalculatePart("22371-0L180-00", "16", "4.00", "2.00", "300.00", "200.00"));
		invoiceReCalculateRequestDto.setPartDetails(invoiceRecalculateParts);
		ResponseEntity<ApiResponseMessage> response = invoiceRecalculateController.recalculateInvoice(invoiceReCalculateRequestDto);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}
	
	@Test
	void testRecalculateInvoiceCustomException() throws ParseException {
		
		Mockito.doThrow(MyResourceNotFoundException.class).when(invoiceRecalculateService).recalculateInvoice(Mockito.any());

		InvoiceReCalculateRequestDto invoiceReCalculateRequestDto = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto.setInvoiceNumber("KR22102686");
		invoiceReCalculateRequestDto.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto.setUserId("TestUser");
		invoiceReCalculateRequestDto.setPrivilege("PP");
		List<InvoiceRecalculatePart> invoiceRecalculateParts = new ArrayList<>();
		invoiceRecalculateParts.add(new InvoiceRecalculatePart("22371-0L180-00", "16", "4.00", "2.00", "300.00", "200.00"));
		invoiceReCalculateRequestDto.setPartDetails(invoiceRecalculateParts);
		
	    assertThrows(MyResourceNotFoundException.class, () -> invoiceRecalculateController.recalculateInvoice(invoiceReCalculateRequestDto));
	}
	
	@Test
	void testRecalculateInvoiceException() throws ParseException {
		
		Mockito.doThrow(new RuntimeException("Exception")).when(invoiceRecalculateService).recalculateInvoice(Mockito.any(InvoiceReCalculateRequestDto.class));

		InvoiceReCalculateRequestDto invoiceReCalculateRequestDto = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto.setInvoiceNumber("KR22102686");
		invoiceReCalculateRequestDto.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto.setUserId("TestUser");
		invoiceReCalculateRequestDto.setPrivilege("PP");
		List<InvoiceRecalculatePart> invoiceRecalculateParts = new ArrayList<>();
		invoiceRecalculateParts.add(new InvoiceRecalculatePart("22371-0L180-00", "16", "4.00", "2.00", "300.00", "200.00"));
		invoiceReCalculateRequestDto.setPartDetails(invoiceRecalculateParts);
		
	    assertThrows(Exception.class, () -> invoiceRecalculateController.recalculateInvoice(invoiceReCalculateRequestDto));
	}
}