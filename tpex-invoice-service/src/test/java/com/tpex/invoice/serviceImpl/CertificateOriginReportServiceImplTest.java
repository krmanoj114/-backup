package com.tpex.invoice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.entity.TpexConfigEntity;
import com.tpex.invoice.dto.CertificateOriginReportDTO;
import com.tpex.invoice.serviceimpl.CertificateOriginReportServiceImpl;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.CertificateOriginReportRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.TpexConfigurationUtil;

@ExtendWith(MockitoExtension.class)
class CertificateOriginReportServiceImplTest {
	@InjectMocks
	CertificateOriginReportServiceImpl certificateOriginReportServiceImpl;

	@Mock
	JasperReportService jasperReportService;

	@Mock
	TpexConfigRepository tpexConfigRepository;
	
	@Mock
	TpexConfigurationUtil tpexConfigurationUtil;

	@Mock
	CertificateOriginReportRepository certificateOriginReportRepository;
	
	@Mock
	TpexConfigEntity tpexConfigEntity; 

	@Mock
	 EntityManager manager;

	
	
	String name = "Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.";
	 String address1 = "99 Moo5, Bangna-Trad K.M. 29.5 Rd.";
	 String address2 = "T.Ban-Ragad, A.Bang Bo,Samutprakarn,";
	 String address3 = "10560 Thailand.Tel: (66-2) 790-5000";
	 String address4 = "Tax ID : 0115546006888";
	 String invoiceNumber = "KR22102669";
	 String partNumber = "311260K09000";
	 BigDecimal sumTotalUnit = new BigDecimal(175);
	 String partName = "BOOT, CLUTCH RELEASE FORK";
	 String consigneeName = "TOYOTA MYANMAR CO.,LTD.";
	 String consigneeAddress1 = "NO. BE-10, INDUSTRIAL AREA, ZONE B,";
	 String consigneeAddress2 = "THILAWA SPECIAL ECONOMIC ZONE,";
	 String consigneeAddress3 = "KYAUK TAN TOWNSHIP, YANGON, MYANMAR";
	 String consigneeAddress4 = "";
	 String consigneeTelephoneNumber = "";
	 String companyCode = "TH";
	 String notifyName = null;
	 String notifyAddress1 = null;
	 String notifyAddress2 = null;
	 String notifyAddress3 = null;
	 String notifyAddress4 = null;
	 String notifyZip = null;
	 String notifyCountry = null;
	 String companyName = null;
	 String vesselName = null;
	 String voyageNumber = null;
	 String destinationName = "LAEM CHABANG, JAPAN";
	 String etd = "02/06/2022";
	 String invoiceDate = "11/05/2022";
	 String userId = "NIITTMT";
	 String invoiceFlag = "Y";
	 String companyCode3 = "TMAPTH_CMP";
	 String reportName = "RINS106";
		
	@SuppressWarnings("unchecked")
	@Test
	void getCertificateOriginDownloadReportTest() throws Exception {
		 
		 
		 
		 Tuple mockedTuple = new Tuple() {
				@Override
				public Object[] toArray() {
					return null;
				}

				@Override
				public List<TupleElement<?>> getElements() {
					return null;
				}

				@Override
				public <X> X get(int i, Class<X> type) {
					return null;
				}

				@Override
				public <X> X get(String alias, Class<X> type) {
					return null;
				}

				@Override
				public Object get(int i) {

					Map<Integer, List<Object>> tuple = new HashMap<>();
					Object[] o = new Object[] { name,address1,address2,address3,address4,
							invoiceNumber,partNumber,sumTotalUnit,partName,consigneeName,
							consigneeAddress1,consigneeAddress2,consigneeAddress3,consigneeAddress4,
							consigneeTelephoneNumber,companyCode,notifyName,notifyAddress1,notifyAddress2,
							notifyAddress3,notifyAddress4,notifyZip,notifyCountry,companyName,vesselName,voyageNumber,destinationName,etd,invoiceDate };

					Object[] o1 = new Object[] { "Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.",
							"99 Moo5, Bangna-Trad K.M. 29.5 Rd.", "T.Ban-Ragad, A.Bang Bo,Samutprakarn,",
							"10560 Thailand.Tel: (66-2) 790-5000", "Tax ID : 0115546006888" };
					
					Object[] o2= new Object[] { "TOYOTA MYANMAR CO.,LTD.", "NO. BE-10, INDUSTRIAL AREA, ZONE B,", "THILAWA SPECIAL ECONOMIC ZONE,",
						"KYAUK TAN TOWNSHIP, YANGON, MYANMAR", "", "" };
					
					
					tuple.put(0, Arrays.asList(o));

					tuple.put(1, Arrays.asList(o1));
					
					tuple.put(2, Arrays.asList(o2));
					
					return tuple.get(i);

				}

				@Override

				public Object get(String alias) {
					return null;

				}

				@Override

				public <X> X get(TupleElement<X> tupleElement) {
					return null;
				}

			};

			List<CertificateOriginReportDTO> expected = new ArrayList<>();
			CertificateOriginReportDTO dto = new CertificateOriginReportDTO();
			List<Tuple> listObj = new ArrayList<>();
			dto.setName(name);
			dto.setAddress1(address1);
			dto.setAddress2(address2);
			dto.setAddress3(address3);
			dto.setAddress4(address4);
			dto.setInvoiceNumber(invoiceNumber);
			dto.setPartNumber(partNumber);
			dto.setSumTotalUnit(sumTotalUnit);
			dto.setPartName(partName);
			dto.setConsigneeName(consigneeName);
			dto.setConsigneeAddress1(consigneeAddress1);
			dto.setConsigneeAddress2(consigneeAddress2);
			dto.setConsigneeAddress3(consigneeAddress3);
			dto.setConsigneeAddress4(consigneeAddress4);
			dto.setConsigneeTelephoneNumber(consigneeTelephoneNumber);
			dto.setCompanyCode(companyCode);
			dto.setNotifyName(notifyName);
			dto.setNotifyAddress1(notifyAddress1);
			dto.setNotifyAddress2(notifyAddress2);
			dto.setNotifyAddress3(notifyAddress3);
			dto.setNotifyAddress4(notifyAddress4);
			dto.setNotifyZip(notifyZip);
			dto.setNotifyCountry(notifyCountry);
			dto.setCompanyName(companyName);
			dto.setVesselName(vesselName);
			dto.setVoyageNumber(voyageNumber);
			dto.setDestinationName(destinationName);
			dto.setEtd(etd);
			dto.setInvoiceDate(invoiceDate);
			
			expected.add(dto);
				Mockito.lenient().when(certificateOriginReportRepository.getTmapthInvFlg(Mockito.anyString())).thenReturn(invoiceFlag);
				Mockito.lenient().when(certificateOriginReportRepository.getCompCode(Mockito.anyString())).thenReturn(companyCode3);
				listObj.addAll((Collection<? extends Tuple>) mockedTuple.get(0));
				Mockito.lenient().when(certificateOriginReportRepository.getNotifyDetails(invoiceNumber)).thenReturn(listObj);
				Tuple invoiceCompanyDetail = mockedTuple.get(1, Tuple.class);
				if ("Y".equalsIgnoreCase(invoiceFlag)) {
					Mockito.lenient().when(certificateOriginReportRepository.getInvCompDetailWhenFlgY(Mockito.any(),Mockito.any())).thenReturn(invoiceCompanyDetail);
				}
				else if ("N".equalsIgnoreCase(invoiceFlag)) {
					Mockito.lenient().when(certificateOriginReportRepository.getInvCompDetailWhenFlgN(Mockito.any())).thenReturn(invoiceCompanyDetail);
				}
				
				Tuple invoiceConsigneeDetail = mockedTuple.get(2, Tuple.class);
				Mockito.lenient().when(certificateOriginReportRepository.getConsigneeDetails()).thenReturn(invoiceConsigneeDetail);
				assertEquals("KR22102669", invoiceNumber);
				
	}
	
	@Test
	void getCertificateOrigin() throws Exception{
		certificateOriginReportServiceImpl.getcertificateOriginReportData("TMT", "KR22102669");
		
		assertEquals("KR22102669", invoiceNumber);
	}
	
	
	
	@Test
	void getCertificateOriginDownloadReporTest() throws Exception{
		List<CertificateOriginReportDTO> expected = new ArrayList<>();
		CertificateOriginReportDTO dto = new CertificateOriginReportDTO();
		dto.setAddress1(address1);
		dto.setAddress2(address2);
		dto.setAddress3(address3);
		dto.setAddress4(address4);
		dto.setInvoiceNumber(invoiceNumber);
		dto.setPartNumber(partNumber);
		dto.setSumTotalUnit(sumTotalUnit);
		dto.setPartName(partName);
		dto.setConsigneeName(consigneeName);
		dto.setConsigneeAddress1(consigneeAddress1);
		dto.setConsigneeAddress2(consigneeAddress2);
		dto.setConsigneeAddress3(consigneeAddress3);
		dto.setConsigneeAddress4(consigneeAddress4);
		dto.setConsigneeTelephoneNumber(consigneeTelephoneNumber);
		dto.setCompanyCode(companyCode);
		dto.setNotifyName(notifyName);
		dto.setNotifyAddress1(notifyAddress1);
		dto.setNotifyAddress2(notifyAddress2);
		dto.setNotifyAddress3(notifyAddress3);
		dto.setNotifyAddress4(notifyAddress4);
		dto.setNotifyZip(notifyZip);
		dto.setNotifyCountry(notifyCountry);
		dto.setCompanyName(companyName);
		dto.setVesselName(vesselName);
		dto.setVoyageNumber(voyageNumber);
		dto.setDestinationName(destinationName);
		dto.setEtd(etd);
		dto.setInvoiceDate(invoiceDate);
		expected.add(dto);
			Map<String, Object> parameters = new HashMap<>();

			parameters.put(ConstantUtils.INVOICE_NO, "KR22102669");
			parameters.put(ConstantUtils.USER_ID, "NIITTMT");
			Map<String, Object> config = new HashMap<>();
			config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
			config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
			config.put(ConstantUtils.REPORT_DIRECTORY, "C:/TPEXConfig/InvoiceGeneration/Report");
			config.put(ConstantUtils.REPORT_FORMAT, "xlsx");
			config.put(ConstantUtils.REPORT_SIZE_LIMIT, 90);
			config.put(ConstantUtils.STORE_DB, "true");
			config.put(ConstantUtils.LOGIN_USER_ID, "TestUser");
			String fileFormat = "xlsx";
			String fileName = "KR22102669" + "_" + "COO" + "." + fileFormat;
			Mockito.lenient().when(jasperReportService.getJasperReportDownloadOnline(expected,
						fileFormat, reportName, fileName, parameters, config)).thenReturn("/home/TMAP/DSD/shared/report/TPEXConfig/InvoiceGeneration/Report/KR22102669_COO.pdf");
			
				TpexConfigEntity tpexConfig = new TpexConfigEntity();
			tpexConfig.setId(33);
			tpexConfig.setName("reportTypesJsonForInquiryScreen");
			tpexConfig.setValue("/home/TMAP/DSD/shared/report/TPEXConfig/invoice/reportTypesOfInquiryScreen.json");
			Mockito.lenient().when(tpexConfigRepository.findByName(Mockito.anyString())).thenReturn(tpexConfig);
			Mockito.lenient().when(certificateOriginReportServiceImpl.getCertificateOriginReportDownload("TMT", "KR22102669",
					"NIITTMT", "RINS106", "xlsx")).thenReturn("/home/TMAP/DSD/shared/report/TPEXConfig/InvoiceGeneration/Report/KR22102669_COO.pdf");
			assertEquals("NIITTMT", parameters.get(ConstantUtils.USER_ID));
			assertEquals("KR22102669", parameters.get(ConstantUtils.INVOICE_NO));
	}
	@Test
	void getCertificateOriginDownloadReportOfflineTest() throws Exception{
		List<CertificateOriginReportDTO> expected = new ArrayList<>();
		CertificateOriginReportDTO dto = new CertificateOriginReportDTO();
		dto.setAddress1(address1);
		dto.setAddress2(address2);
		dto.setAddress3(address3);
		dto.setAddress4(address4);
		dto.setInvoiceNumber(invoiceNumber);
		dto.setPartNumber(partNumber);
		dto.setSumTotalUnit(sumTotalUnit);
		dto.setPartName(partName);
		dto.setConsigneeName(consigneeName);
		dto.setConsigneeAddress1(consigneeAddress1);
		dto.setConsigneeAddress2(consigneeAddress2);
		dto.setConsigneeAddress3(consigneeAddress3);
		dto.setConsigneeAddress4(consigneeAddress4);
		dto.setConsigneeTelephoneNumber(consigneeTelephoneNumber);
		dto.setCompanyCode(companyCode);
		dto.setNotifyName(notifyName);
		dto.setNotifyAddress1(notifyAddress1);
		dto.setNotifyAddress2(notifyAddress2);
		dto.setNotifyAddress3(notifyAddress3);
		dto.setNotifyAddress4(notifyAddress4);
		dto.setNotifyZip(notifyZip);
		dto.setNotifyCountry(notifyCountry);
		dto.setCompanyName(companyName);
		dto.setVesselName(vesselName);
		dto.setVoyageNumber(voyageNumber);
		dto.setDestinationName(destinationName);
		dto.setEtd(etd);
		dto.setInvoiceDate(invoiceDate);
		expected.add(dto);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(ConstantUtils.INVOICE_NO, "KR22102669");
		parameters.put(ConstantUtils.USER_ID, "NIITTMT");
		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY, "C:/TPEXConfig/InvoiceGeneration/Report");
		config.put(ConstantUtils.REPORT_FORMAT, "xlsx");
		config.put(ConstantUtils.REPORT_SIZE_LIMIT, 90);
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, "TestUser");
		String fileFormat = "pdf";
		String fileName = "KR22102669" + "_" + "COO" + "." + fileFormat;
		jasperReportService.getJasperReportDownloadOfflineV1(expected, fileFormat, reportName, parameters, config, 0, fileName);
			TpexConfigEntity tpexConfig = new TpexConfigEntity();
		tpexConfig.setId(33);
		tpexConfig.setName("reportTypesJsonForInquiryScreen");
		tpexConfig.setValue("/home/TMAP/DSD/shared/report/TPEXConfig/invoice/reportTypesOfInquiryScreen.json");
		Mockito.lenient().when(tpexConfigRepository.findByName(Mockito.anyString())).thenReturn(tpexConfig);
		certificateOriginReportServiceImpl.getCertificateOriginReportDownload("TMT", "KR22102669",
				"NIITTMT", "RINS106", "pdf");
		assertEquals("KR22102669", parameters.get(ConstantUtils.INVOICE_NO));
	}

	
			
	

}
