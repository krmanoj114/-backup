package com.tpex.invoice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.invoice.dto.invAttachedSheetResponseDto;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InsInvPartsDetailsRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

@ExtendWith(MockitoExtension.class)
public class InvoiceAttachedSheetServiceImplTest {


	@Mock
	InsInvPartsDetailsRepository insInvPartsDetailsRepository;
	
	@Mock
	JasperReportService jasperReportService;
	
	@Mock
	TpexConfigRepository tpexConfigRepository;
	
		
	@Test
	void testdownloadPINS103() throws Exception {
	
	final Double insUnitPerBo = 0d ;
	final Double insPartW = 0d;
	final Double insGrossW = 0d;
	final Double insMeasurem = 0d;
	String insShipma4 = "";
	String insShipma5 = "";
	String invoiceNo = "KR22102669";
	String userId = "TestUser";
	
	String tmapthInvFlg = "Y";
	String scRemarks= "null";

	String cmpCd = "TMT";

	String INS_INV_DT = "09/12/2022";
	String INS_PART_NO= "882100D05200";		
	Double INS_SUM_TOT_UNIT = 0d;
	String INS_ICO_FLG ="360" ;
	Double INS_PART_PRICE = 0d;
	String INS_PART_NAME = "1";		
	String INS_CF_CD = "848W";
	String INS_SRS_NAME ="VIOS";
	String INS_CURR_CD ="USD";
	String INS_ICO_DESC= "NON";
	String INS_CO_CD ="CN";
	String ORG_CRITERIA= "null";
	String SC_REMARK = "null";
	String SC_AUTH_EMP ="null";
	Double INS_SORT_SEQ = 1D ;
	String INS_HS_CD ="null";
	
	List<Tuple> listObj = new ArrayList<>();
	
	Tuple mockedTuple = new Tuple(){

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<TupleElement<?>> getElements() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X> X get(int i, Class<X> type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X> X get(String alias, Class<X> type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object get(int i) {
			Map<Integer, List<Object>> tuple = new HashMap<>();

			Object[] o = new Object[] {tmapthInvFlg, scRemarks};
			Object[] o1 = new Object[] {INS_INV_DT,INS_PART_NO,INS_SUM_TOT_UNIT,INS_ICO_FLG,INS_PART_PRICE,INS_PART_NAME,INS_CF_CD,INS_SRS_NAME,INS_CURR_CD,INS_ICO_DESC,INS_CO_CD,ORG_CRITERIA,SC_REMARK,SC_AUTH_EMP,INS_SORT_SEQ,INS_HS_CD};
			Object[] o2 = new Object[] {"PT.TOYOTA MOTOR MANUFACTURING INDONESIA","99 Moo5, Bangna-Trad K.M. 29.5 Rd.","T.Ban-Ragad, A.Bang Bo,Samutprakarn,","10560 Thailand.Tel: (66-2) 790-5000","Tax ID : 0115546006888"};
			

			tuple.put(0, Arrays.asList(o));
			tuple.put(1, Arrays.asList(o1));
			tuple.put(2, Arrays.asList(o2));
			
			return tuple.get(i);
		}

		@Override
		public Object get(String alias) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X> X get(TupleElement<X> tupleElement) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	
	invAttachedSheetResponseDto ob = new invAttachedSheetResponseDto();
	ob.setINS_CNSG_NAME("Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.");
	ob.setINS_CNSG_ADD1("99 Moo5, Bangna-Trad K.M. 29.5 Rd.");
	ob.setINS_CNSG_ADD2("T.Ban-Ragad, A.Bang Bo,Samutprakarn,");
	ob.setINS_CNSG_ADD3("10560 Thailand.Tel: (66-2) 790-5000");
	ob.setINS_CNSG_ADD4("Tax ID : 0115546006888");
	ob.setINS_INV_NO("KR22102669");
	ob.setINS_INV_DT("09/12/2022");
	ob.setINS_PART_NO("882100D05200");
	ob.setINS_UNIT_PER_BOX(insUnitPerBo);
	ob.setINS_SUM_TOT_UNIT(null);
	ob.setINS_ICO_FLG("360");
	ob.setINS_PART_PRICE(null);
	ob.setINS_PART_NAME("1");
	ob.setINS_PART_WT(insPartW);
	ob.setINS_GROSS_WT(insGrossW);
	ob.setINS_MEASUREMENT(insMeasurem);
	ob.setINS_SHIPMARK_4(insShipma4);
	ob.setINS_SHIPMARK_5(insShipma5);
	ob.setINS_CF_CD("848W");
	ob.setINS_SRS_NAME("VIOS");
	ob.setINS_CURR_CD("USD");
	ob.setINS_ICO_DESC("NON");
	ob.setINS_CO_CD("CN");
	ob.setORG_CRITERIA("null");
	ob.setSC_REMARK("null");
	ob.setSC_AUTH_EMP("null");
	ob.setINS_SORT_SEQ(1D);
	ob.setINS_HS_CD("null");
	
	List resultList = new ArrayList<>();
	resultList.add(ob);
	Tuple tmaptTuple =  mockedTuple.get(0,Tuple.class);
	
	Mockito.lenient().when(insInvPartsDetailsRepository.getScRemarks(invoiceNo)).thenReturn(tmaptTuple);
	
	listObj.addAll((Collection<? extends Tuple>) mockedTuple.get(1));
	
	Mockito.lenient().when(insInvPartsDetailsRepository.getQuerydataforpins003(invoiceNo)).thenReturn(listObj);
	
	Tuple InvCompDet =  mockedTuple.get(2,Tuple.class);	
	Mockito.lenient().when(insInvPartsDetailsRepository.getInvCompDetailsWhenFlgY(cmpCd,invoiceNo)).thenReturn(InvCompDet);
	
	
	
	Map<String, Object> config = new HashMap<>();
	
	config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
	config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
	config.put(ConstantUtils.REPORT_DIRECTORY,"C:/TPEXConfig/InvoiceGeneration/Report");
	config.put(ConstantUtils.REPORT_FORMAT,"xlsx");
	config.put(ConstantUtils.REPORT_SIZE_LIMIT,90);
	config.put(ConstantUtils.STORE_DB, "true");
	config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
	
	
	Map<String, Object> parameters = new HashMap<>();
	parameters.put(ConstantUtils.TEST_USER, userId);
	parameters.put(ConstantUtils.INVOICE_NO, invoiceNo);
	
	String fileName = "RINS003B";
	StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
			.append("/").append(fileName);
	String fileFormat = "pdf";
	String reportName =ConstantUtils.RINS003B;

	jasperReportService.getJasperReportDownloadOffline(resultList, fileFormat, reportName, parameters, config, 0, sb);
	
	

	assertEquals("TestUser", parameters.get(ConstantUtils.TEST_USER));
	assertEquals("KR22102669",parameters.get(ConstantUtils.INVOICE_NO));
	
	}
}
