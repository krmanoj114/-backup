package com.tpex.invoice.service;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.WorkInstrReportDTO;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InsInvContainerDtlsRepository;
import com.tpex.repository.OemParameterRepository;
import com.tpex.repository.TpexConfigRepository;

import net.sf.jasperreports.engine.JRException;

@Service
@SuppressWarnings("squid:S3776")
public class WorkInstructionReportServiceImpl implements WorkInstructionReportService {

	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private InsInvContainerDtlsRepository insInvContainerDtlsRepository;

	@Autowired
	OemParameterRepository oemParameterRepository;

	@SuppressWarnings("unused")
	@Override
	public Object getRegularWrkInstructionRptDownload(String etd, String countryCd, String bookingNo, String userId,
			String reportFormat, String reportId) throws ParseException, FileNotFoundException, JRException {

		List<WorkInstrReportDTO> resultlist = new ArrayList<>();

		if (bookingNo.isBlank())
			bookingNo = null;

		SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

		String formatETD = outputFormat.format(inputFormat.parse(etd));

		List<Tuple> list = insInvContainerDtlsRepository.getListOfBookingNum(etd, bookingNo, countryCd);

		for (Tuple obj : list) {

			String bookingNum = null;
			String haisenNo = null;
			String haisenYrMnth = null;

			if (obj.get(0) != null)
				bookingNum = obj.get(0).toString();
			if (obj.get(1) != null)
				haisenNo = obj.get(1).toString();
			if (obj.get(2) != null)
				haisenYrMnth = obj.get(2).toString();

			Tuple bookingDetails = insInvContainerDtlsRepository.getDataForBookingNum(bookingNum, haisenNo,
					haisenYrMnth);

			Integer invoiceCount = insInvContainerDtlsRepository.getInvoiceDataCount(bookingNum, haisenNo,
					haisenYrMnth);

			Integer rRackCount = insInvContainerDtlsRepository.getRRackDataCount(bookingNum, haisenNo, haisenYrMnth);

			String parameter = oemParameterRepository.getParameter();

			List<String> rBoxDataList = insInvContainerDtlsRepository.getRBoxDataCount(bookingNum, haisenNo, haisenYrMnth,
					parameter);

			String rBoxData = rBoxDataList.stream().collect(Collectors.joining(","));

			Tuple nonDGCount = insInvContainerDtlsRepository.getNonDgCount(bookingNum, haisenNo, haisenYrMnth);

			Tuple dGCount = insInvContainerDtlsRepository.getDgCount(bookingNum, haisenNo, haisenYrMnth);

			Object model = insInvContainerDtlsRepository.getDataForSeries(bookingNum, haisenNo, haisenYrMnth);

			List<Tuple> invoiceData = insInvContainerDtlsRepository.getInvoiceData(bookingNum, haisenNo, haisenYrMnth);

			SimpleDateFormat monthDate = new SimpleDateFormat("MMM-yy");
			SimpleDateFormat iformat = new SimpleDateFormat("yyyyMM");

			DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

			List<WorkInstrReportDTO> dtoList = new ArrayList<>();

			if (invoiceData != null && !invoiceData.isEmpty()) {

				for (Tuple t : invoiceData) {

					WorkInstrReportDTO dto = new WorkInstrReportDTO();

					if (t.get(0, String.class) != null)
						dto.setInvoiceNumber(t.get(0).toString());
					if (t.get(1) != null)
						dto.setModNumber(Integer.parseInt(t.get(1).toString()));
					if (t.get(2) != null) {
						String invoiceAmnt = t.get(2).toString();
						dto.setInvoiceAmount(formatter.format(Double.parseDouble(invoiceAmnt)));
					}
					if (t.get(3) != null)
						dto.setSeries(t.get(3).toString());
					if (t.get(4) != null) {
						String monthName = monthDate.format(iformat.parse(t.get(4).toString()));
						dto.setInpPackMonth(monthName.toUpperCase());
					}
					if (t.get(5) != null)
						dto.setAicoName(t.get(5).toString());

					dtoList.add(dto);
				}

			}

			if (!dtoList.isEmpty()) {
				for (WorkInstrReportDTO r : dtoList) {

					r.setBookingNumber(bookingNum);
					r.setHaisenNumber(haisenNo);

					if (bookingDetails != null) {
						if (bookingDetails.get(1) != null)
							r.setFinalDestination(bookingDetails.get(1).toString());
						if (bookingDetails.get(2) != null)
							r.setVanPlantCode(bookingDetails.get(2).toString().toUpperCase());
						if (bookingDetails.get(3) != null)
							r.setInmPackMonth(bookingDetails.get(3).toString().toUpperCase());
						if (bookingDetails.get(4) != null)
							r.setEtd(bookingDetails.get(4).toString().toUpperCase());
						if (bookingDetails.get(5) != null)
							r.setEta(bookingDetails.get(5).toString().toUpperCase());
						if (bookingDetails.get(6) != null)
							r.setVanningDate(bookingDetails.get(6).toString().toUpperCase());
						if (bookingDetails.get(7) != null)
							r.setShippingCompany(bookingDetails.get(7).toString());
						if (bookingDetails.get(8) != null)
							r.setVesselNameOcean(bookingDetails.get(8).toString());
						if (bookingDetails.get(9) != null)
							r.setVoyageNumberOcean(bookingDetails.get(9).toString());
						if (bookingDetails.get(0) != null)
							r.setIndBuyer(bookingDetails.get(0).toString());
					}

					if (invoiceCount != null)
						r.setCount(invoiceCount);

					if (rRackCount != null)
						r.setRackCount(String.valueOf(rRackCount));

					if (nonDGCount != null) {
						if (nonDGCount.get(0) != null)
							r.setNonDg40(nonDGCount.get(0).toString());
						if (nonDGCount.get(1) != null)
							r.setNonDg20(nonDGCount.get(1).toString());
					}

					if (dGCount != null) {
						if (dGCount.get(0) != null)
							r.setDg40(dGCount.get(0).toString());
						if (dGCount.get(1) != null)
							r.setDg20(dGCount.get(1).toString());
					}

					if (model != null)
						r.setModel(model.toString());

					if (rBoxData != null)
						r.setRboxdataCnt(String.valueOf(rBoxData));

					resultlist.add(r);
				}
			} else {

				WorkInstrReportDTO rpt = new WorkInstrReportDTO();

				rpt.setBookingNumber(bookingNum);
				rpt.setHaisenNumber(haisenNo);

				if (bookingDetails != null) {
					if (bookingDetails.get(1) != null)
						rpt.setFinalDestination(bookingDetails.get(1).toString());
					if (bookingDetails.get(2) != null)
						rpt.setVanPlantCode(bookingDetails.get(2).toString().toUpperCase());
					if (bookingDetails.get(3) != null)
						rpt.setInmPackMonth(bookingDetails.get(3).toString().toUpperCase());
					if (bookingDetails.get(4) != null)
						rpt.setEtd(bookingDetails.get(4).toString().toUpperCase());
					if (bookingDetails.get(5) != null)
						rpt.setEta(bookingDetails.get(5).toString().toUpperCase());
					if (bookingDetails.get(6) != null)
						rpt.setVanningDate(bookingDetails.get(6).toString().toUpperCase());
					if (bookingDetails.get(7) != null)
						rpt.setShippingCompany(bookingDetails.get(7).toString());
					if (bookingDetails.get(8) != null)
						rpt.setVesselNameOcean(bookingDetails.get(8).toString());
					if (bookingDetails.get(9) != null)
						rpt.setVoyageNumberOcean(bookingDetails.get(9).toString());
					if (bookingDetails.get(0) != null)
						rpt.setIndBuyer(bookingDetails.get(0).toString());
				}

				if (invoiceCount != null)
					rpt.setCount(invoiceCount);

				if (rRackCount != null)
					rpt.setRackCount(String.valueOf(rRackCount));

				if (nonDGCount != null) {
					if (nonDGCount.get(0) != null)
						rpt.setNonDg40(nonDGCount.get(0).toString());
					if (nonDGCount.get(1) != null)
						rpt.setNonDg20(nonDGCount.get(1).toString());
				}

				if (dGCount != null) {
					if (dGCount.get(0) != null)
						rpt.setDg40(dGCount.get(0).toString());
					if (dGCount.get(1) != null)
						rpt.setDg20(dGCount.get(1).toString());
				}

				if (model != null)
					rpt.setModel(model.toString());

				if (rBoxData != null)
					rpt.setRboxdataCnt(String.valueOf(rBoxData));

				resultlist.add(rpt);

			}
		}

		Object jasperResponse = null;
		Map<String, Object> parameters = new HashMap<>();
		String reportDownloadUrl = null;

		parameters.put("P_I_V_ETD_FROM", etd);
		parameters.put("P_I_V_ETD_TO", etd);
		parameters.put("P_I_V_BOOK_NO", bookingNo);
		parameters.put("P_I_V_COUNTRY_CD", countryCd);
		parameters.put("P_I_V_USER_ID", userId);
		String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat
				: "pdf";
		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		if (StringUtils.isNotBlank(fileFormat) && fileFormat.equals("xlsx")) {
			config.put("setFontSizeFixEnabled", true);
		}
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("reportSizeLimit",
				tpexConfigRepository.findByName("invoiceGeneration.report.size.limit").getValue());
		config.put("storeInDB", "true");
		config.put("loginUserId", "TestUser");

		SimpleDateFormat reqFormat = new SimpleDateFormat("ddMMMyy");
		String etdDate = reqFormat.format(inputFormat.parse(etd));

		String fileName = null;

		if (bookingNo != null) {
			fileName = "WorkInstruction" + "_" + countryCd + "_" + "ETD" + etdDate.toUpperCase() + "_" + bookingNo + "."
					+ fileFormat;
		} else {
			fileName = "WorkInstruction" + "_" + countryCd + "_" + "ETD" + etdDate.toUpperCase() + "." + fileFormat;
		}

		String reportName = "RINS105";

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(resultlist, fileFormat, reportName,
					fileName, parameters, config);
		} else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(resultlist, fileFormat, reportName,
					parameters, config, 0, fileName);

		}
		return jasperResponse;
	}

}
