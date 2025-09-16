package com.tpex.invoice.serviceimpl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.AttachedInvHeaderPageResponseDTO;
import com.tpex.invoice.service.InvoiceHeaderPageService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvPackingListRepository;
import com.tpex.repository.InvoiceHeaderPageRepository;
import com.tpex.repository.TpexConfigRepository;

import net.sf.jasperreports.engine.JRException;

@Service
@Transactional
public class InvoiceHeaderPageServiceImpl implements InvoiceHeaderPageService {

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Autowired
	InvPackingListRepository invPackingListRepository;

	@Autowired
	InvoiceHeaderPageRepository invoiceHeaderPageRepository;

	@Autowired
	JasperReportService jasperReportService;

	@Override
	public Object getAttachedInvHeaderPageRptDownload(String invNumber, String fileTemplateName, String reportFormat)
			throws FileNotFoundException, JRException {

		Map<String, Object> parameters = new HashMap<>();

		Object response = null;

		parameters.put("P_I_V_INVOICE_NO", invNumber);

		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("storeInDB", "true");
		config.put("loginUserId", "TestUser");
		String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat
				: "pdf";
		String fileName = invNumber + "_COV_ATT." + fileFormat;

		List<AttachedInvHeaderPageResponseDTO> responseDtoList = geAttacehdSheetData(invNumber);

		if ("xlsx".equals(fileFormat)) {
			response = jasperReportService.getJasperReportDownloadOnline(responseDtoList, fileFormat, fileTemplateName,
					fileName, parameters, config);
		} else {
			response = jasperReportService.getJasperReportDownloadOfflineV1(responseDtoList, fileFormat,
					fileTemplateName, parameters, config, 0, fileName);

		}
		return response;
	}

	List<AttachedInvHeaderPageResponseDTO> geAttacehdSheetData(String invNumber) {

		Tuple invCompDetail = null;

		String companyCode = invPackingListRepository.getCompanyCode();

		String countryOfOrigin = invoiceHeaderPageRepository.getCountryOfOrigin();

		String compCode = invPackingListRepository.getCompCode(invNumber);

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

		String finalCountryName = invoiceHeaderPageRepository.getFinalCountryName(invNumber);

		Tuple invHeaderPageDetail = invoiceHeaderPageRepository.getInvoiceHeaderPageDetail(invNumber);

		List<AttachedInvHeaderPageResponseDTO> responseDtoList = new ArrayList<>();

		AttachedInvHeaderPageResponseDTO attachedInvHeaderPageResponseDTO = new AttachedInvHeaderPageResponseDTO();

		attachedInvHeaderPageResponseDTO.setCompanyName(invCompDetail1);
		attachedInvHeaderPageResponseDTO.setCompanyAddress1(invCompDetail2);
		attachedInvHeaderPageResponseDTO.setCompanyAddress2(invCompDetail3);
		attachedInvHeaderPageResponseDTO.setCompanyAddress3(invCompDetail4);
		attachedInvHeaderPageResponseDTO.setCompanyAddress4(invCompDetail5);
		attachedInvHeaderPageResponseDTO.setMark1(invHeaderPageDetail.get(0, String.class));
		attachedInvHeaderPageResponseDTO.setMark2(invHeaderPageDetail.get(1, String.class));
		attachedInvHeaderPageResponseDTO.setMark3(invHeaderPageDetail.get(2, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of1(invHeaderPageDetail.get(3, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of2(invHeaderPageDetail.get(4, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of3(invHeaderPageDetail.get(5, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of4(invHeaderPageDetail.get(6, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of5(invHeaderPageDetail.get(7, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of6(invHeaderPageDetail.get(8, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of7(invHeaderPageDetail.get(9, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of8(invHeaderPageDetail.get(10, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of9(invHeaderPageDetail.get(11, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of10(invHeaderPageDetail.get(12, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of11(invHeaderPageDetail.get(13, String.class));
		attachedInvHeaderPageResponseDTO.setMark4Of12(invHeaderPageDetail.get(14, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of1(invHeaderPageDetail.get(15, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of2(invHeaderPageDetail.get(16, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of3(invHeaderPageDetail.get(17, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of4(invHeaderPageDetail.get(18, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of5(invHeaderPageDetail.get(19, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of6(invHeaderPageDetail.get(20, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of7(invHeaderPageDetail.get(21, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of8(invHeaderPageDetail.get(22, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of9(invHeaderPageDetail.get(23, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of10(invHeaderPageDetail.get(24, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of11(invHeaderPageDetail.get(25, String.class));
		attachedInvHeaderPageResponseDTO.setMark5Of12(invHeaderPageDetail.get(26, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of1(invHeaderPageDetail.get(27, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of2(invHeaderPageDetail.get(28, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of3(invHeaderPageDetail.get(29, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of4(invHeaderPageDetail.get(30, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of5(invHeaderPageDetail.get(31, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of6(invHeaderPageDetail.get(32, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of7(invHeaderPageDetail.get(33, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of8(invHeaderPageDetail.get(34, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of9(invHeaderPageDetail.get(35, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of10(invHeaderPageDetail.get(36, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of11(invHeaderPageDetail.get(37, String.class));
		attachedInvHeaderPageResponseDTO.setMark6Of12(invHeaderPageDetail.get(38, String.class));
		attachedInvHeaderPageResponseDTO.setMark7(invHeaderPageDetail.get(40, String.class));
		attachedInvHeaderPageResponseDTO.setMark8(invHeaderPageDetail.get(39, String.class));
		attachedInvHeaderPageResponseDTO.setInvoiceDate(invHeaderPageDetail.get(42, String.class));
		attachedInvHeaderPageResponseDTO.setBuyerCounteryFinal(finalCountryName);
		attachedInvHeaderPageResponseDTO.setCountryOrigin(countryOfOrigin);

		responseDtoList.add(attachedInvHeaderPageResponseDTO);
		return responseDtoList;

	}

}
