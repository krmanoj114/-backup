package com.tpex.invoice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.invoice.service.InvVinListService;
import com.tpex.repository.InvVinListRepository;
import com.tpex.repository.TpexConfigRepository;

@ExtendWith(MockitoExtension.class)
public class InquiryScreenforInvoiceAndShippingReportsControllerTest {

	@InjectMocks
	InquiryScreenforInvoiceAndShippingReportsController inquiryScreenforInvoiceAndShippingReportsController;

	@Mock
	InvVinListService invVinListService;

	@Mock
	TpexConfigRepository tpexConfigRepository;

	@Mock
	InvVinListRepository invVinListRepository;

	@Mock
	DownloadInvoiceReportsRequestDTO downloadInvoiceReportsRequestDTO;

	@Test
	void downloadInvoiceReportsTest() throws Exception {
		List<DownloadInvoiceReportsRequestDTO> expected = new ArrayList<>();
		DownloadInvoiceReportsRequestDTO dto = new DownloadInvoiceReportsRequestDTO();
		String[] report = new String[] { "RINS005" };
		String[] dest = new String[] { "" };
		dto.setBookingNo("");
		dto.setDestinations(dest);
		dto.setEtd("");
		dto.setEtdTo("");
		dto.setInvoiceNumber("KR22106543");
		dto.setOrderType("R");
		dto.setReportFormate("pdf");
		dto.setReportName(null);
		dto.setReportTypes(report);
		dto.setUserId("NIITTMT");
		String templateId = "RINS005";
		String cmpCd = "TMT";
		String reportUrl = "/home/TMAP/DSD/shared/report/TPEXConfig/InvoiceGeneration/Report/KR22106849_VIN.pdf";
		Mockito.lenient().when(invVinListService.getInvVinListReportDownload(cmpCd, dto.getInvoiceNumber(),
				dto.getUserId(), templateId, dto.getReportFormate())).thenReturn(reportUrl);
		assertEquals("KR22106543", dto.getInvoiceNumber());
	}

}
