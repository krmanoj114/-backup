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

import com.tpex.entity.TpexConfigEntity;
import com.tpex.invoice.dto.DgInvoicePackingListResponseDto;
import com.tpex.invoice.dto.PackingListCustomBrokerDto;
import com.tpex.invoice.serviceImpl.PackingListCustomBrokerServiceImpl;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.PackingListCustomBrokerRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

@ExtendWith(MockitoExtension.class)
 class PackingListCustomBrokerServiceImplTest {
	@Mock
	JasperReportService jasperReportService;
	
	
	@Mock
	TpexConfigRepository tpexConfigRepository;
	
	@Mock
	PackingListCustomBrokerDto packingListCustomBrokerDto;
	
	@InjectMocks
	PackingListCustomBrokerServiceImpl packingListCustomBrokerServiceImpl;
	
	@Mock
	PackingListCustomBrokerRepository packingListCustomBrokerRepository;

	String invoiceNo = "KR22102702";
	
	@Test
	void generateReportTest() throws Exception {
		
		Map<String,Object> parameters=new HashMap<>();
		parameters.put("P_I_V_INVOICE_NO", invoiceNo);
		parameters.put("pidUserId","NIITTMT");
		String resp="home\\TMAP\\DSD\\shared\\report\\TPEXConfig\\InvoiceGeneration\\Report\\KR22102702_PAC_CB.pdf";
		Map<String, Object> config = new HashMap<>();
	    config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,"D:\\home\\TMAP\\DSD\\shared\\report\\TPEXConfig\\InvoiceGeneration\\Report");
		config.put(ConstantUtils.REPORT_FORMAT,"xlsx");
		config.put(ConstantUtils.STORE_DB, "false");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		String fileFormat = "pdf";
		String fileName = invoiceNo + "_"+ "PAC_CB" + "." + fileFormat;
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
				.append("/").append(fileName);
		String templateId=ConstantUtils.RINS002A;
        List<PackingListCustomBrokerDto> response = new ArrayList<>();
		
		PackingListCustomBrokerDto dto=setData();
		response.add(dto);
		List<Object[]> listObj = new ArrayList<>();
		
		Object[] objs = new Object[] { "Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.","99 Moo5, Bangna-Trad K.M. 29.5 Rd.",
				                     "T.Ban-Ragad, A.Bang Bo,Samutprakarn,","10560 Thailand.Tel: (66-2) 790-5000","Tax ID : 0115546006888",
				invoiceNo,"12-05-2022","757610206000","MOULDING, RR DOOR WINDOW FRAME, FR RH",
				                     20.0,null,null,20.0,0.085,282.938,2.447,null,"EA0549","EA","EA0549",
				                     "797W","COROLLA",1.0};
		listObj.add(objs);
		
		packingListCustomBrokerRepository.getPINS002AData(invoiceNo);
		
		Mockito.lenient().when(packingListCustomBrokerRepository.getINSPACKLISTTEMPData()).thenReturn(listObj);
		jasperReportService.getJasperReportDownloadOffline(response, fileFormat, templateId, parameters, config, 0, sb);
		TpexConfigEntity tpexConfig=new TpexConfigEntity();
		Mockito.lenient().when(tpexConfigRepository.findByName(Mockito.anyString())).thenReturn(tpexConfig);
		 try {
				Mockito.lenient().when(packingListCustomBrokerServiceImpl.generateReport(invoiceNo,"RINS002A","NIITTMT","pdf")).thenReturn(resp);
			} catch (Exception e) {
			 //  e.printStackTrace();
			}
	}

	 PackingListCustomBrokerDto setData() {
		
		 PackingListCustomBrokerDto dto=new PackingListCustomBrokerDto();
		    dto.setInsCnsgAdd1("99 Moo5, Bangna-Trad K.M. 29.5 Rd.");
		    dto.setInsCnsgName("Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.");
		 dto.setInsCnsgAdd2("T.Ban-Ragad, A.Bang Bo,Samutprakarn,");
		 dto.setInsCnsgAdd3("10560 Thailand.Tel: (66-2) 790-5000");
		 dto.setInsCnsgAdd4("Tax ID : 0115546006888");
		 dto.setInsInvNo(invoiceNo);
		 dto.setInsInvDt("12-05-2022");
		 dto.setInsPartNo("757610206000");
		 dto.setInsPartName("MOULDING, RR DOOR WINDOW FRAME, FR RH");
		 dto.setInsSumTotUnit(20.0);
		 dto.setInsIcoFlag(null);
		 dto.setInsPartPrice(null);
		 dto.setInsUnitPerBox(20.0);
		 dto.setInsPartWt(0.085);
		 dto.setInsGrossWt(282.938);
		 dto.setInsMeasurement(2.447);
		 dto.setInsShipmark4(null);
		 dto.setInsShipmark5("EA0549");
		 dto.setShipMarkGp("EA");
		 dto.setCaseMod("EA0549");
		 dto.setInsCfCd("797W");
		 dto.setInsSrsName("COROLLA");
		 dto.setInsNoOfCases(1.0);
	       

		return dto;
	}
	

}
