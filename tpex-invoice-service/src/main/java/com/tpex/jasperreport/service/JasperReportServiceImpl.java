package com.tpex.jasperreport.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
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

import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.repository.RddDownLocDtlRepository;
import com.tpex.util.TpexConfigurationUtil;

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
public class JasperReportServiceImpl implements JasperReportService {

	@Autowired
	private RddDownLocDtlRepository rddDownLocDtlRepository;

	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;

	

	@Override
	public void getJasperReportDownloadOffline(List<?> reportData, String fileFormat, String fileTemplateName,
			Map<String, Object> reportParameters, Map<String, Object> reportConfiguration, int reportId,
			StringBuilder sb) {

		try(FileOutputStream fos = new FileOutputStream(sb.toString())){
			// load the jrxml file and compile it

			ByteArrayOutputStream outputStream = getOutPutStream(reportData, fileFormat, fileTemplateName, reportParameters,
					reportConfiguration);

			Path uploadPath = Paths.get(String.valueOf(reportConfiguration.get("reportDirectory")));

			// Create directory if not exist
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			fos.write(outputStream.toByteArray());
			/* Update offline download detail to db with status Completed */
			if (reportId != 0) {
				RddDownLocDtlEntity rddDownLocDtlEntity = rddDownLocDtlRepository.findByReportId(reportId);
				rddDownLocDtlEntity.setStatus("Success");
				rddDownLocDtlRepository.save(rddDownLocDtlEntity);
			}

		} catch (Exception e) {
		//	e.printStackTrace();
		}
		
	}

	@Override
	public Object getJasperReportDownloadOfflineV1(List<?> reportData, String fileFormat, String fileTemplateName,
												   Map<String, Object> reportParameters, Map<String, Object> reportConfiguration, int reportId,
												   StringBuilder sb, String fileName) {
		HashMap<String, Object> map = new HashMap<>();

		try(FileOutputStream fos = new FileOutputStream(sb.toString())){
			// load the jrxml file and compile it

			ByteArrayOutputStream outputStream = getOutPutStream(reportData, fileFormat, fileTemplateName, reportParameters,
					reportConfiguration);

			Path uploadPath = Paths.get(String.valueOf(reportConfiguration.get("reportDirectory")));

			// Create directory if not exist
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			fos.write(outputStream.toByteArray());
			/* Update offline download detail to db with status Completed */
			if (reportId != 0) {
				RddDownLocDtlEntity rddDownLocDtlEntity = rddDownLocDtlRepository.findByReportId(reportId);
				rddDownLocDtlEntity.setStatus("Success");
				rddDownLocDtlRepository.save(rddDownLocDtlEntity);
			}
			map.put("outStream", outputStream.toByteArray());
			map.put("fileName", fileName);

		} catch (Exception e) {
				//e.printStackTrace();
		}
		return map;
	}

	@Override
	public Object getJasperReportDownloadOnline(List<?> reportData, String fileFormat, String fileTemplateName, String fileName,
			Map<String, Object> reportParameters, Map<String, Object> reportConfiguration) throws FileNotFoundException, JRException {

		ByteArrayOutputStream outputStream = getOutPutStream(reportData, fileFormat, fileTemplateName, reportParameters,
				reportConfiguration);

		//String fileName = getFileName(fileTemplateName, fileFormat);
		// Return stream to download file directly
		HashMap<String, Object> map = new HashMap<>();
		map.put("outStream", outputStream.toByteArray());
		map.put("fileName", fileName);
		
		return map;
	}

	public ByteArrayOutputStream getOutPutStream(List<?> reportData, String fileFormat, String fileTemplateName,
			Map<String, Object> reportParameters, Map<String, Object> reportConfiguration) throws FileNotFoundException, JRException {
		// load the jrxml file and compile it
		String resourceLocation = tpexConfigurationUtil.getFilePath(fileTemplateName);
		JasperPrint jasperPrint = getJasperPrint(reportData, resourceLocation, reportParameters);
		ByteArrayOutputStream outputStream = generateReportAndGetUploadPath(fileFormat, jasperPrint,reportConfiguration);
		return outputStream;
	}
	
	public String getFileName(String fileTemplateName, String fileFormat) {
		LocalDateTime ldt = LocalDateTime.now();
		String formattedDateStr = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss").format(ldt);
		return fileTemplateName + "_" + formattedDateStr + "." + fileFormat;
	}

	public RddDownLocDtlEntity saveOfflineDownloadDetail(String fileTemplateName,
			Map<String, Object> reportConfiguration, StringBuilder sb) {
		RddDownLocDtlEntity savedRddDownLocDtlEntity = null;
		if ("true".equalsIgnoreCase(String.valueOf(reportConfiguration.get("storeInDB")))) {
			String loginUserId = String.valueOf(reportConfiguration.get("loginUserId"));
			RddDownLocDtlEntity rddDownLocDtlEntity = new RddDownLocDtlEntity();
			rddDownLocDtlEntity.setReportName(fileTemplateName);
			rddDownLocDtlEntity.setStatus("Processing");
			rddDownLocDtlEntity.setDownLoc(sb.toString());
			rddDownLocDtlEntity.setCreateBy(loginUserId);
			rddDownLocDtlEntity
					.setCreateDate(getCurrentTimestamp());
			rddDownLocDtlEntity.setUpdateBy(loginUserId);
			rddDownLocDtlEntity
					.setUpdateDate(getCurrentTimestamp());
			savedRddDownLocDtlEntity = rddDownLocDtlRepository.save(rddDownLocDtlEntity);
		}
		return savedRddDownLocDtlEntity;
	}

	private Timestamp getCurrentTimestamp() {
		return new Timestamp(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime());
	}

	private JasperPrint getJasperPrint(List<?> reportData, String resourceLocation,
			Map<String, Object> reportParameters) throws JRException, FileNotFoundException {
		// load the jrxml file
		File file = ResourceUtils.getFile(resourceLocation);

		// Compile the jrxml file
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

		// Get DataSource for provided data and fill in the report
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParameters, dataSource);

		return jasperPrint;
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

				if (reportConfiguration.containsKey("setEndPageIndex")) {
					reportConfig.setEndPageIndex((Integer) reportConfiguration.get("setEndPageIndex"));
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

				if (reportConfiguration.containsKey("setIgnoreHyperlink")) {
					reportConfig.setIgnoreHyperlink((Boolean) reportConfiguration.get("setIgnoreHyperlink"));
				}

				if (reportConfiguration.containsKey("setOddPageOffsetX")) {
					reportConfig.setOddPageOffsetX((Integer) reportConfiguration.get("setOddPageOffsetX"));
				}

				if (reportConfiguration.containsKey("setOddPageOffsetY")) {
					reportConfig.setOddPageOffsetY((Integer) reportConfiguration.get("setOddPageOffsetY"));
				}

				if (reportConfiguration.containsKey("setOffsetX")) {
					reportConfig.setOffsetX((Integer) reportConfiguration.get("setOffsetX"));
				}

				if (reportConfiguration.containsKey("setOffsetY")) {
					reportConfig.setOffsetY((Integer) reportConfiguration.get("setOffsetY"));
				}

				if (reportConfiguration.containsKey("setOverrideHints")) {
					reportConfig.setOverrideHints((Boolean) reportConfiguration.get("setOverrideHints"));
				}

				if (reportConfiguration.containsKey("setPageIndex")) {
					reportConfig.setPageIndex((Integer) reportConfiguration.get("setPageIndex"));
				}

				if (reportConfiguration.containsKey("setStartPageIndex")) {
					reportConfig.setStartPageIndex((Integer) reportConfiguration.get("setStartPageIndex"));
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

				if (reportConfiguration.containsKey("setEndPageIndex")) {
					reportConfig.setEndPageIndex((Integer) reportConfiguration.get("setEndPageIndex"));
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
				if (reportConfiguration.containsKey("setIgnoreHyperlink")) {
					reportConfig.setIgnoreHyperlink((Boolean) reportConfiguration.get("setIgnoreHyperlink"));
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
				if (reportConfiguration.containsKey("setOffsetX")) {
					reportConfig.setOffsetX((Integer) reportConfiguration.get("setOffsetX"));
				}
				if (reportConfiguration.containsKey("setOffsetY")) {
					reportConfig.setOffsetY((Integer) reportConfiguration.get("setOffsetY"));
				}
				if (reportConfiguration.containsKey("setOnePagePerSheet")) {
					reportConfig.setOnePagePerSheet((Boolean) reportConfiguration.get("setOnePagePerSheet"));
				}
				if (reportConfiguration.containsKey("setOverrideHints")) {
					reportConfig.setOverrideHints((Boolean) reportConfiguration.get("setOverrideHints"));
				}
				if (reportConfiguration.containsKey("setPageIndex")) {
					reportConfig.setPageIndex((Integer) reportConfiguration.get("setPageIndex"));
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
				if (reportConfiguration.containsKey("setStartPageIndex")) {
					reportConfig.setStartPageIndex((Integer) reportConfiguration.get("setStartPageIndex"));
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
