package com.tpex.month.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tpex.month.exception.InvalidFileException;
import com.tpex.month.serviceimpl.CommonUploadAndDownloadServiceImpl;
import com.tpex.month.util.ConstantUtil;
import com.tpex.month.util.TpexConfigUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VesselBookingMasterUploadService {

	private final TpexConfigUtils tpexConfigUtils;
	private final CommonUploadAndDownloadServiceImpl commonUploadAndDownloadServiceImpl;
	private final FileService fileService;
	
	public ResponseEntity<Object> uploadVesselBookingMaster(String batchName, MultipartFile file,String userId) throws IOException {

		Workbook workbook = tpexConfigUtils.getWorkbook(file);
		Sheet worksheet = workbook.getSheetAt(0);

		if (worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1
				|| tpexConfigUtils.checkForEmptySheet(worksheet, 2)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtil.ERR_AD_2001);
		}

		
		List<String> header = Arrays.asList("Vanning Month", "Destination", "ETD1", "Final ETA", "Container Code(s)",
				"No. of 20 ft. Container(s)", "No. of 40 ft. Container(s)", "Ship. Comp.", "Custom Broker",
				"Booking No.", "Vessel1", "Booking Status");
		List<String> header2 = Arrays.asList("-", "-", "-", "-", "-", "-", "-", "-", "xxx(3)", "xxx..(15)", "xxx..(30)",
				"-");

		if (!tpexConfigUtils.checkForValidHeader(worksheet, header, 0)
				|| !tpexConfigUtils.checkForValidHeader(worksheet, header2, 1)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtil.ERR_CM_3009);
		}
		workbook.close();

		fileService.save(file);

		commonUploadAndDownloadServiceImpl.saveProcessDetails(userId, batchName);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
