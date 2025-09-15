package com.tpex.invoice.serviceImpl;

import java.time.LocalDateTime;
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
import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.invoice.dto.InvPackingListResponseDTO;
import com.tpex.invoice.service.InvoiceHeaderPageService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvPackingListRepository;
import com.tpex.repository.InvoiceHeaderPageRepository;
import com.tpex.repository.TpexConfigRepository;


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
	public Object getAttachedInvHeaderPageRptDownload(String invNumber, String fileTemplateName, String reportFormat) throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		String reportDownloadUrl = null;
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
		String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat : "pdf";
		String fileName = invNumber+"_COV_ATT."+fileFormat;

		List<AttachedInvHeaderPageResponseDTO> responseDtoList = geAttacehdSheetData(invNumber);
		
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(fileName);
		
		if ("xlsx".equals(fileFormat)) {
			response = jasperReportService.getJasperReportDownloadOnline(responseDtoList, fileFormat, fileTemplateName, fileName, parameters, config);
		}else {
			response = jasperReportService.getJasperReportDownloadOfflineV1(responseDtoList, fileFormat, fileTemplateName, parameters, config, 0, sb, fileName);

		}
		return response;
	}
	
	List<AttachedInvHeaderPageResponseDTO> geAttacehdSheetData(String invNumber) {
		
		Tuple invCompDetail = null;
		
		String companyCode = invPackingListRepository.getCompanyCode(); //TODO : input pram
		
		String countryOfOrigin = invoiceHeaderPageRepository.getCountryOfOrigin();
		
		String compCode = invPackingListRepository.getCompCode(invNumber);
		
		String tmapthInvFlg = invPackingListRepository.getTmapthInvFlg(invNumber);
		if("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgY(companyCode, compCode);
		}else {
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
		
		attachedInvHeaderPageResponseDTO.setCMP_NAME(invCompDetail1);
		attachedInvHeaderPageResponseDTO.setCMP_ADD_1(invCompDetail2);   
		attachedInvHeaderPageResponseDTO.setCMP_ADD_2(invCompDetail3); 
		attachedInvHeaderPageResponseDTO.setCMP_ADD_3(invCompDetail4);
		attachedInvHeaderPageResponseDTO.setCMP_ADD_4(invCompDetail5);
		attachedInvHeaderPageResponseDTO.setIND_MARK1(invHeaderPageDetail.get(0, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK2(invHeaderPageDetail.get(1, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK3(invHeaderPageDetail.get(2, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_1(invHeaderPageDetail.get(3, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_2(invHeaderPageDetail.get(4, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_3(invHeaderPageDetail.get(5, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_4(invHeaderPageDetail.get(6, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_5(invHeaderPageDetail.get(7, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_6(invHeaderPageDetail.get(8, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_7(invHeaderPageDetail.get(9, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_8(invHeaderPageDetail.get(10, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_9(invHeaderPageDetail.get(11, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_10(invHeaderPageDetail.get(12, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_11(invHeaderPageDetail.get(13, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK4_12(invHeaderPageDetail.get(14, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_1(invHeaderPageDetail.get(15, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_2(invHeaderPageDetail.get(16, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_3(invHeaderPageDetail.get(17, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_4(invHeaderPageDetail.get(18, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_5(invHeaderPageDetail.get(19, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_6(invHeaderPageDetail.get(20, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_7(invHeaderPageDetail.get(21, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_8(invHeaderPageDetail.get(22, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_9(invHeaderPageDetail.get(23, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_10(invHeaderPageDetail.get(24, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_11(invHeaderPageDetail.get(25, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK5_12(invHeaderPageDetail.get(26, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_1(invHeaderPageDetail.get(27, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_2(invHeaderPageDetail.get(28, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_3(invHeaderPageDetail.get(29, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_4(invHeaderPageDetail.get(30, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_5(invHeaderPageDetail.get(31, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_6(invHeaderPageDetail.get(32, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_7(invHeaderPageDetail.get(33, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_8(invHeaderPageDetail.get(34, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_9(invHeaderPageDetail.get(35, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_10(invHeaderPageDetail.get(36, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_11(invHeaderPageDetail.get(37, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK6_12(invHeaderPageDetail.get(38, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK7(invHeaderPageDetail.get(39, String.class));
		attachedInvHeaderPageResponseDTO.setIND_MARK8(invHeaderPageDetail.get(40, String.class));
		attachedInvHeaderPageResponseDTO.setINV_DT(invHeaderPageDetail.get(42, String.class));
		attachedInvHeaderPageResponseDTO.setBUYER_CNTRY_FINAL(finalCountryName);
		attachedInvHeaderPageResponseDTO.setCNTRY_ORG(countryOfOrigin);
		
		responseDtoList.add(attachedInvHeaderPageResponseDTO);
		return responseDtoList;
		
	}

}
