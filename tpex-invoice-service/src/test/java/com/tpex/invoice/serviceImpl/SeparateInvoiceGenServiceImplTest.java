package com.tpex.invoice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.invoice.dto.SeparateInvoiceGenerationResponceDto;
import com.tpex.invoice.serviceimpl.SeparateInvoiceGenerationServiceImpl;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.OemParameterEntityRepository;
import com.tpex.repository.PrivilegeTypeEntityRepository;
import com.tpex.repository.SeperateInvoiceGenRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

@ExtendWith(MockitoExtension.class)
class SeparateInvoiceGenServiceImplTest {

	@InjectMocks
	SeparateInvoiceGenerationServiceImpl separateInvoiceGenerationService;

	@Mock
	SeperateInvoiceGenRepository seperateInvoiceGenRepository;

	@Mock
	JasperReportService jasperReportService;

	@Mock
	TpexConfigRepository tpexConfigRepository;

	@Mock
	OemParameterEntityRepository oemParameterEntityRepository;

	@Mock
	PrivilegeTypeEntityRepository privilegeTypeEntityRepository;

	@Test
	void testdownloadRINS012() throws Exception {

		final Double insUnitPerBo = 0d;
		final Double insPartW = 0d;
		final Double insGrossW = 0d;
		final Double insMeasurem = 0d;
		String insShipma4 = "";
		String insShipma5 = "";
		String invoiceNo = "KR22102669";
		String userId = "TestUser";

		String cmpCd = "TMT";

		SeparateInvoiceGenerationResponceDto ob = new SeparateInvoiceGenerationResponceDto();
		ob.setInsCnsgName("Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.");
		ob.setInsCnsgAdd1("99 Moo5, Bangna-Trad K.M. 29.5 Rd.");
		ob.setInsCnsgAdd2("T.Ban-Ragad, A.Bang Bo,Samutprakarn,");
		ob.setInsCnsgAdd3("10560 Thailand.Tel: (66-2) 790-5000");
		ob.setInsCnsgAdd4("Tax ID : 0115546006888");
		ob.setInsInvNo("KR22102669");
		ob.setInsInvDt("09/12/2022");
		ob.setInsPartNo("882100D05200");
		ob.setInsUnitPerBox(insUnitPerBo);
		ob.setInsSumTotUnit(null);
		ob.setInsIcoflg("360");
		ob.setInsPartPrice(null);
		ob.setInsPartName("1");
		ob.setInsPartWt(insPartW);
		ob.setInsGrossWt(insGrossW);
		ob.setInsMeasurement(insMeasurem);
		ob.setInsShipmark4(insShipma4);
		ob.setInsShipmark5(insShipma5);
		ob.setInsCfCd("848W");
		ob.setInsSrsName("VIOS");
		ob.setInsCurrCd("USD");
		ob.setInsIcoDesc("NON");
		ob.setInsCoCd("CN");

		List<SeparateInvoiceGenerationResponceDto> resultList = new ArrayList<>();
		resultList.add(ob);

		Mockito.lenient().when(oemParameterEntityRepository.findByOprParaCd()).thenReturn("TMT");

		Mockito.lenient().when(privilegeTypeEntityRepository.findByPrivilegeCode(invoiceNo)).thenReturn("Y");

		Mockito.lenient().when(seperateInvoiceGenRepository.getCompCode(invoiceNo)).thenReturn("TEST");
		List<Object[]> obj = new ArrayList<>();

		Mockito.lenient().when(seperateInvoiceGenRepository.getInvCompDetailWhenFlgN("companyCode")).thenReturn(obj);

		Mockito.lenient().when(seperateInvoiceGenRepository.getInvCompDetailWhenFlgY("compCode", cmpCd))
				.thenReturn(obj);

		Map<String, Object> config = new HashMap<>();

		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY, "C:/TPEXConfig/InvoiceGeneration/Report");
		config.put(ConstantUtils.REPORT_FORMAT, "xlsx");
		config.put(ConstantUtils.REPORT_SIZE_LIMIT, 90);
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put(ConstantUtils.TEST_USER, userId);
		parameters.put(ConstantUtils.INVOICE_NO, invoiceNo);

		String fileName = "KR22102669(B)_ATT";
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
				.append("/").append(fileName);
		String fileFormat = "pdf";
		String reportName = ConstantUtils.RINS012;

		jasperReportService.getJasperReportDownloadOffline(resultList, fileFormat, reportName, parameters, config, 0,
				sb);

		assertEquals("TestUser", parameters.get(ConstantUtils.TEST_USER));
		assertEquals("KR22102669", parameters.get(ConstantUtils.INVOICE_NO));

	}
}
