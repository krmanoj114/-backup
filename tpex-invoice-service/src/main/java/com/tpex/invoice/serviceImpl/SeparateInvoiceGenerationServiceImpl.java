package com.tpex.invoice.serviceimpl;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.SeparateInvoiceGenerationResponceDto;
import com.tpex.invoice.service.SeparateInvoiceGenerationService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InsInvDetailsEntityRepository;
import com.tpex.repository.OemParameterEntityRepository;
import com.tpex.repository.PrivilegeTypeEntityRepository;
import com.tpex.repository.SeperateInvoiceGenRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

import net.sf.jasperreports.engine.JRException;

@Service
public class SeparateInvoiceGenerationServiceImpl implements SeparateInvoiceGenerationService {

	@Autowired
	SeperateInvoiceGenRepository seperateInvoiceGenRepository;

	@Autowired
	OemParameterEntityRepository oemParameterEntityRepository;

	@Autowired
	InsInvDetailsEntityRepository insInvDetailsEntityRepository;

	@Autowired
	PrivilegeTypeEntityRepository privilegeTypeEntityRepository;

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Override
	public Object getSeparateInvoiceGeneration(String cmpCd, String invoiceNo, String usertId, String reportId,
			String reportFormat, String invoiceType) throws FileNotFoundException, JRException, ParseException {

		Object jasperResponse = null;

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

		Integer reportIdValue = tpexConfigRepository.findByName(reportId).getId();

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("P_I_V_INVOICE_NO", invoiceNo);
		parameters.put("P_I_V_INVOICE_TYPE", invoiceType);
		parameters.put("P_I_V_DISP_INV_NO", invoiceNo);
		parameters.put("P_I_V_USER_ID", usertId);
		parameters.put("P_I_V_CMP_CD", cmpCd);

		List<SeparateInvoiceGenerationResponceDto> responseDtoList = geAttacehdSheetData(invoiceNo, invoiceType);

		String fileName = invoiceNo + "(" + invoiceType + ")" + "_" + "ATT" + "." + fileFormat;

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(responseDtoList, fileFormat, reportId,
					fileName, parameters, config);

		} else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(responseDtoList, fileFormat, reportId,
					parameters, config, reportIdValue, fileName);

		}
		return jasperResponse;
	}

	List<SeparateInvoiceGenerationResponceDto> geAttacehdSheetData(String invNumber, String invoiceType) {

		List<Object[]> invCompDetail = null;

		String companyCode = oemParameterEntityRepository.findByOprParaCd();

		String invoiceTypeValue = privilegeTypeEntityRepository.findByPrivilegeCode(invoiceType);

		String compCode = seperateInvoiceGenRepository.getCompCode(invNumber);

		String tmapthInvFlg = insInvDetailsEntityRepository.findByTmapthInvFlg(invNumber);

		if ("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = seperateInvoiceGenRepository.getInvCompDetailWhenFlgY(companyCode, compCode);
		} else {
			invCompDetail = seperateInvoiceGenRepository.getInvCompDetailWhenFlgN(companyCode);
		}

		List<Object[]> invHeaderPageDetail = seperateInvoiceGenRepository.getDataForSeperateInvoiceGeneration(invNumber,
				invoiceTypeValue, companyCode);

		List<SeparateInvoiceGenerationResponceDto> responseDtoList = new ArrayList<>();

		invCompDetail.forEach(invComp -> {
			if (invComp != null) {
				String invCompDetail1 = invComp[0].toString();
				String invCompDetail2 = invComp[1].toString();
				String invCompDetail3 = invComp[2].toString();
				String invCompDetail4 = invComp[3].toString();
				String invCompDetail5 = invComp[4].toString();

				invHeaderPageDetail.stream().forEach(obj -> {
					SeparateInvoiceGenerationResponceDto separateInvoiceGenerationResponceDto = new SeparateInvoiceGenerationResponceDto();
					separateInvoiceGenerationResponceDto.setInsCnsgName(invCompDetail1);
					separateInvoiceGenerationResponceDto.setInsCnsgAdd1(invCompDetail2);
					separateInvoiceGenerationResponceDto.setInsCnsgAdd2(invCompDetail3);
					separateInvoiceGenerationResponceDto.setInsCnsgAdd3(invCompDetail4);
					separateInvoiceGenerationResponceDto.setInsCnsgAdd4(invCompDetail5);
					getSeperateInvoiceReport(invoiceType, obj, separateInvoiceGenerationResponceDto);
					
					getInvoiceSeperationReport1(obj, separateInvoiceGenerationResponceDto);
					responseDtoList.add(separateInvoiceGenerationResponceDto);
				});
			}
		});

		return responseDtoList;

	}

	private void getInvoiceSeperationReport1(Object[] obj,
			SeparateInvoiceGenerationResponceDto separateInvoiceGenerationResponceDto) {
		if (obj[14] != null) {
			separateInvoiceGenerationResponceDto.setInsSrsName(obj[14].toString());
		}
		if (obj[15] != null) {
			separateInvoiceGenerationResponceDto.setInsCurrCd(obj[15].toString());
		}
		if (obj[16] != null) {
			separateInvoiceGenerationResponceDto.setInsIcoDesc(obj[16].toString());
		}
		if (obj[17] != null) {
			separateInvoiceGenerationResponceDto.setInsCoCd(obj[17].toString());
		}
	}

	private void getSeperateInvoiceReport(String invoiceType, Object[] obj,
			SeparateInvoiceGenerationResponceDto separateInvoiceGenerationResponceDto) {
		if (obj[0] != null) {
			separateInvoiceGenerationResponceDto.setInsInvNo((obj[0].toString()) + "(" + invoiceType + ")");
		}
		if (obj[1] != null) {
			separateInvoiceGenerationResponceDto.setInsInvDt(obj[1].toString());
		}
		if (obj[2] != null) {
			separateInvoiceGenerationResponceDto.setInsPartNo(obj[2].toString());
		}
		if (obj[3] != null) {
			separateInvoiceGenerationResponceDto.setInsUnitPerBox(Double.valueOf(obj[3].toString()));
		}
		if (obj[4] != null) {
			separateInvoiceGenerationResponceDto.setInsSumTotUnit(Integer.valueOf(obj[4].toString()));
		}
		if (obj[5] != null) {
			separateInvoiceGenerationResponceDto.setInsIcoflg(obj[5].toString());
		}
		if (obj[6] != null) {
			separateInvoiceGenerationResponceDto.setInsPartPrice(Double.valueOf(obj[6].toString()));
		}
		if (obj[7] != null) {
			separateInvoiceGenerationResponceDto.setInsPartName(obj[7].toString());
		}
		if (obj[8] != null) {
			separateInvoiceGenerationResponceDto.setInsPartWt(Double.valueOf(obj[8].toString()));
		}
		if (obj[9] != null) {
			separateInvoiceGenerationResponceDto.setInsGrossWt(Double.valueOf(obj[9].toString()));
		}
		if (obj[10] != null) {
			separateInvoiceGenerationResponceDto.setInsMeasurement(Double.valueOf(obj[10].toString()));
		}
		if (obj[11] != null) {
			separateInvoiceGenerationResponceDto.setInsShipmark4(obj[11].toString());
		}
		if (obj[12] != null) {
			separateInvoiceGenerationResponceDto.setInsShipmark5(obj[12].toString());
		}
		if (obj[13] != null) {
			separateInvoiceGenerationResponceDto.setInsCfCd(obj[13].toString());
		}
		
		
	}

}
