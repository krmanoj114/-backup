package com.tpex.invoice.serviceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.DgInvoicePackingListResponseDto;
import com.tpex.invoice.dto.DgTwoInvoicePackingListResponseDto;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.DgDeclarationReportResponseRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

@Service
public class DgInvoicePackingListService {

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	DgDeclarationReportResponseRepository dgDeclarationReportResponseRepository;

	@Autowired
	OemFnlDstMstRepository oemFnlDstMstRepository;

	/**
	 * Methof for generate report of DG Declaration Report RINS104 in PDF formate
	 * 
	 * @author Mohd.Javed
	 * @param request
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	public Object downloadDgInvoicePackingListReport(String bookingNo, String etd, String eta, String destination,
			String reportFormat, String templateId) throws Exception {

		Object jasperResponse = null;
		String contryCd = null;
		if (destination != null && StringUtils.isNotBlank(destination)) {
			contryCd = destination;
		}
		if (bookingNo.isBlank())
			bookingNo = null;

		if (StringUtils.isBlank(eta)) {
			eta = etd;
		}

		Map<String, Object> parameters = new HashMap<>();
		parameters.put(ConstantUtils.ETD_FROM, etd);
		parameters.put(ConstantUtils.ETD_TO, eta);
		parameters.put(ConstantUtils.BOOKING_NO, bookingNo);

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

		List<DgInvoicePackingListResponseDto> response = new ArrayList<>();

		List<Object[]> dataList1 = dgDeclarationReportResponseRepository.getRins104Data(etd, eta, bookingNo, contryCd);
		if (!dataList1.isEmpty()) {
			for (Object[] obj : dataList1) {
				DgInvoicePackingListResponseDto dto = new DgInvoicePackingListResponseDto();

				if (obj[0] != null)
					dto.setINS014_RENBAN(obj[0].toString());
				if (obj[1] != null)
					dto.setINS014_TOT_CASES_RB(obj[1].toString());
				if (obj[2] != null)
					dto.setINS014_CASE_NO(obj[2].toString());
				if (obj[3] != null)
					dto.setINS014_TOT_NW_CASE(Double.valueOf(obj[3].toString()));
				if (obj[4] != null)
					dto.setINS014_TOT_GW_CASE(Double.valueOf(obj[4].toString()));
				if (obj[5] != null)
					dto.setINS014_TOT_M3_CASE(Double.valueOf(obj[5].toString()));
				if (obj[6] != null)
					dto.setINS014_PART_NO(obj[6].toString());
				if (obj[7] != null)
					dto.setINS014_PART_NM(obj[7].toString());
				if (obj[8] != null)
					dto.setINS014_QTY_BOX(obj[8].toString());
				if (obj[9] != null)
					dto.setINS014_TOT_QTY_PCS(obj[9].toString());
				if (obj[10] != null)
					dto.setINS014_NET_WT_PC(obj[10].toString());
				if (obj[11] != null)
					dto.setINS014_TOT_NET_WT_PC(obj[11].toString());
				if (obj[12] != null)
					dto.setINS014_BOX_GROSS_WT_PC(obj[12].toString());
				if (obj[13] != null)
					dto.setINS014_BOX_M3(obj[13].toString());
				if (obj[14] != null)
					dto.setINS014_INV_LIST(obj[14].toString().substring(0, obj[14].toString().length() - 1));
				if (obj[15] != null)
					dto.setINS014_TOT_NW_RB(obj[15].toString());
				if (obj[16] != null)
					dto.setINS014_TOT_GW_RB(obj[16].toString());
				if (obj[17] != null)
					dto.setINS014_TOT_M3_RB(obj[17].toString());
				if (obj[18] != null)
					dto.setINS014_BOOKING_NO(obj[18].toString());
				if (obj[19] != null)
					dto.setINS014_CTRY_CD(obj[19].toString());
				response.add(dto);
			}
		}

		String etdDate = DateUtil.getStringDate(etd);

		String fileName = null;
		fileName = "DG_declare" + "_" + contryCd + "_" + etdDate + "." + fileFormat;
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
				.append("/").append(fileName);

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(response, fileFormat, templateId,
					fileName, parameters, config);

		} else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(response, fileFormat, templateId,
					parameters, config, 0, sb, fileName);
		}
		return jasperResponse;
	}

	/**
	 * method for download report using procedure RINS002DG (DG Invoice Packing
	 * List)
	 * 
	 * @author Mohd.Javed
	 * @param bookingNo
	 * @param etd
	 * @param destination
	 * @param reportFormat
	 * @param invoiceNo
	 * @param templateId
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public Object download2DgInvoicePackingListReport(String etd, String destination, String reportFormat,
			String invoiceNo, String templateId, String orderType, String cmpCd, String usertId) throws Exception {
		if (etd == null || destination == null || destination.equals("") || orderType == null) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}
		Object jasperResponse = null;
		String contryCd = null;
		if (destination != null && StringUtils.isNotBlank(destination)) {
			contryCd = destination;
		}
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
		List<DgTwoInvoicePackingListResponseDto> response = new ArrayList<>();

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("P_I_V_INVOICE_NO", invoiceNo);
		parameters.put("p_i_v_user_id", "NIIT");

		if (invoiceNo != null && StringUtils.isNotBlank(invoiceNo)) {
			dgDeclarationReportResponseRepository.getInvoiceTwoDgData(invoiceNo, orderType, usertId, cmpCd);
		} else if (etd != null && destination != null) {
			dgDeclarationReportResponseRepository.getInvoiceTwoDgData(orderType, usertId, etd, destination, cmpCd);
		}

		List<Object[]> dataList = dgDeclarationReportResponseRepository.getDataFromTemp1(contryCd);

		if (dataList != null && !dataList.isEmpty()) {
			for (Object[] obj : dataList) {
				DgTwoInvoicePackingListResponseDto commonDto = new DgTwoInvoicePackingListResponseDto();
				if (obj[0] != null)
					commonDto.setINS_CNSG_NAME(obj[0].toString());
				if (obj[1] != null)
					commonDto.setINS_CNSG_ADD1(obj[1].toString());
				if (obj[2] != null)
					commonDto.setINS_CNSG_ADD2(obj[2].toString());
				if (obj[3] != null)
					commonDto.setINS_CNSG_ADD3(obj[3].toString());
				if (obj[4] != null) {
					commonDto.setINS_CNSG_ADD4(obj[4].toString());
				}
				if (obj[5] != null) {
					commonDto.setINS_INV_NO(obj[5].toString());
				}
				if (obj[6] != null) {
					commonDto.setINS_INV_DT(obj[6].toString());
				}
				if (obj[7] != null) {
					commonDto.setINS_PART_NO(obj[7].toString());
				}
				if (obj[8] != null) {
					commonDto.setINS_UNIT_PER_BOX(Integer.valueOf(obj[8].toString()));
				}
				if (obj[9] != null) {
					commonDto.setINS_SUM_TOT_UNIT(Integer.valueOf(obj[9].toString()));
				}
				if (obj[10] != null) {
					commonDto.setINS_ICO_FLG(obj[10].toString());
				}
				if (obj[11] != null) {
					commonDto.setINS_PART_PRICE(Double.valueOf(obj[11].toString()));
				}
				if (obj[12] != null) {
					commonDto.setINS_PART_NAME(obj[12].toString());
				}
				if (obj[13] != null) {
					commonDto.setINS_PART_WT(Double.valueOf(obj[13].toString()));
				}
				if (obj[14] != null) {
					commonDto.setINS_GROSS_WT(Double.valueOf(obj[14].toString()));
				}
				if (obj[15] != null) {
					commonDto.setINS_MEASUREMENT(Double.valueOf(obj[15].toString()));
				}
				if (obj[16] != null) {
					commonDto.setINS_SHIPMARK_4(obj[16].toString());
				}
				if (obj[17] != null) {
					commonDto.setINS_SHIPMARK_5(obj[17].toString());
				}
				if (obj[18] != null) {
					commonDto.setSHIP_MARK_GP(obj[18].toString());
				}

				if (obj[19] != null) {
					commonDto.setCASE_MOD(obj[19].toString());
				}
				if (obj[20] != null) {
					commonDto.setINS_CF_CD(obj[20].toString());
				}
				if (obj[21] != null) {
					commonDto.setINS_SRS_NAME(obj[21].toString());
				}
				if (obj[22] != null) {
					commonDto.setINS_NO_OF_CASES(Integer.valueOf(obj[22].toString()));
				}
				if (obj[23] != null) {
					commonDto.setINS_NO_OF_BOXES(Integer.valueOf(obj[23].toString()));
				}
				if (obj[24] != null) {
					commonDto.setINS_CONT_SNO(obj[24].toString());
				}
				if (obj[25] != null) {
					commonDto.setINS_ISO_CONT_NO(obj[25].toString());
				}
				if (obj[26] != null) {
					commonDto.setINS_TPT_CD(obj[26].toString());
				}
				response.add(commonDto);
			}
		}
		String importerName = null;
		Optional<OemFnlDstMstEntity> obj = oemFnlDstMstRepository.findById(destination);
		if (obj.isPresent()) {
			importerName = obj.get().getFdDstNm();
		}
		String fileName = null;
		if (importerName != null) {
			importerName = importerName.replace(" ", "_");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DATEFORMAT);
		Date date = sdf.parse(etd);
		sdf = new SimpleDateFormat(ConstantUtils.NEWDATEFORMAT);
		String etdDate = sdf.format(date);

		fileName = "DG" + "_" + contryCd + "_" + importerName + "_" + etdDate + "." + fileFormat;
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY)))
				.append("/").append(fileName);
		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(response, fileFormat, templateId,
					fileName, parameters, config);
		} else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(response, fileFormat, templateId,
					parameters, config, 0, sb, fileName);

		}
		return jasperResponse;
	}

}
