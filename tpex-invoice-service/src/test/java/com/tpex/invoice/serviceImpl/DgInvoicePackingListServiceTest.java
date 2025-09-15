package com.tpex.invoice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.DgInvoicePackingListResponseDto;
import com.tpex.invoice.dto.DgTwoInvoicePackingListResponseDto;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.DgDeclarationReportResponseRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class DgInvoicePackingListServiceTest {

	@InjectMocks
	DgInvoicePackingListService dgInvoicePackingListService;
	
	

	@Mock
	DgDeclarationReportResponseRepository dgDeclarationReportResponseRepository;
	
	@Mock
	JasperReportService jasperReportService;
	
	@Mock
	TpexConfigRepository tpexConfigRepository;
	
	@Mock
	OemFnlDstMstRepository oemFnlDstMstRepository;

	@Test
	void downloadDgInvoicePackingListReportTest() throws Exception {
		String etd = "01/06/2022";
		String eta = "15/06/2022";
		String bookingNo = "BKKCG5673700";
		String contryCd = "900A";
		List<Object[]> listObj = new ArrayList<>();
		Object[] objs = new Object[] { 1, "A36925", "4.735", "6.105", "0.061", "62180-0K080",
				"AIR BAG ASSY, CURTAIN SHIELD, LH ", 5, 5, "0.94700", "4.735", "6.105", "0.061", "KR22100530,KR22102686", "0.061",
				"0.061", "0.061", "BKKCG5673700", "900A" };
		listObj.add(objs);
		Mockito.lenient().when(dgDeclarationReportResponseRepository.getRins104Data(etd, eta, bookingNo, contryCd)).thenReturn(listObj);
	
		List<DgInvoicePackingListResponseDto> response = new ArrayList<>();
		DgInvoicePackingListResponseDto dto=setData();
		response.add(dto);

		String etdDate = DateUtil.getStringDate(etd);
		String fileName = "DG_declare" + "_" + contryCd + "_" + etdDate;	
        Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,"C:/TPEXConfig/InvoiceGeneration/Report");
		config.put(ConstantUtils.REPORT_FORMAT,"xlsx");
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,90);
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
				.append("/").append(fileName);
		String fileFormat = "pdf";
	    String templateId=ConstantUtils.RINS104_DG;
	    Map<String, Object> parameters = new HashMap<>();
		parameters.put(ConstantUtils.ETD_FROM, etd);
		parameters.put(ConstantUtils.ETD_TO, eta);
		parameters.put(ConstantUtils.BOOKING_NO, bookingNo);
		
		jasperReportService.getJasperReportDownloadOffline(response, fileFormat, templateId, parameters, config, 0, sb);
		assertEquals("01/06/2022", parameters.get(ConstantUtils.ETD_FROM));
	}
	DgInvoicePackingListResponseDto setData() {
		DgInvoicePackingListResponseDto dto = new DgInvoicePackingListResponseDto();
		dto.setINS014_RENBAN("123");
		dto.setINS014_TOT_CASES_RB("1");
		dto.setINS014_CASE_NO("A36925");
		dto.setINS014_TOT_NW_CASE(Double.valueOf("4.735"));
		dto.setINS014_TOT_GW_CASE(Double.valueOf("6.105"));
		dto.setINS014_TOT_M3_CASE(Double.valueOf("0.061"));
		dto.setINS014_PART_NM("AIR BAG ASSY, CURTAIN SHIELD");
		dto.setINS014_PART_NO("5");
		dto.setINS014_QTY_BOX("5");
		dto.setINS014_TOT_QTY_PCS("0.94700");
		dto.setINS014_NET_WT_PC("4.735");
		dto.setINS014_TOT_NET_WT_PC("6.105");
		dto.setINS014_BOX_GROSS_WT_PC("0.061");
		dto.setINS014_BOX_M3("KR22100530,KR22102686");
		dto.setINS014_INV_LIST("0.061");
		dto.setINS014_TOT_NW_RB("0.061");
		dto.setINS014_TOT_GW_RB("0.061");
		dto.setINS014_TOT_M3_RB("1234");
		dto.setINS014_BOOKING_NO("BKKCG5673700");
		dto.setINS014_CTRY_CD("900A");
		
		return dto;
	}
	// tpex-187 test case
	@SuppressWarnings("unused")
	@Test
	void download2DgInvoicePackingListReportTest() throws Exception {
		String etd = "01/06/2022";
		String bookingNo = "BKKCG5673700";
		String contryCd = "900A";
		String orderType="R";
		String invoiceNo="KR22102686";
		String userId="TestUser";
		String cmpCd=null;
		List<Object[]> listObj = new ArrayList<>();
		Object[] objs = new Object[] { "Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.", "test", "test", "test", "test", "KS22100248",
				"06/05/2022", "473520A05000", null, 7,null,null,"BRACKET, FLEXIBLE HOSE, LH", "0.08700",null, "0.000", "GZS026",null, "899W", "COROLLA",
				1,"GZS004",5,5, };
		listObj.add(objs);
		if(invoiceNo!=null) {
			Mockito.lenient().doNothing().when(dgDeclarationReportResponseRepository).getInvoiceTwoDgData(invoiceNo,orderType,userId,cmpCd);
		}else if(etd!=null && contryCd!=null) {
			Mockito.lenient().doNothing().when(dgDeclarationReportResponseRepository).getInvoiceTwoDgData(orderType,userId,etd,contryCd,cmpCd);
		}
		Mockito.lenient().when(dgDeclarationReportResponseRepository.getDataFromTemp1(contryCd)).thenReturn(listObj);
		
		List<DgTwoInvoicePackingListResponseDto> response = new ArrayList<>();
		DgTwoInvoicePackingListResponseDto dto=setData1();
		response.add(dto);
		
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String sDate1 = "14/11/2004";
		java.util.Date date = format.parse(sDate1);
		
		String etdDate = DateUtil.getStringDate(etd);
		String importerName="JAPAN";
		String fileName="DG" + "_" + contryCd + "_" + importerName + "_" + etdDate;
        Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,"C:/TPEXConfig/InvoiceGeneration/Report");
		config.put(ConstantUtils.REPORT_FORMAT,"xlsx");
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,90);
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
				.append("/").append(fileName);
		String fileFormat = "pdf";
	    String templateId=ConstantUtils.RINS104_DG;
	    Map<String, Object> parameters = new HashMap<>();
		parameters.put(ConstantUtils.ETD_FROM, etd);
		parameters.put(ConstantUtils.BOOKING_NO, bookingNo);
		
		jasperReportService.getJasperReportDownloadOffline(response, fileFormat, templateId, parameters, config, 0, sb);
		assertEquals("01/06/2022", parameters.get(ConstantUtils.ETD_FROM));
	}
	DgTwoInvoicePackingListResponseDto setData1() {
		DgTwoInvoicePackingListResponseDto dto = new DgTwoInvoicePackingListResponseDto();

		dto.setINS_CNSG_ADD1("123");
		dto.setINS_CNSG_ADD2("1");
		dto.setINS_CNSG_ADD3("A36925");
		dto.setINS_CNSG_ADD4("4.735");
		dto.setINS_INV_NO("6.105");
		dto.setINS_INV_DT("0.061");
		dto.setINS_PART_NO("AIR BAG ASSY, CURTAIN SHIELD");
		dto.setINS_UNIT_PER_BOX(Integer .valueOf(12));
		dto.setINS_SUM_TOT_UNIT(Integer.valueOf(4));
		dto.setINS_ICO_FLG("0.94700");
		dto.setINS_PART_PRICE(Double .valueOf("4.735"));
		dto.setINS_PART_NAME("6.105");
		dto.setINS_PART_WT(Double .valueOf("0.061"));
		dto.setINS_GROSS_WT(Double.valueOf("0.123"));
		dto.setINS_MEASUREMENT(Double.valueOf("0.061"));
		dto.setINS_SHIPMARK_4("0.061");
		dto.setINS_SHIPMARK_5("0.061");
		dto.setSHIP_MARK_GP("1234");
		dto.setCASE_MOD("BKKCG5673700");
		dto.setINS_CF_CD("900A");
		dto.setINS_SRS_NAME("900A");
		dto.setINS_NO_OF_CASES(Integer .valueOf("12"));
		dto.setINS_NO_OF_BOXES(Integer.valueOf("13"));
		dto.setINS_CONT_SNO("900A");
		dto.setINS_ISO_CONT_NO("900A");
		dto.setINS_TPT_CD("900A");
		
		return dto;
	}

}
