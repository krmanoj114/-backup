package com.tpex.invoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.tpex.invoice.dto.InvVinListResponseDTO;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvVinListRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

@ExtendWith(MockitoExtension.class)
public class InvVinListServiceImplTest {
	@InjectMocks
	InvVinListServiceImpl invVinListServiceImpl;

	@Mock
	JasperReportService jasperReportService;

	@Mock
	TpexConfigRepository tpexConfigRepository;

	@Mock
	InvVinListRepository invVinListRepository;

	@Mock
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	@Test
	void getVinListDataTest() throws Exception {

		String INM_INV_NO = "KR22106543";
		String INV_DT = "25/11/2022";
		String CMP_NAME = "Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.";
		String CMP_ADD_1 = "99 Moo5, Bangna-Trad K.M. 29.5 Rd.";
		String CMP_ADD_2 = "T.Ban-Ragad, A.Bang Bo,Samutprakarn,";
		String CMP_ADD_3 = "10560 Thailand.Tel: (66-2) 790-5000";
		String CMP_ADD_4 = "Tax ID : 0115546006888";
		String INM_LOT_NO = "RN0046";
		String IIV_INV_VIN_NO = "PN1BA3FS001265107";
		String userId = "NIITTMT";
		String tmapthInvFlg = "Y";
		String cmpCd = "TMT";
		String compCode = "TMAPTH_CMP";
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
				Object[] o = new Object[] { INM_INV_NO, INV_DT, CMP_NAME, CMP_ADD_1, CMP_ADD_2, CMP_ADD_3, CMP_ADD_4,
						INM_LOT_NO, IIV_INV_VIN_NO, userId, tmapthInvFlg, cmpCd };

				Object[] o1 = new Object[] { "Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.",
						"99 Moo5, Bangna-Trad K.M. 29.5 Rd.", "T.Ban-Ragad, A.Bang Bo,Samutprakarn,",
						"10560 Thailand.Tel: (66-2) 790-5000", "Tax ID : 0115546006888" };
				tuple.put(0, Arrays.asList(o));

				tuple.put(1, Arrays.asList(o1));

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

		List<InvVinListResponseDTO> expected = new ArrayList<>();
		InvVinListResponseDTO dto = new InvVinListResponseDTO();
		List<Tuple> listObj = new ArrayList<>();
		dto.setINV_NO("KR22106543");
		dto.setINV_DT("25/11/2022");
		dto.setNAME("Toyota Daihatsu Engineering & Manufacturing Co.,Ltd.");
		dto.setADD_1("99 Moo5, Bangna-Trad K.M. 29.5 Rd.");
		dto.setADD_2("T.Ban-Ragad, A.Bang Bo,Samutprakarn,");
		dto.setADD_3("10560 Thailand.Tel: (66-2) 790-5000");
		dto.setADD_4("Tax ID : 0115546006888");
		dto.setLOT_NO("RN0046");
		dto.setINV_VIN_NO("PN1BA3FS001265107");
		expected.add(dto);
		listObj.addAll((Collection<? extends Tuple>) mockedTuple.get(0));
		Mockito.lenient().when(invVinListRepository.getPartDetailData(INM_INV_NO)).thenReturn(listObj);
		Tuple InvCompDet = mockedTuple.get(1, Tuple.class);
		Mockito.lenient().when(invVinListRepository.getInvCompDetailWhenFlgY(cmpCd, compCode)).thenReturn(InvCompDet);
		Map<String, Object> parameters = new HashMap<>();

		parameters.put(ConstantUtils.INVOICE_NO, INM_INV_NO);
		parameters.put(ConstantUtils.USER_ID, userId);
		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY, "C:/TPEXConfig/InvoiceGeneration/Report");
		config.put(ConstantUtils.REPORT_FORMAT, "xlsx");
		config.put(ConstantUtils.REPORT_SIZE_LIMIT, 90);
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, "TestUser");
		String fileFormat = "pdf";
		String fileName = "KR22106543" + "_" + "VIN" + "." + fileFormat;
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/")
				.append(fileName);
		String reportName = "RINS005";
		jasperReportService.getJasperReportDownloadOffline(expected, fileFormat, reportName, parameters, config, 0, sb);
		assertEquals("Tax ID : 0115546006888", dto.getADD_4());
		assertEquals("NIITTMT", parameters.get(ConstantUtils.USER_ID));
		assertEquals("KR22106543", parameters.get(ConstantUtils.INVOICE_NO));
	}

}
