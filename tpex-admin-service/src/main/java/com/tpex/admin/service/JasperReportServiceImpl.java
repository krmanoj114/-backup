package com.tpex.admin.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.tpex.admin.entity.RddDownLocDtlEntity;
import com.tpex.admin.exception.MyResourceNotFoundException;
import com.tpex.admin.repository.RddDownLocDtlRepository;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.TpexConfigurationUtil;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
@RequiredArgsConstructor
@SuppressWarnings("squid:S3776")
public class JasperReportServiceImpl implements JasperReportService {

	@Autowired
	private RddDownLocDtlRepository rddDownLocDtlRepository;

	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;

	@Override
	public void getJasperReportDownloadOffline(List<?> reportData, String fileFormat, String fileTemplateName,
			Map<String, Object> reportParameters, Map<String, Object> reportConfiguration, int reportId,
			StringBuilder sb) {

		try (FileOutputStream fos = new FileOutputStream(sb.toString())) {
			// load the jrxml file and compile it
			String resourceLocation = tpexConfigurationUtil.getFilePath(fileTemplateName);
			JasperPrint jasperPrint = getJasperPrint(reportData, resourceLocation, reportParameters);
			ByteArrayOutputStream outputStream = generateReportAndGetUploadPath(fileFormat, jasperPrint,
					reportConfiguration);

			Path uploadPath = Paths.get(String.valueOf(reportConfiguration.get("reportDirectory")));
			// Create directory if not exist
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			fos.write(outputStream.toByteArray());
			/* Update offline download detail to db with status Completed */
			RddDownLocDtlEntity rddDownLocDtlEntity = rddDownLocDtlRepository.findByReportId(reportId);
			rddDownLocDtlEntity.setStatus("Success");
			rddDownLocDtlRepository.save(rddDownLocDtlEntity);
		} catch (Exception e) {
			throw new MyResourceNotFoundException("problem while downloading data =" + e.getMessage());

		}
	}

	@Override
	public Object getJasperReportDownloadOnline(List<?> reportData, String fileFormat, String fileTemplateName,
			Map<String, Object> reportParameters, Map<String, Object> reportConfiguration)
			throws JRException, IOException {
		String fileName = null;

		// load the jrxml file and compile it
		if("report.xlsx".equals(fileTemplateName.split("_")[7])) {
			fileName = getFileName(fileTemplateName, fileFormat);
			fileTemplateName = "DailyContainerRequisitionSheet";
		}
		else if ("data".equals(fileTemplateName.split("_")[7])) {
			fileName = getFileName(fileTemplateName, fileFormat);
			fileTemplateName = "DailyContainerRequisitionOnlyDataSheet";
		}
		else {	
			 fileName = getFileName(fileTemplateName, fileFormat);
		}
		String resourceLocation = tpexConfigurationUtil.getFilePath(fileTemplateName);
		JasperPrint jasperPrint = getJasperPrint(reportData, resourceLocation, reportParameters);
		ByteArrayOutputStream outputStream = generateReportAndGetUploadPath(fileFormat, jasperPrint,
				reportConfiguration);
		// Return stream to download file directly
		HashMap<String, Object> map = new HashMap<>();
		map.put("outStream", outputStream.toByteArray());
		map.put("fileName", fileName);
		return map;

	}

	public String getFileName(String fileTemplateName, String fileFormat) {
		LocalDateTime ldt = LocalDateTime.now();
		LocalDate ld = LocalDate.now();
		String formattedDateStr = DateTimeFormatter.ofPattern(ConstantUtils.DATE_HOUR).format(ldt);
		String formattedOnlyDateStr = DateTimeFormatter.ofPattern(ConstantUtils.DATE).format(ld);
		String fileName = fileTemplateName + "_" + formattedDateStr + "." + fileFormat;
		if (("InvoiceHeaderPage".equalsIgnoreCase(fileTemplateName))
				|| ("AttachedInvoiceHeaderPage".equalsIgnoreCase(fileTemplateName))
				|| ("PackingList".equalsIgnoreCase(fileTemplateName))
				|| ("PackingList(forCustomBroker)".equalsIgnoreCase(fileTemplateName))
				|| ("DGPackingList".equalsIgnoreCase(fileTemplateName))
				|| ("InvoiceHeaderPagewithPrivilege".equalsIgnoreCase(fileTemplateName))
				|| ("VinNo.List".equalsIgnoreCase(fileTemplateName))
				|| ("SCInvoiceAttachedSheet".equalsIgnoreCase(fileTemplateName))
				|| ("CountryofOrigins(COO)".equalsIgnoreCase(fileTemplateName))
				|| ("AttachedSheet".equalsIgnoreCase(fileTemplateName))) {
			fileName = fileTemplateName + "." + fileFormat;
		} else if ("AddressMaster".equalsIgnoreCase(fileTemplateName)) {
			fileName = fileTemplateName + "_" + formattedOnlyDateStr + "." + fileFormat;
		}
		else if (("report.xlsx".equals(fileTemplateName.split("_")[7])
				||("data".equals(fileTemplateName.split("_")[7])))) {
			fileName = fileTemplateName;
		}
		return fileName;
	}

	public RddDownLocDtlEntity saveOfflineDownloadDetail(String fileTemplateName,
			Map<String, Object> reportConfiguration, StringBuilder sb) {
		RddDownLocDtlEntity savedRddDownLocDtlEntity = null;

		if ("true".equalsIgnoreCase(String.valueOf(reportConfiguration.get("storeInDB")))) {
			String loginUserId = String.valueOf(reportConfiguration.get("loginUserId"));
			RddDownLocDtlEntity rddDownLocDtlEntity = new RddDownLocDtlEntity();
			if ("NationalCalendar".equalsIgnoreCase(fileTemplateName))
				rddDownLocDtlEntity.setReportName("NationalCalendarMaster");
			else if ("AddressMaster".equalsIgnoreCase(fileTemplateName))
				rddDownLocDtlEntity.setReportName("Master-Address");
			else if ("Lotpartshortage".equalsIgnoreCase(fileTemplateName))
				rddDownLocDtlEntity.setReportName("Lot Part Shortage Information");
			else
				rddDownLocDtlEntity.setReportName(fileTemplateName);
			rddDownLocDtlEntity.setStatus("Processing");
			rddDownLocDtlEntity.setDownLoc(sb.toString());
			rddDownLocDtlEntity.setCreateBy(loginUserId);

			rddDownLocDtlEntity.setCreateDate(new Timestamp(System.currentTimeMillis()));

			rddDownLocDtlEntity.setUpdateBy(loginUserId);
			rddDownLocDtlEntity
					.setUpdateDate(new Timestamp(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime()));
			savedRddDownLocDtlEntity = rddDownLocDtlRepository.save(rddDownLocDtlEntity);
		}
		return savedRddDownLocDtlEntity;
	}

	private JasperPrint getJasperPrint(List<?> reportData, String resourceLocation,
			Map<String, Object> reportParameters) throws JRException, FileNotFoundException {
		// load the jrxml file
		File file = ResourceUtils.getFile(resourceLocation);
		// Compile the jrxml file
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		// Get DataSource for provided data and fill in the report
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
		return JasperFillManager.fillReport(jasperReport, reportParameters, dataSource);
	}

	private ByteArrayOutputStream generateReportAndGetUploadPath(String fileFormat, JasperPrint jasperPrint,
			Map<String, Object> reportConfiguration) throws JRException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// Generate report in PDF format
		if (fileFormat.equalsIgnoreCase("pdf")) {
			JRPdfExporter exporter = new JRPdfExporter();

			// Set exporter input and output
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

			// Set report configuration
			if (reportConfiguration != null) {
				SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
				if (reportConfiguration.containsKey("setSizePageToContent")) {
					reportConfig.setSizePageToContent((Boolean) reportConfiguration.get("setSizePageToContent"));
				}

				if (reportConfiguration.containsKey("setForceLineBreakPolicy")) {
					reportConfig.setForceLineBreakPolicy((Boolean) reportConfiguration.get("setForceLineBreakPolicy"));
				}

				if (reportConfiguration.containsKey("setCollapseMissingBookmarkLevels")) {
					reportConfig.setCollapseMissingBookmarkLevels(
							(Boolean) reportConfiguration.get("setCollapseMissingBookmarkLevels"));
				}

				if (reportConfiguration.containsKey(ConstantUtils.END_PAGE_INDEX)) {
					reportConfig.setEndPageIndex((Integer) reportConfiguration.get(ConstantUtils.END_PAGE_INDEX));
				}

				if (reportConfiguration.containsKey("setEvenPageOffsetX")) {
					reportConfig.setEvenPageOffsetX((Integer) reportConfiguration.get("setEvenPageOffsetX"));
				}

				if (reportConfiguration.containsKey("setEvenPageOffsetY")) {
					reportConfig.setEvenPageOffsetY((Integer) reportConfiguration.get("setEvenPageOffsetY"));
				}

				if (reportConfiguration.containsKey("setForceSvgShapes")) {
					reportConfig.setForceSvgShapes((Boolean) reportConfiguration.get("setForceSvgShapes"));
				}

				if (reportConfiguration.containsKey(ConstantUtils.HYPER_LINK)) {
					reportConfig.setIgnoreHyperlink((Boolean) reportConfiguration.get(ConstantUtils.HYPER_LINK));
				}

				if (reportConfiguration.containsKey("setOddPageOffsetX")) {
					reportConfig.setOddPageOffsetX((Integer) reportConfiguration.get("setOddPageOffsetX"));
				}

				if (reportConfiguration.containsKey("setOddPageOffsetY")) {
					reportConfig.setOddPageOffsetY((Integer) reportConfiguration.get("setOddPageOffsetY"));
				}

				if (reportConfiguration.containsKey(ConstantUtils.OFFSET_X)) {
					reportConfig.setOffsetX((Integer) reportConfiguration.get(ConstantUtils.OFFSET_X));
				}

				if (reportConfiguration.containsKey(ConstantUtils.OFFSET_Y)) {
					reportConfig.setOffsetY((Integer) reportConfiguration.get(ConstantUtils.OFFSET_Y));
				}

				if (reportConfiguration.containsKey(ConstantUtils.OVERRIDE_HINTS)) {
					reportConfig.setOverrideHints((Boolean) reportConfiguration.get(ConstantUtils.OVERRIDE_HINTS));
				}

				if (reportConfiguration.containsKey(ConstantUtils.PAGE_INDEX)) {
					reportConfig.setPageIndex((Integer) reportConfiguration.get(ConstantUtils.PAGE_INDEX));
				}

				if (reportConfiguration.containsKey(ConstantUtils.START_PAGE_INDEX)) {
					reportConfig.setStartPageIndex((Integer) reportConfiguration.get(ConstantUtils.START_PAGE_INDEX));
				}
				// Set configuration to exporter
				exporter.setConfiguration(reportConfig);
			}
			// Export report
			exporter.exportReport();

		} else if (fileFormat.equalsIgnoreCase("xlsx")) { // Generate report in excel format
			JRXlsxExporter exporter = new JRXlsxExporter();
			// Set exporter input and output
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			// Set report configuration
			if (reportConfiguration != null) {
				SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
				if (reportConfiguration.containsKey("setAutoFitPageHeight")) {
					reportConfig.setAutoFitPageHeight((Boolean) reportConfiguration.get("setAutoFitPageHeight"));
				}

				if (reportConfiguration.containsKey("setCellHidden")) {
					reportConfig.setCellHidden((Boolean) reportConfiguration.get("setCellHidden"));
				}

				if (reportConfiguration.containsKey("setCellLocked")) {
					reportConfig.setCellLocked((Boolean) reportConfiguration.get("setCellLocked"));
				}

				if (reportConfiguration.containsKey("setCollapseRowSpan")) {
					reportConfig.setCollapseRowSpan((Boolean) reportConfiguration.get("setCollapseRowSpan"));
				}

				if (reportConfiguration.containsKey("setColumnWidthRatio")) {
					reportConfig.setColumnWidthRatio((Float) reportConfiguration.get("setColumnWidthRatio"));
				}

				if (reportConfiguration.containsKey("setDetectCellType")) {
					reportConfig.setDetectCellType((Boolean) reportConfiguration.get("setDetectCellType"));
				}

				if (reportConfiguration.containsKey(ConstantUtils.END_PAGE_INDEX)) {
					reportConfig.setEndPageIndex((Integer) reportConfiguration.get(ConstantUtils.END_PAGE_INDEX));
				}

				if (reportConfiguration.containsKey("setFirstPageNumber")) {
					reportConfig.setFirstPageNumber((Integer) reportConfiguration.get("setFirstPageNumber"));
				}

				if (reportConfiguration.containsKey("setFitHeight")) {
					reportConfig.setFitHeight((Integer) reportConfiguration.get("setFitHeight"));
				}

				if (reportConfiguration.containsKey("setFitWidth")) {
					reportConfig.setFitWidth((Integer) reportConfiguration.get("setFitWidth"));
				}

				if (reportConfiguration.containsKey("setFontSizeFixEnabled")) {
					reportConfig.setFontSizeFixEnabled((Boolean) reportConfiguration.get("setFontSizeFixEnabled"));
				}

				if (reportConfiguration.containsKey("setForcePageBreaks")) {
					reportConfig.setForcePageBreaks((Boolean) reportConfiguration.get("setForcePageBreaks"));
				}

				if (reportConfiguration.containsKey("setFreezeColumn")) {
					reportConfig.setFreezeColumn((String) reportConfiguration.get("setFreezeColumn"));
				}

				if (reportConfiguration.containsKey("setFreezeRow")) {
					reportConfig.setFreezeRow((Integer) reportConfiguration.get("setFreezeRow"));
				}
				if (reportConfiguration.containsKey("setIgnoreAnchors")) {
					reportConfig.setIgnoreAnchors((Boolean) reportConfiguration.get("setIgnoreAnchors"));
				}
				if (reportConfiguration.containsKey("setIgnoreCellBackground")) {
					reportConfig.setIgnoreCellBackground((Boolean) reportConfiguration.get("setIgnoreCellBackground"));
				}
				if (reportConfiguration.containsKey("setIgnoreCellBorder")) {
					reportConfig.setIgnoreCellBorder((Boolean) reportConfiguration.get("setIgnoreCellBorder"));
				}
				if (reportConfiguration.containsKey("setIgnoreGraphics")) {
					reportConfig.setIgnoreGraphics((Boolean) reportConfiguration.get("setIgnoreGraphics"));
				}
				if (reportConfiguration.containsKey(ConstantUtils.HYPER_LINK)) {
					reportConfig.setIgnoreHyperlink((Boolean) reportConfiguration.get(ConstantUtils.HYPER_LINK));
				}
				if (reportConfiguration.containsKey("setIgnorePageMargins")) {
					reportConfig.setIgnorePageMargins((Boolean) reportConfiguration.get("setIgnorePageMargins"));
				}
				if (reportConfiguration.containsKey("setIgnoreTextFormatting")) {
					reportConfig.setIgnoreTextFormatting((Boolean) reportConfiguration.get("setIgnoreTextFormatting"));
				}
				if (reportConfiguration.containsKey("setImageBorderFixEnabled")) {
					reportConfig
							.setImageBorderFixEnabled((Boolean) reportConfiguration.get("setImageBorderFixEnabled"));
				}
				if (reportConfiguration.containsKey("setMaxRowsPerSheet")) {
					reportConfig.setMaxRowsPerSheet((Integer) reportConfiguration.get("setMaxRowsPerSheet"));
				}
				if (reportConfiguration.containsKey(ConstantUtils.OFFSET_X)) {
					reportConfig.setOffsetX((Integer) reportConfiguration.get(ConstantUtils.OFFSET_X));
				}
				if (reportConfiguration.containsKey(ConstantUtils.OFFSET_Y)) {
					reportConfig.setOffsetY((Integer) reportConfiguration.get(ConstantUtils.OFFSET_Y));
				}
				if (reportConfiguration.containsKey("setOnePagePerSheet")) {
					reportConfig.setOnePagePerSheet((Boolean) reportConfiguration.get("setOnePagePerSheet"));
				}
				if (reportConfiguration.containsKey(ConstantUtils.OVERRIDE_HINTS)) {
					reportConfig.setOverrideHints((Boolean) reportConfiguration.get(ConstantUtils.OVERRIDE_HINTS));
				}
				if (reportConfiguration.containsKey(ConstantUtils.PAGE_INDEX)) {
					reportConfig.setPageIndex((Integer) reportConfiguration.get(ConstantUtils.PAGE_INDEX));
				}
				if (reportConfiguration.containsKey("setPageScale")) {
					reportConfig.setPageScale((Integer) reportConfiguration.get("setPageScale"));
				}
				if (reportConfiguration.containsKey("setPassword")) {
					reportConfig.setPassword((String) reportConfiguration.get("setPassword"));
				}
				if (reportConfiguration.containsKey("setPrintFooterMargin")) {
					reportConfig.setPrintFooterMargin((Integer) reportConfiguration.get("setPrintFooterMargin"));
				}
				if (reportConfiguration.containsKey("setPrintHeaderMargin")) {
					reportConfig.setPrintHeaderMargin((Integer) reportConfiguration.get("setPrintHeaderMargin"));
				}
				if (reportConfiguration.containsKey("setPrintPageBottomMargin")) {
					reportConfig
							.setPrintPageBottomMargin((Integer) reportConfiguration.get("setPrintPageBottomMargin"));
				}
				if (reportConfiguration.containsKey("setPrintPageHeight")) {
					reportConfig.setPrintPageHeight((Integer) reportConfiguration.get("setPrintPageHeight"));
				}
				if (reportConfiguration.containsKey("setPrintPageLeftMargin")) {
					reportConfig.setPrintPageLeftMargin((Integer) reportConfiguration.get("setPrintPageLeftMargin"));
				}
				if (reportConfiguration.containsKey("setPrintPageRightMargin")) {
					reportConfig.setPrintPageRightMargin((Integer) reportConfiguration.get("setPrintPageRightMargin"));
				}
				if (reportConfiguration.containsKey("setPrintPageTopMargin")) {
					reportConfig.setPrintPageTopMargin((Integer) reportConfiguration.get("setPrintPageTopMargin"));
				}
				if (reportConfiguration.containsKey("setPrintPageWidth")) {
					reportConfig.setPrintPageWidth((Integer) reportConfiguration.get("setPrintPageWidth"));
				}
				if (reportConfiguration.containsKey("setRemoveEmptySpaceBetweenColumns")) {
					reportConfig.setRemoveEmptySpaceBetweenColumns(
							(Boolean) reportConfiguration.get("setRemoveEmptySpaceBetweenColumns"));
				}
				if (reportConfiguration.containsKey("setRemoveEmptySpaceBetweenRows")) {
					reportConfig.setRemoveEmptySpaceBetweenRows(
							(Boolean) reportConfiguration.get("setRemoveEmptySpaceBetweenRows"));
				}
				if (reportConfiguration.containsKey("setSheetFooterCenter")) {
					reportConfig.setSheetFooterCenter((String) reportConfiguration.get("setSheetFooterCenter"));
				}
				if (reportConfiguration.containsKey("setSheetFooterLeft")) {
					reportConfig.setSheetFooterLeft((String) reportConfiguration.get("setSheetFooterLeft"));
				}
				if (reportConfiguration.containsKey("setSheetFooterRight")) {
					reportConfig.setSheetFooterRight((String) reportConfiguration.get("setSheetFooterRight"));
				}
				if (reportConfiguration.containsKey("setSheetHeaderCenter")) {
					reportConfig.setSheetHeaderCenter((String) reportConfiguration.get("setSheetHeaderCenter"));
				}
				if (reportConfiguration.containsKey("setSheetHeaderLeft")) {
					reportConfig.setSheetHeaderLeft((String) reportConfiguration.get("setSheetHeaderLeft"));
				}
				if (reportConfiguration.containsKey("setSheetHeaderRight")) {
					reportConfig.setSheetHeaderRight((String) reportConfiguration.get("setSheetHeaderRight"));
				}
				if (reportConfiguration.containsKey("setSheetNames")) {
					reportConfig.setSheetNames((String[]) reportConfiguration.get("setSheetNames"));
				}
				if (reportConfiguration.containsKey("setShowGridLines")) {
					reportConfig.setShowGridLines((Boolean) reportConfiguration.get("setShowGridLines"));
				}
				if (reportConfiguration.containsKey("setShrinkToFit")) {
					reportConfig.setShrinkToFit((Boolean) reportConfiguration.get("setShrinkToFit"));
				}
				if (reportConfiguration.containsKey(ConstantUtils.START_PAGE_INDEX)) {
					reportConfig.setStartPageIndex((Integer) reportConfiguration.get(ConstantUtils.START_PAGE_INDEX));
				}
				if (reportConfiguration.containsKey("setUseTimeZone")) {
					reportConfig.setUseTimeZone((Boolean) reportConfiguration.get("setUseTimeZone"));
				}
				if (reportConfiguration.containsKey("setWhitePageBackground")) {
					reportConfig.setWhitePageBackground((Boolean) reportConfiguration.get("setWhitePageBackground"));
				}
				if (reportConfiguration.containsKey("setWrapText")) {
					reportConfig.setWrapText((Boolean) reportConfiguration.get("setWrapText"));
				}
				// Set configuration to exporter
				exporter.setConfiguration(reportConfig);
			}
			// Export report
			exporter.exportReport();
		}
		return outputStream;
	}

}
