package com.tpex.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tpex.admin.module.OndemandDownRptModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.admin.service.OndemandDownRptServiceImpl;
import com.tpex.admin.dto.RddDownLocDtlDto;
import com.tpex.admin.dto.ReportStatusInformation;
import com.tpex.admin.entity.RddDownLocDtlEntity;
import com.tpex.admin.exception.MyResourceNotFoundException;

import com.tpex.admin.util.ConstantUtils;

/**
 * The Class OndemandDownRptController.
 */
@RestController
@RequestMapping("/onDemandDownload")
@CrossOrigin
public class OndemandDownRptController {

	@Autowired
	OndemandDownRptServiceImpl ondemandDownRptService;

	/**
	 * Search on demand reports.
	 *
	 * @param request the request
	 * @return the response entity
	 * @throws IllegalAccessException    the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws ParseException            the parse exception
	 * @throws ParseException            the parse exception
	 */
	@PostMapping("/reports")
	public ResponseEntity<List<RddDownLocDtlDto>> searchOnDemandReports(@RequestBody OndemandDownRptModule request)
			throws IllegalAccessException, InvocationTargetException, ParseException, java.text.ParseException {

		List<RddDownLocDtlEntity> reports = ondemandDownRptService.searchOnDemandReports(request);

		List<RddDownLocDtlDto> finalReports = new ArrayList<>();

		for (RddDownLocDtlEntity listOfReportDB : reports) {
			RddDownLocDtlDto rddDownLocDto = new RddDownLocDtlDto();
			rddDownLocDto.setCreateBy(listOfReportDB.getCreateBy());

			rddDownLocDto.setCreateDate(
					new SimpleDateFormat(ConstantUtils.DATEWITHTIME).format(listOfReportDB.getCreateDate()));

			rddDownLocDto.setReportId(listOfReportDB.getReportId());
			rddDownLocDto.setReportName(listOfReportDB.getReportName());
			rddDownLocDto.setStatus(listOfReportDB.getStatus());
			rddDownLocDto.setUpdateBy(listOfReportDB.getUpdateBy());
			LocalDateTime updateeDate = listOfReportDB.getUpdateDate();
			Timestamp convertedUpdateDate = Timestamp.valueOf(updateeDate);
			rddDownLocDto.setUpdateDate(convertedUpdateDate);
			if (StringUtils.isNotEmpty(listOfReportDB.getStatus())
					&& listOfReportDB.getStatus().equalsIgnoreCase("Success")) {
				rddDownLocDto.setDownLoc(listOfReportDB.getDownLoc());
			}
			finalReports.add(rddDownLocDto);
		}
		return new ResponseEntity<>(finalReports, HttpStatus.OK);
	}

	/**
	 * Fetch report and status.
	 *
	 * @return the response entity
	 */
	@GetMapping("/reportNamesAndStatus")
	public ResponseEntity<ReportStatusInformation> fetchReportAndStatus() {
		ReportStatusInformation reportStatusInformation = new ReportStatusInformation();
		reportStatusInformation = ondemandDownRptService.fetchReportAndStatus(reportStatusInformation);
		return new ResponseEntity<>(reportStatusInformation, HttpStatus.OK);
	}

	/**
	 * Download.
	 *
	 * @param filePath the file path
	 * @return the response entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GetMapping("/reportDownload")
	public ResponseEntity<InputStreamResource> download(@RequestParam String filePath) throws IOException {
		String filename = fileNameFromFilePath(filePath);
		File filePathlocation = new File(filePath);
		if (StringUtils.isEmpty(filename) || filename.equalsIgnoreCase("null")) {
			throw new MyResourceNotFoundException(ConstantUtils.FILE_NOT_FOUND);
		}
		InputStreamResource resource = new InputStreamResource(new FileInputStream(filePathlocation));
		HttpHeaders headers = new HttpHeaders();
		headers.add("file_name", filename);
		headers.add("Content-Disposition", "attachment; filename=" + filename);
		return ResponseEntity.ok().headers(headers).contentLength(filePathlocation.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	/**
	 * File name from file path.
	 *
	 * @param filePath the file path
	 * @return the string
	 */
	private String fileNameFromFilePath(String filePath) {
		String filename = "";
		if (StringUtils.isNoneBlank(filePath)) {
			String[] sytringArray = filePath.split("/");
			filename = sytringArray[sytringArray.length - 1];
		}
		return filename;
	}

}
