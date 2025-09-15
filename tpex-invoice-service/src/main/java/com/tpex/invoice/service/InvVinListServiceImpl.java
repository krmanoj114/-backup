package com.tpex.invoice.service;

import java.io.FileNotFoundException;
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

import com.tpex.invoice.dto.InvVinListResponseDTO;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvVinListRepository;
import com.tpex.repository.TpexConfigRepository;

import net.sf.jasperreports.engine.JRException;

@Service
@Transactional
public class InvVinListServiceImpl implements InvVinListService {

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Autowired
	InvVinListRepository invVinListRepository;

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Object getInvVinListReportDownload(String cmpCd, String invNumber, String userId, String reportName,
			String reportFormat) throws FileNotFoundException, JRException {

		Map<String, Object> parameters = new HashMap<>();
		Object jasperResponse = null;

		parameters.put("P_I_V_INVOICE_NO", invNumber);
		parameters.put("P_I_V_USER_ID", userId);

		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("storeInDB", "false");
		config.put("loginUserId", "TestUser");

		String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat
				: "pdf";
		String fileName = invNumber + "_" + "VIN" + "." + fileFormat;

		List<InvVinListResponseDTO> invVinListResponseDTOList = getVinListData(cmpCd, invNumber);

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(invVinListResponseDTOList, fileFormat,
					reportName, fileName, parameters, config);
		} else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(invVinListResponseDTOList, fileFormat,
					reportName, parameters, config, 0, fileName);

		}
		return jasperResponse;
	}

	public List<InvVinListResponseDTO> getVinListData(String cmpCd, String invNumber) {
		List<InvVinListResponseDTO> invVinListResponseDTOList = null;
		Tuple invCompDetail = null;
		String tmapthInvFlg = invVinListRepository.getTmapthInvFlg(invNumber);
		String compCode = invVinListRepository.getCompCode(invNumber);

		if ("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = invVinListRepository.getInvCompDetailWhenFlgY(cmpCd, compCode);
		} else if ("N".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = invVinListRepository.getInvCompDetailWhenFlgN(cmpCd);
		}

		String invCompDetail1 = invCompDetail == null || invCompDetail.get(0, String.class) == null ? ""
				: invCompDetail.get(0, String.class);
		String invCompDetail2 = invCompDetail == null || invCompDetail.get(1, String.class) == null ? ""
				: invCompDetail.get(1, String.class);
		String invCompDetail3 = invCompDetail == null || invCompDetail.get(2, String.class) == null ? ""
				: invCompDetail.get(2, String.class);
		String invCompDetail4 = invCompDetail == null || invCompDetail.get(3, String.class) == null ? ""
				: invCompDetail.get(3, String.class);
		String invCompDetail5 = invCompDetail == null || invCompDetail.get(4, String.class) == null ? ""
				: invCompDetail.get(4, String.class);
		List<Tuple> partDetailList = invVinListRepository.getPartDetailData(invNumber);

		invVinListResponseDTOList = partDetailList.stream()
				.map(t -> new InvVinListResponseDTO(t.get(0, String.class), t.get(1, String.class), invCompDetail1,
						invCompDetail2, invCompDetail3, invCompDetail4, invCompDetail5, t.get(2, String.class),
						t.get(3, String.class)))
				.collect(Collectors.toList());

		return invVinListResponseDTOList;
	}

}
