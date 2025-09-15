package com.tpex.jasperreport.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.repository.RddDownLocDtlRepository;
import com.tpex.util.ConstantUtils;
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
@SuppressWarnings("squid:S3776")
public class JasperReportServiceImpl implements JasperReportService {

	private static final String REPORT_DIRECTORY = "reportDirectory";

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

			ByteArrayOutputStream outputStream = getOutPutStream(reportData, fileFormat, fileTemplateName,
					reportParameters, reportConfiguration);

			Path uploadPath = Paths.get(String.valueOf(reportConfiguration.get(REPORT_DIRECTORY)));

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
			throw new MyResourceNotFoundException("problem while downloading data =" + e.getMessage());
		}

	}

	@Override
	public Object getJasperReportDownloadOfflineV1(List<?> reportData, String fileFormat, String fileTemplateName,
			Map<String, Object> reportParameters, Map<String, Object> reportConfiguration, int reportId,
			String fileName) {
		HashMap<String, Object> map = new HashMap<>();
		StringBuilder sb = new StringBuilder().append(String.valueOf(reportConfiguration.get(ConstantUtils.REPORT_DIRECTORY))).append("/")
				.append(fileName);
		try (FileOutputStream fos = new FileOutputStream(sb.toString())) {
			// load the jrxml file and compile it

			ByteArrayOutputStream outputStream = getOutPutStream(reportData, fileFormat, fileTemplateName,
					reportParameters, reportConfiguration);

			Path uploadPath = Paths.get(String.valueOf(reportConfiguration.get(REPORT_DIRECTORY)));

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
			throw new MyResourceNotFoundException("problem while downloading data =" + e.getMessage());
		}
		return map;
	}

	@Override
	public Object getJasperReportDownloadOnline(List<?> reportData, String fileFormat, String fileTemplateName,
			String fileName, Map<String, Object> reportParameters, Map<String, Object> reportConfiguration)
			throws FileNotFoundException, JRException {

		ByteArrayOutputStream outputStream;
		try {
			outputStream = getOutPutStream(reportData, fileFormat, fileTemplateName, reportParameters,
					reportConfiguration);
		} catch (FileNotFoundException | IllegalAccessException | InvocationTargetException | JRException
				| InvalidInputException e) {
			return null;
		}

		HashMap<String, Object> map = new HashMap<>();
		map.put("outStream", outputStream.toByteArray());
		map.put("fileName", fileName);

		return map;
	}

	public ByteArrayOutputStream getOutPutStream(List<?> reportData, String fileFormat, String fileTemplateName,
			Map<String, Object> reportParameters, Map<String, Object> reportConfiguration)
			throws FileNotFoundException, JRException, IllegalAccessException, InvocationTargetException, InvalidInputException {
		// load the jrxml file and compile it
		String resourceLocation = tpexConfigurationUtil.getFilePath(fileTemplateName);
		JasperPrint jasperPrint = getJasperPrint(reportData, resourceLocation, reportParameters);

		return generateReportAndGetUploadPath(fileFormat, jasperPrint, reportConfiguration);
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
			rddDownLocDtlEntity.setCreateDate(getCurrentTimestamp());
			rddDownLocDtlEntity.setUpdateBy(loginUserId);
			rddDownLocDtlEntity.setUpdateDate(getCurrentTimestamp());
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

		return JasperFillManager.fillReport(jasperReport, reportParameters, dataSource);
	}

	private ByteArrayOutputStream generateReportAndGetUploadPath(String fileFormat, JasperPrint jasperPrint,
			Map<String, Object> reportConfiguration) throws JRException, InvalidInputException, IllegalAccessException, InvocationTargetException {
		// Generate report in PDF format
		if (fileFormat.equalsIgnoreCase("pdf")) {
			return generatePdfReportAndGetUploadPath(jasperPrint, reportConfiguration);
		} else if (fileFormat.equalsIgnoreCase("xlsx")) { // Generate report in excel format
			return generateExcelReportAndGetUploadPath(jasperPrint, reportConfiguration);
		} else {
			throw new InvalidInputException("Invalid file format");
		}
	}
	
	private ByteArrayOutputStream generatePdfReportAndGetUploadPath(JasperPrint jasperPrint,
			Map<String, Object> reportConfiguration) throws JRException, IllegalAccessException, InvocationTargetException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();
		// Set exporter input and output
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

		// Set report configuration
		if (reportConfiguration != null) {
			SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
			BeanUtils.copyProperties(reportConfig, reportConfiguration);
			exporter.setConfiguration(reportConfig);
		}

		// Export report
		exporter.exportReport();
		return outputStream;
	}
	
	private ByteArrayOutputStream generateExcelReportAndGetUploadPath(JasperPrint jasperPrint,
			Map<String, Object> reportConfiguration) throws JRException, IllegalAccessException, InvocationTargetException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JRXlsxExporter exporter = new JRXlsxExporter();
		// Set exporter input and output
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
		
		// Set report configuration
		if (reportConfiguration != null) {
			SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
			BeanUtils.copyProperties(reportConfig, reportConfiguration);
			// Set configuration to exporter
			exporter.setConfiguration(reportConfig);
		}
		
		exporter.exportReport();
		return outputStream;
	}

}
