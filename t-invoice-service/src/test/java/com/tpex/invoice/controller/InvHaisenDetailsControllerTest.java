package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.dto.NoemHaisenDetailDTO;
import com.tpex.invoice.dto.InvoiceDetailsResponseDto;
import com.tpex.invoice.service.InvHaisenDetailsSeviceImpl;

@ExtendWith(MockitoExtension.class)
class InvHaisenDetailsControllerTest {

	@InjectMocks
	InvHaisenDetailsController invHaisenDetailsController;

	@Mock
	InvHaisenDetailsSeviceImpl invHaisenDetailsSeviceImpl;

	@Test
	void saveInvoiceHaisenDetailsTest() throws Exception {

		List<String> feederVessel = new ArrayList<>();
		feederVessel.add("test");

		List<String> feederVoyage = new ArrayList<>();
		feederVoyage.add("158W");

		List<NoemHaisenDetailDTO> list=new LinkedList<>();
		NoemHaisenDetailDTO request = new NoemHaisenDetailDTO();
		request.setHaisenYearMonth("202302");
		request.setHaisenNo("TB082");
		request.setEtd("17/12/2022");
		request.setEta("18/12/2022");
		request.setPortOfLoading("BKK");
		request.setPortOfDischarge("KHH");
		request.setOceanVessel("EVER PEARL");
		request.setOceanVoyage("0612-371S");
		request.setFeederVessel(feederVessel);
		request.setFeederVoyage(feederVoyage);

		list.add(request);

		String userId= "Tpextest";

		ResponseEntity<List<String>> response = invHaisenDetailsController.saveInvoiceHaisenDetails(userId, list);
	    verify(invHaisenDetailsSeviceImpl, times(1)).saveHaisenDetails(anyString(),anyList());


	}
	
	@Test
	void updateInvoiceDetailsTest() throws Exception {

		List<InvoiceDetailsResponseDto> list=new LinkedList<>();
		InvoiceDetailsResponseDto request = new InvoiceDetailsResponseDto();
		request.setHaisenNo("TB028");
		request.setEtdDate("02/10/2023");
		request.setEtaDate("02/12/2022");
		request.setFeederVoyage("YAT");
		request.setOceanVessel("TBA");
		request.setOceanVoyage("89");
		request.setInvoiceNo("KR22100541");
		request.setInvoiceDate("02/12/2022");
		request.setInvoiceAmount("117464");
		request.setInvoiceM3("329.9");
		request.setPortOfDischarge("LCH");
		request.setPortOfLoading("BKK");
		request.setShipCompName("ONE");
		request.setBuyer("UMW");

		list.add(request);
		
		String userId= "Tpextest";

		ResponseEntity<List<String>> response = invHaisenDetailsController.updateInvoiceDetails(userId, list);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(invHaisenDetailsSeviceImpl, times(1)).updateInvoiceDetails(anyString(),anyList());


	}


}
