package com.tpex.invoice.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.invoice.dto.InsModuleDtlsDto;
import com.tpex.invoice.dto.InvPackingListResponseDTO;
import com.tpex.invoice.dto.PartDetailDataDTO;
import com.tpex.invoice.service.InvPackingListService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvPackingListRepository;
import com.tpex.repository.TpexConfigRepository;

@Service
@Transactional
public class InvPackingListServiceImpl implements InvPackingListService {

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Autowired
	InvPackingListRepository invPackingListRepository;

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Object getInvPackingListRptDownload(String invNumber, String fileTemplateName,
			String reportFormat) throws Exception {

		Object response = null;
		Map<String, Object> parameters = new HashMap<>();

		parameters.put("P_I_V_INVOICE_NO", invNumber);

		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("storeInDB", "true");
		config.put("loginUserId", "TestUser");
		String fileFormat = StringUtils.isNotBlank(reportFormat)
				&& "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat : "pdf";
		String fileName = invNumber + "_PAC." + fileFormat;

		List<InvPackingListResponseDTO> invPackingListResponseDTOList = getPackingListData(invNumber);

		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/")
				.append(fileName);

		if ("xlsx".equals(fileFormat)) {
			response = jasperReportService.getJasperReportDownloadOnline(invPackingListResponseDTOList, fileFormat, fileTemplateName, fileName, parameters, config);
		
		}else {
			response = jasperReportService.getJasperReportDownloadOfflineV1(invPackingListResponseDTOList, fileFormat, fileTemplateName, parameters, config, 0, sb, fileName);

		}
		return response;
	}
	
	private List<InvPackingListResponseDTO> getPackingListData(String invNumber) throws Exception {

		List<InvPackingListResponseDTO> invPackingListResponseDTOList = new ArrayList<>();
		List<PartDetailDataDTO> partDetailDataDTOList = null;
		Tuple invCompDetail = null;
		String userMsg = "Fetch data from Parameter Master";

		String companyCode = invPackingListRepository.getCompanyCode(); // TODO : input pram
		if (companyCode == null) {
			throw new Exception("");
		}

		// String compCode = "TMAPTH_CMPO";
		String compCode = invPackingListRepository.getCompCode(invNumber);

		userMsg = "Fetch data from Consignee Mst";
		String tmapthInvFlg = invPackingListRepository.getTmapthInvFlg(invNumber);
		if ("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgY(companyCode, compCode);
		} else {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgN(companyCode);
		}

		String invCompDetail1 = invCompDetail.get(0, String.class);
		String invCompDetail2 = invCompDetail.get(1, String.class);
		String invCompDetail3 = invCompDetail.get(2, String.class);
		String invCompDetail4 = invCompDetail.get(3, String.class);
		String invCompDetail5 = invCompDetail.get(4, String.class);

		List<Tuple> partDetailData = invPackingListRepository.getPartDetailData(invNumber);

		partDetailDataDTOList = partDetailData.stream()
				.map(t -> new PartDetailDataDTO(t.get(0, String.class), t.get(1, String.class),
						t.get(2, BigDecimal.class), t.get(3, BigDecimal.class), t.get(4, String.class),
						t.get(5, String.class), t.get(6, String.class), t.get(7, String.class), t.get(8, String.class),
						t.get(9, String.class), t.get(10, BigDecimal.class))

				).collect(Collectors.toList());

		String l_n_Prev_Mod = null;
		String l_v_Prev_CF_CD = null;

		List<Tuple> list1 = invPackingListRepository.getData(invNumber);
		List<InsModuleDtlsDto> insModuleDtlsDtoList = null;

		insModuleDtlsDtoList = list1.stream()
				.map(t -> new InsModuleDtlsDto(String.valueOf(t.get(0, String.class)), t.get(1, BigDecimal.class),
						t.get(2, BigDecimal.class), new BigDecimal(t.get(3, BigInteger.class).toString())))
				.collect(Collectors.toList());

		Map<String, InsModuleDtlsDto> map = new HashMap<>();
		for (InsModuleDtlsDto mapObj : insModuleDtlsDtoList) {
			map.put(mapObj.getCfCd_modNo(), mapObj);
		}

		BigDecimal grossWeight = new BigDecimal("0");
		BigDecimal measurement = new BigDecimal("0");
		BigDecimal noOfModule = new BigDecimal("0");
		int l_n_Part_Wt = 0;
		int l_n_Ctr = 0;

		for (PartDetailDataDTO partDetailDataDTO : partDetailDataDTOList) {
			if (partDetailDataDTO != null) {

				l_n_Part_Wt = 0;

				if (l_n_Ctr == 0) {
					l_n_Prev_Mod = partDetailDataDTO.getSHIP_MARK();
					l_v_Prev_CF_CD = partDetailDataDTO.getCF_CD();
					String key = partDetailDataDTO.getCF_CD() + "-" + partDetailDataDTO.getSHIP_MARK();
					grossWeight = map.get(key) != null && map.get(key).getGrossWt() != null ? map.get(key).getGrossWt()	: new BigDecimal("0");
					measurement = map.get(key) != null && map.get(key).getTotalM3() != null ? map.get(key).getTotalM3()	: new BigDecimal("0");
					noOfModule = map.get(key) != null && map.get(key).getCCount() != null ? map.get(key).getCCount() : new BigDecimal("0");
				}

				if ((l_n_Prev_Mod != null && !l_n_Prev_Mod.equals(partDetailDataDTO.getSHIP_MARK()))
						|| (l_v_Prev_CF_CD != null && !l_v_Prev_CF_CD.equals(partDetailDataDTO.getCF_CD()))) {

					String l_v_user_msg = partDetailDataDTO.getSHIP_MARK();
					grossWeight = new BigDecimal("0");
					measurement = new BigDecimal("0");
					noOfModule = new BigDecimal("0");

					String key = partDetailDataDTO.getCF_CD() + "-" + partDetailDataDTO.getSHIP_MARK();
					grossWeight = map.get(key) != null && map.get(key).getGrossWt() != null ? map.get(key).getGrossWt()	: new BigDecimal("0");
					measurement = map.get(key) != null && map.get(key).getTotalM3() != null ? map.get(key).getTotalM3()	: new BigDecimal("0");
					noOfModule = map.get(key) != null && map.get(key).getCCount() != null ? map.get(key).getCCount() : new BigDecimal("0");
				}
				if (partDetailDataDTO.getPART_WT() == null) {
					noOfModule = new BigDecimal("0");
					// l_n_Part_Wt := 0;
				}

				InvPackingListResponseDTO invPackingListResponseDTO = new InvPackingListResponseDTO();

				invPackingListResponseDTO.setINS_CNSG_NAME(invCompDetail1);
				invPackingListResponseDTO.setINS_CNSG_ADD1(invCompDetail2);
				invPackingListResponseDTO.setINS_CNSG_ADD2(invCompDetail3);
				invPackingListResponseDTO.setINS_CNSG_ADD3(invCompDetail4);
				invPackingListResponseDTO.setINS_CNSG_ADD4(invCompDetail5);
				invPackingListResponseDTO.setINS_INV_NO(invNumber);
				invPackingListResponseDTO.setINS_INV_DT(partDetailDataDTO.getINV_DT());
				invPackingListResponseDTO.setINS_PART_NO(partDetailDataDTO.getPART_NO());
				invPackingListResponseDTO.setINS_UNIT_PER_BOX(partDetailDataDTO.getUNIT_BOX().intValue());
				invPackingListResponseDTO.setINS_SUM_TOT_UNIT(partDetailDataDTO.getSUM_TOT_UNIT().intValue());
				invPackingListResponseDTO.setINS_PART_NAME(partDetailDataDTO.getPRT_NAME());
				invPackingListResponseDTO.setINS_PART_WT(partDetailDataDTO.getPART_WT().doubleValue());
				/*
				  String key = partDetailDataDTO.getCF_CD()+"-"+partDetailDataDTO.getSHIP_MARK(); 
				  BigDecimal grossWeight = map.get(key) != null && map.get(key).getGrossWt() != null ? map.get(key).getGrossWt() : new BigDecimal("0"); 
				  BigDecimal measurement = map.get(key) != null && map.get(key).getTotalM3() != null ? map.get(key).getTotalM3() : new BigDecimal("0"); 
				  BigDecimal noOfModule = map.get(key) != null && map.get(key).getCCount() != null ? map.get(key).getCCount() : new BigDecimal("0");
				 */
				invPackingListResponseDTO.setINS_GROSS_WT(grossWeight.doubleValue());
				invPackingListResponseDTO.setINS_MEASUREMENT(measurement.doubleValue());
				invPackingListResponseDTO.setINS_SHIPMARK_4(partDetailDataDTO.getSHIP_MARK());
				invPackingListResponseDTO.setINS_SHIPMARK_5(partDetailDataDTO.getSHIP_MARK4());
				invPackingListResponseDTO.setSHIP_MARK_GP(partDetailDataDTO.getSHIP_MARK());
				invPackingListResponseDTO
						.setCASE_MOD((partDetailDataDTO.getSHIP_MARK().substring(0, 3) == companyCode)
								? (partDetailDataDTO.getSHIP_MARK().substring(0, 3) + "-"
										+ partDetailDataDTO.getSHIP_MARK().substring(3, 4) + "-"
										+ partDetailDataDTO.getSHIP_MARK().substring(4, 5) + "-"
										+ partDetailDataDTO.getSHIP_MARK().substring(5))
								: partDetailDataDTO.getSHIP_MARK());
				invPackingListResponseDTO.setINS_CF_CD(partDetailDataDTO.getCF_CD());
				invPackingListResponseDTO.setINS_SRS_NAME(partDetailDataDTO.getSERIES());
				invPackingListResponseDTO.setINS_NO_OF_CASES(noOfModule.intValue());

				l_n_Prev_Mod = partDetailDataDTO.getSHIP_MARK();
				l_v_Prev_CF_CD = partDetailDataDTO.getCF_CD();
				l_n_Ctr = l_n_Ctr + 1;

				invPackingListResponseDTOList.add(invPackingListResponseDTO);
			}
		}
		return invPackingListResponseDTOList;
	}

}
