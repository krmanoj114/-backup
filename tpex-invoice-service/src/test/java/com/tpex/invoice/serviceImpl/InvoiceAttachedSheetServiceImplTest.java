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

import com.tpex.invoice.dto.InvAttachedSheetResponseDto;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InsInvPartsDetailsRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

@ExtendWith(MockitoExtension.class)
class InvoiceAttachedSheetServiceImplTest {

	@Mock
	InsInvPartsDetailsRepository insInvPartsDetailsRepository;

	@Mock
	JasperReportService jasperReportService;

	@Mock
	TpexConfigRepository tpexConfigRepository;

	@Test
	void testdownloadPINS103() throws Exception {

		final Double insUnitPerBo = 0d;
		final Double insPartW = 0d;
		final Double insGrossW = 0d;
		final Double insMeasurem = 0d;
		String insShipma4 = "";
		String insShipma5 = "";
		String invoiceNo = "KR22102669";
		String userId = "TestUser";

		String tmapthInvFlg = "Y";
		String scRemarks = "null";

		String cmpCd = "TMT";

		String INS_INV_DT = "09/12/2022";
		String INS_PART_NO = "882100D05200";
		Double INS_SUM_TOT_UNIT = 0d;
		String INS_ICO_FLG = "360";
		Double INS_PART_PRICE = 0d;
		String INS_PART_NAME = "1";
		String INS_CF_CD = "848W";
		String INS_SRS_NAME = "VIOS";
		String INS_CURR_CD = "USD";
		String INS_ICO_DESC = "NON";
		String INS_CO_CD = "CN";
		String ORG_CRITERIA = "null";
		String SC_REMARK = "null";
		String SC_AUTH_EMP = "null";
		Double INS_SORT_SEQ = 1D;
		String INS_HS_CD = "null";

		List<Tuple> listObj = new ArrayList<>();

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

				Object[] o = new Object[] { tmapthInvFlg, scRemarks };
				Object[] o1 = new Object[] { INS_INV_DT, INS_PART_NO, INS_SUM_TOT_UNIT, INS_ICO_FLG, INS_PART_PRICE,
						INS_PART_NAME, INS_CF_CD, INS_SRS_NAME, INS_CURR_CD, INS_ICO_DESC, INS_CO_CD, ORG_CRITERIA,
						SC_REMARK, SC_AUTH_EMP, INS_SORT_SEQ, INS_HS_CD };
				Object[] o2 = new Object[] { "PT.TOYOTA MOTOR MANUFACTURING INDONESIA",
						"99 Moo5, Bangna-Trad K.M. 29.5 Rd.", "T.Ban-Ragad, A.Bang Bo,Samutprakarn,",
						"10560 Thailand.Tel: (66-2) 790-5000", "Tax ID : 0115546006888" };

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

		InvAttachedSheetResponseDto ob = new InvAttachedSheetResponseDto();
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
		ob.setOrgCriteria("null");
		ob.setScRemark("null");
		ob.setScAuthEmp("null");
		ob.setInsSortSeq(1D);
		ob.setInsHsCd("null");

		List resultList = new ArrayList<>();
		resultList.add(ob);
		Tuple tmaptTuple = mockedTuple.get(0, Tuple.class);

		Mockito.lenient().when(insInvPartsDetailsRepository.getScRemarks(invoiceNo)).thenReturn(tmaptTuple);

		listObj.addAll((Collection<? extends Tuple>) mockedTuple.get(1));

		Mockito.lenient().when(insInvPartsDetailsRepository.getQuerydataforpins003(invoiceNo)).thenReturn(listObj);

		Tuple InvCompDet = mockedTuple.get(2, Tuple.class);
		Mockito.lenient().when(insInvPartsDetailsRepository.getInvCompDetailsWhenFlgY(cmpCd, invoiceNo))
				.thenReturn(InvCompDet);

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

		String fileName = "RINS003B";
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
				.append("/").append(fileName);
		String fileFormat = "pdf";
		String reportName = ConstantUtils.RINS003B;

		jasperReportService.getJasperReportDownloadOffline(resultList, fileFormat, reportName, parameters, config, 0,
				sb);

		assertEquals("TestUser", parameters.get(ConstantUtils.TEST_USER));
		assertEquals("KR22102669", parameters.get(ConstantUtils.INVOICE_NO));

	}
}
