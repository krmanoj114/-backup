package com.tpex.invoice.serviceImpl;

import java.math.BigDecimal;
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

import com.tpex.invoice.dto.CertificateOriginReportDTO;
import com.tpex.invoice.service.CertificateOriginReportService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.CertificateOriginReportRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

@Service
@Transactional
public class CertificateOriginReportServiceImpl implements CertificateOriginReportService {

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Autowired
	CertificateOriginReportRepository certificateOriginReportRepository;

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Object getCertificateOriginReportDownload(String cmpCd, String invNumber, String userId, String reportName,
			String reportFormat) throws Exception {
		Map<String, Object> parameters = new HashMap<>();
		Object jasperResponse = null;

		parameters.put(ConstantUtils.INVOICE_NO, invNumber);
		parameters.put(ConstantUtils.USER_ID, userId);

		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,
				tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue());
		config.put(ConstantUtils.REPORT_FORMAT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT).getValue());
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_SIZE_LIMIT).getValue());
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);

		String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat
				: "pdf";
		String fileName = invNumber + "_" + "COO" + "." + fileFormat;
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY))).append("/")
				.append(fileName);
		List<CertificateOriginReportDTO> certificateOriginReportDTOList = getcertificateOriginReportData(cmpCd,
				invNumber);

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(certificateOriginReportDTOList, fileFormat, reportName, fileName, parameters, config);
		
		}else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(certificateOriginReportDTOList, fileFormat, reportName, parameters, config, 0, sb, fileName);

		}
		return jasperResponse;

	}

	public List<CertificateOriginReportDTO> getcertificateOriginReportData(String cmpCd, String invNumber) {
		
		List<CertificateOriginReportDTO> certificateOriginReportDTOList ;
		Tuple invCompDetail = null;
		Tuple invConsigneeDetail = null;
		String tmapthInvFlg = certificateOriginReportRepository.getTmapthInvFlg(invNumber);
		String compCode = certificateOriginReportRepository.getCompCode(invNumber);
		if ("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = certificateOriginReportRepository.getInvCompDetailWhenFlgY(cmpCd, compCode);
		} else if ("N".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = certificateOriginReportRepository.getInvCompDetailWhenFlgN(cmpCd);
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
		
		invConsigneeDetail = certificateOriginReportRepository.getConsigneeDetails();
		
		String invConsigneeDetail1 = invConsigneeDetail == null || invConsigneeDetail.get(0, String.class) == null ? ""
				: invConsigneeDetail.get(0, String.class);
		String invConsigneeDetail2 = invConsigneeDetail == null || invConsigneeDetail.get(1, String.class) == null ? ""
				: invConsigneeDetail.get(1, String.class);
		String invConsigneeDetail3 = invConsigneeDetail == null || invConsigneeDetail.get(2, String.class) == null ? ""
				: invConsigneeDetail.get(2, String.class);
		String invConsigneeDetail4 = invConsigneeDetail == null || invConsigneeDetail.get(3, String.class) == null ? ""
				: invConsigneeDetail.get(3, String.class);
		String invConsigneeDetail5 = invConsigneeDetail == null || invConsigneeDetail.get(4, String.class) == null ? ""
				: invConsigneeDetail.get(4, String.class);
		String invConsigneeDetail6 = invConsigneeDetail == null || invConsigneeDetail.get(5, String.class) == null ? ""
				: invConsigneeDetail.get(5, String.class);
		List<Tuple> notifyDetailList = certificateOriginReportRepository.getNotifyDetails(invNumber);

		certificateOriginReportDTOList = notifyDetailList.stream()
				.map(t -> new CertificateOriginReportDTO(invCompDetail1,invCompDetail2,invCompDetail3,invCompDetail4,invCompDetail5,
						invNumber,t.get(10, String.class), t.get(16, BigDecimal.class),t.get(11, String.class),
						invConsigneeDetail1,
						invConsigneeDetail2,invConsigneeDetail3,invConsigneeDetail4,invConsigneeDetail5,invConsigneeDetail6,
						t.get(12, String.class),t.get(1, String.class),t.get(3, String.class),
						t.get(4, String.class),t.get(5, String.class),t.get(6, String.class),t.get(7, String.class),
						t.get(8, String.class),t.get(13, String.class),t.get(14, String.class),t.get(15, String.class),
						t.get(2, String.class),t.get(9, String.class),t.get(0, String.class)))
				.collect(Collectors.toList());

		return certificateOriginReportDTOList;
	}

}
