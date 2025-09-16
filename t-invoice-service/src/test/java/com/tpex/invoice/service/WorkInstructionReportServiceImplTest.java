package com.tpex.invoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.invoice.dto.WorkInstrReportDTO;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InsInvContainerDtlsRepository;
import com.tpex.repository.OemParameterRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;



@ExtendWith(MockitoExtension.class)
class WorkInstructionReportServiceImplTest {

	@InjectMocks
	WorkInstructionReportServiceImpl workInstructionReportService;

	@Mock
	InsInvContainerDtlsRepository insInvContainerDtlsRepository;

	@Mock
	JasperReportService jasperReportService;

	@Mock
	TpexConfigRepository tpexConfigRepository;

	@Mock
	OemParameterRepository oemParameterRepository;


	@SuppressWarnings("unchecked")
	@Test
	void getRegularWrkInstructionRptDownload() throws Exception {

		String etd = "01/06/2022";
		String bookingNo = "BKKCG9037500";
		String contryCd = "537B";
		String haisenNo = "TB084";
		String haisenYrMnth = "202302";

		List<Tuple> listObj = new ArrayList<>();

		Tuple mockedTuple = new Tuple() {

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

				Object[] o = new Object[] {"BKKCG9037500","TB084","202302"};
				Object[] o1 = new Object[] {"TSM","537B","Gateway(K)","May'22","01/Jun/22","12/Jul/22","11/May/22","ONE","TBA","600"};
				Object[] o2 = new Object[] {"3","0"};
				Object[] o3 = new Object[] {"3","0"};
				Object[] o4 = new Object[] {"KR22102701","140","353193.79","CSUV","202205","NON"};
				Object[] o5 = new Object[] {"KR22102702","1","4528.87","COROLLA","202205","NON"};

				tuple.put(0, Arrays.asList(o));
				tuple.put(1, Arrays.asList(o1));
				tuple.put(2, Arrays.asList(o2));
				tuple.put(3, Arrays.asList(o3));
				tuple.put(4, Arrays.asList(o4));
				tuple.put(5, Arrays.asList(o5));

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

		listObj.addAll((Collection<? extends Tuple>) mockedTuple.get(0));

		Mockito.lenient().when(insInvContainerDtlsRepository.getListOfBookingNum(etd,bookingNo,contryCd)).thenReturn(listObj);

		Tuple bookingDtls =  mockedTuple.get(1,Tuple.class);

		Mockito.lenient().when(insInvContainerDtlsRepository.getDataForBookingNum(bookingNo,haisenNo,haisenYrMnth)).thenReturn(bookingDtls);

		Integer invoiceCount = 2;

		Mockito.lenient().when(insInvContainerDtlsRepository.getInvoiceDataCount(bookingNo,haisenNo,haisenYrMnth)).thenReturn(invoiceCount);

		Integer rRackCount = 0;

		Mockito.lenient().when(insInvContainerDtlsRepository.getRRackDataCount(bookingNo,haisenNo,haisenYrMnth)).thenReturn(rRackCount);

		String parameter = "TMT";

		Mockito.lenient().when(oemParameterRepository.getParameter()).thenReturn(parameter);

		List rBoxDataList = null;

		Mockito.lenient().when(insInvContainerDtlsRepository.getRBoxDataCount(bookingNo,haisenNo,haisenYrMnth,parameter)).thenReturn(rBoxDataList);

		Tuple nonDGCount = mockedTuple.get(2,Tuple.class);

		Mockito.lenient().when(insInvContainerDtlsRepository.getNonDgCount(bookingNo,haisenNo,haisenYrMnth)).thenReturn(nonDGCount);

		Tuple dGCount = mockedTuple.get(3,Tuple.class);

		Mockito.lenient().when(insInvContainerDtlsRepository.getDgCount(bookingNo,haisenNo,haisenYrMnth)).thenReturn(dGCount);

		Object model = "COROLLA/CSUV";

		Mockito.lenient().when(insInvContainerDtlsRepository.getDataForSeries(bookingNo,haisenNo,haisenYrMnth)).thenReturn(model);

		List<Tuple> invoiceData = new ArrayList<>();
		invoiceData.addAll((Collection<? extends Tuple>) mockedTuple.get(4));
		invoiceData.addAll((Collection<? extends Tuple>) mockedTuple.get(5));

		Mockito.lenient().when(insInvContainerDtlsRepository.getInvoiceData(etd,bookingNo,contryCd)).thenReturn(invoiceData);

		List<WorkInstrReportDTO> resultlist = setData();


		Map<String, Object> parameters = new HashMap<>();
		parameters.put(ConstantUtils.ETD_FROM, etd);
		parameters.put(ConstantUtils.BOOKING_NO, bookingNo);
		parameters.put(ConstantUtils.COUNTRY_CODE, contryCd);
		parameters.put(ConstantUtils.USER_ID, ConstantUtils.TEST_USER);

		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.FONT_SIZE_FIX_ENABLED, true);
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,"C:/TPEXConfig/InvoiceGeneration/Report");
		config.put(ConstantUtils.REPORT_FORMAT,"xlsx");
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,90);
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);

		String etdDate = DateUtil.getStringDate(etd);
		String fileName = "WorkInstruction"+"_"+contryCd+"_"+"ETD"+etdDate.toUpperCase()+"_"+bookingNo;	
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
				.append("/").append(fileName);
		String fileFormat = "xlsx";
		String reportName =ConstantUtils.RINS105;

		jasperReportService.getJasperReportDownloadOffline(resultlist, fileFormat, reportName, parameters, config, 0, sb);
		assertEquals("01/06/2022", parameters.get(ConstantUtils.ETD_FROM));
		assertEquals("BKKCG9037500",parameters.get(ConstantUtils.BOOKING_NO));
	}


	List<WorkInstrReportDTO> setData() {

		List<WorkInstrReportDTO> list = new ArrayList<>();

		WorkInstrReportDTO dto1 = new WorkInstrReportDTO();
		dto1.setBookingNumber("BKKCG9037500");
		dto1.setHaisenNumber("TB084");
		dto1.setFinalDestination("537B");
		dto1.setInmPackMonth("MAY'22");
		dto1.setEtd("01/JUN/22");
		dto1.setEta("12/JUL/22");
		dto1.setVanningDate("11/MAY/22");
		dto1.setShippingCompany("ONE");
		dto1.setVesselNameOcean("TBA");
		dto1.setVoyageNumberOcean("600");
		dto1.setVanPlantCode("Gateway(K)");
		dto1.setCount(2);
		dto1.setInvoiceNumber("KR22102701");
		dto1.setModNumber(140);
		dto1.setInvoiceAmount("353193.79");
		dto1.setAicoName("CSUV");
		dto1.setSeries("NON");
		dto1.setInpPackMonth("CSUV");
		dto1.setRackCount("0");
		dto1.setNonDg20("0");
		dto1.setNonDg40("3");
		dto1.setDg20("0");
		dto1.setDg40("3");
		dto1.setIndBuyer("TSM");
		dto1.setModel("COROLLA/CSUV");
		dto1.setRboxdataCnt(null);

		WorkInstrReportDTO dto2 = new WorkInstrReportDTO();
		dto2.setBookingNumber("BKKCG9037500");
		dto2.setHaisenNumber("TB084");
		dto2.setFinalDestination("537B");
		dto2.setInmPackMonth("MAY'22");
		dto2.setEtd("01/JUN/22");
		dto2.setEta("12/JUL/22");
		dto2.setVanningDate("11/MAY/22");
		dto2.setShippingCompany("ONE");
		dto2.setVesselNameOcean("TBA");
		dto2.setVoyageNumberOcean("600");
		dto2.setVanPlantCode("Gateway(K)");
		dto2.setCount(2);
		dto2.setInvoiceNumber("KR22102702");
		dto2.setModNumber(1);
		dto2.setInvoiceAmount("4528.87");
		dto2.setAicoName("COROLLA");
		dto2.setSeries("NON");
		dto2.setInpPackMonth("CSUV");
		dto2.setRackCount("0");
		dto2.setNonDg20("0");
		dto2.setNonDg40("3");
		dto2.setDg20("0");
		dto2.setDg40("3");
		dto2.setIndBuyer("TSM");
		dto2.setModel("COROLLA/CSUV");
		dto2.setRboxdataCnt(null);

		list.add(dto1);
		list.add(dto2);

		return list;
	}

}
