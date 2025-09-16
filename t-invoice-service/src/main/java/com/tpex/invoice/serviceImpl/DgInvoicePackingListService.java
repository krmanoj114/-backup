package com.tpex.invoice.serviceimpl;

import java.io.FileNotFoundException;
import java.text.ParseException;
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

import net.sf.jasperreports.engine.JRException;

@Service
@SuppressWarnings({"squid:S3776","squid:S107"})
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
	 * @throws JRException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 * @throws Exception
	 */
	public Object downloadDgInvoicePackingListReport(String bookingNo, String etd, String eta, String destination,
			String reportFormat, String templateId) throws FileNotFoundException, JRException, ParseException {

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
					dto.setRenban(obj[0].toString());
				if (obj[1] != null)
					dto.setTotCasesRb(obj[1].toString());
				if (obj[2] != null)
					dto.setCaseNo(obj[2].toString());
				if (obj[3] != null)
					dto.setTotNwCase(Double.valueOf(obj[3].toString()));
				if (obj[4] != null)
					dto.setTotGwCase(Double.valueOf(obj[4].toString()));
				if (obj[5] != null)
					dto.setTotM3Case(Double.valueOf(obj[5].toString()));
				if (obj[6] != null)
					dto.setPartNo(obj[6].toString());
				if (obj[7] != null)
					dto.setPartNm(obj[7].toString());
				if (obj[8] != null)
					dto.setQtyBox(obj[8].toString());
				if (obj[9] != null)
					dto.setTotQtyPcs(obj[9].toString());
				if (obj[10] != null)
					dto.setNetWtPc(obj[10].toString());
				if (obj[11] != null)
					dto.setTotNetWtPc(obj[11].toString());
				if (obj[12] != null)
					dto.setBoxGrossWtPc(obj[12].toString());
				if (obj[13] != null)
					dto.setBoxM3(obj[13].toString());
				if (obj[14] != null)
					dto.setInvList(obj[14].toString().substring(0, obj[14].toString().length() - 1));
				if (obj[15] != null)
					dto.setTotNwRb(obj[15].toString());
				if (obj[16] != null)
					dto.setTotGwRb(obj[16].toString());
				if (obj[17] != null)
					dto.setTotM3Rb(obj[17].toString());
				if (obj[18] != null)
					dto.setBookingNo(obj[18].toString());
				if (obj[19] != null)
					dto.setCtryCd(obj[19].toString());
				response.add(dto);
			}
		}

		String etdDate = DateUtil.getStringDate(etd);

		String fileName = null;
		fileName = "DG_declare" + "_" + contryCd + "_" + etdDate + "." + fileFormat;

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(response, fileFormat, templateId,
					fileName, parameters, config);

		} else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(response, fileFormat, templateId,
					parameters, config, 0, fileName);
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
	 * @throws JRException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 * @throws Exception
	 */
	public Object download2DgInvoicePackingListReport(String etd, String destination, String reportFormat,
			String invoiceNo, String templateId, String orderType, String cmpCd, String usertId)
			throws FileNotFoundException, JRException, ParseException {
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
					commonDto.setInsCnsgName(obj[0].toString());
				if (obj[1] != null)
					commonDto.setInsCnsgAdd1(obj[1].toString());
				if (obj[2] != null)
					commonDto.setInsCnsgAdd2(obj[2].toString());
				if (obj[3] != null)
					commonDto.setInsCnsgAdd3(obj[3].toString());
				if (obj[4] != null) {
					commonDto.setInsCnsgAdd4(obj[4].toString());
				}
				if (obj[5] != null) {
					commonDto.setInsInvNo(obj[5].toString());
				}
				if (obj[6] != null) {
					commonDto.setInsInvDt(obj[6].toString());
				}
				if (obj[7] != null) {
					commonDto.setInsPartNo(obj[7].toString());
				}
				if (obj[8] != null) {
					commonDto.setInsUnitPerBox(Integer.valueOf(obj[8].toString()));
				}
				if (obj[9] != null) {
					commonDto.setInsSumTotUnit(Integer.valueOf(obj[9].toString()));
				}
				if (obj[10] != null) {
					commonDto.setInsIcoFlg(obj[10].toString());
				}
				if (obj[11] != null) {
					commonDto.setInsPartPrice(Double.valueOf(obj[11].toString()));
				}
				if (obj[12] != null) {
					commonDto.setInsPartName(obj[12].toString());
				}
				if (obj[13] != null) {
					commonDto.setInsPartWt(Double.valueOf(obj[13].toString()));
				}
				if (obj[14] != null) {
					commonDto.setInsGrossWt(Double.valueOf(obj[14].toString()));
				}
				if (obj[15] != null) {
					commonDto.setInsMeasurement(Double.valueOf(obj[15].toString()));
				}
				if (obj[16] != null) {
					commonDto.setInsShipmark4(obj[16].toString());
				}
				if (obj[17] != null) {
					commonDto.setInsShipmark5(obj[17].toString());
				}
				if (obj[18] != null) {
					commonDto.setShipMarkGp(obj[18].toString());
				}

				if (obj[19] != null) {
					commonDto.setCaseMod(obj[19].toString());
				}
				if (obj[20] != null) {
					commonDto.setInsCfCd(obj[20].toString());
				}
				if (obj[21] != null) {
					commonDto.setInsSrsName(obj[21].toString());
				}
				if (obj[22] != null) {
					commonDto.setInsNoOfCase(Integer.valueOf(obj[22].toString()));
				}
				if (obj[23] != null) {
					commonDto.setInsNoOfBoxes(Integer.valueOf(obj[23].toString()));
				}
				if (obj[24] != null) {
					commonDto.setInsContSno(obj[24].toString());
				}
				if (obj[25] != null) {
					commonDto.setInsIsoContNo(obj[25].toString());
				}
				if (obj[26] != null) {
					commonDto.setInsTptCd(obj[26].toString());
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
		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(response, fileFormat, templateId,
					fileName, parameters, config);
		} else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(response, fileFormat, templateId,
					parameters, config, 0, fileName);

		}
		return jasperResponse;
	}

}
