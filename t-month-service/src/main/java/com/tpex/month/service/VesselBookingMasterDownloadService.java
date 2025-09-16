package com.tpex.month.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.tpex.month.model.dto.BorderColor;
import com.tpex.month.model.dto.BorderCustomStyle;
import com.tpex.month.model.dto.CellStyleCustom;
import com.tpex.month.model.dto.CommonPaginationRequest;
import com.tpex.month.model.dto.FileItem;
import com.tpex.month.model.dto.FontStyle;
import com.tpex.month.model.dto.MixedVesselBooking;
import com.tpex.month.model.dto.VesselBookingMasterSearch;
import com.tpex.month.model.dto.VesselBookingMasterSearchRequest;
import com.tpex.month.model.entity.FinalDestinationMasterEntity;
import com.tpex.month.model.repository.FinalDestinationMasterRepository;
import com.tpex.month.util.ConstantUtil;
import com.tpex.month.util.TpexConfigUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class VesselBookingMasterDownloadService {

	private final VesselBookingMasterSearchService vesselBookingMasterSearchService;
	private final FinalDestinationMasterRepository finalDestinationMasterRepository;
	private final TpexConfigUtils tpexConfigUtils;

	public Map<String, Object> downloadVesselBookingMaster(VesselBookingMasterSearchRequest request) {
		Map<String, Object> map = new HashMap<>();

		CommonPaginationRequest<VesselBookingMasterSearchRequest> common = new CommonPaginationRequest<>();
		common.setRequestBody(request);
		common.setPagination(null);

		String limitStr = tpexConfigUtils.getConfigValue(ConstantUtil.VESSEL_BOOK_LIMIT);

		List<VesselBookingMasterSearch> content = vesselBookingMasterSearchService.searchVesselBookingMaster(common)
				.getContent();

		List<FileItem> listFile = prepareExcelFile(request, content);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		String zipName = "VesselBookingMaster_"
				+ request.getVanningMonth().replace(ConstantUtil.SLASH, ConstantUtil.BLANK) + "_" + dtf.format(now)
				+ ".zip";
		if (content.size() > Integer.parseInt(limitStr)) {
			String path = tpexConfigUtils.getConfigValue(ConstantUtil.VESSEL_BOOK_PATH);
			// comment for zip only download remove when change request from ui side done
			FileItem zipFile = zipFile(listFile, zipName);
			saveOnDisk(path, zipFile);

			map.put(ConstantUtil.MSG, ConstantUtil.INFO_MN_4001);
			map.put(ConstantUtil.STATUS, ConstantUtil.OFFLINE);
		} else {

			FileItem zipFile = zipFile(listFile, zipName);
			map.put("outStream", zipFile.getContent());
			map.put("filename", zipFile.getFilename());
		}

		return map;
	}

	private List<FileItem> prepareExcelFile(VesselBookingMasterSearchRequest request,
			List<VesselBookingMasterSearch> content) {
		List<FileItem> listFileItem = new ArrayList<>();

		// generate each file for destination code and shipping company
		List<MixedVesselBooking> groupFileList = content.stream()
				.map(c -> new MixedVesselBooking(c.getDestinationCode(), c.getShippingCompany())).distinct()
				.collect(Collectors.toList());
		for (MixedVesselBooking group : groupFileList) {
			List<VesselBookingMasterSearch> filteredShipComp = content.stream()
					.filter(c -> c.getShippingCompany().equals(group.getShippingCompanyCode())
							&& c.getDestinationCode().equals(group.getDestinationCode()))
					.collect(Collectors.toList());

			Optional<FinalDestinationMasterEntity> opt = finalDestinationMasterRepository
					.findById(group.getDestinationCode());
			String fdDstNm = opt.isPresent() ? opt.get().getFdDstNm() : "";

			listFileItem.add(generateExcelFile(filteredShipComp,
					"Vessel Booking " + group.getDestinationCode() + " " + fdDstNm + " "
							+ request.getVanningMonth().replace(ConstantUtil.SLASH, ConstantUtil.BLANK) + " "
							+ group.getShippingCompanyCode() + ".xlsx"));
		}
		return listFileItem;
	}

	private FileItem zipFile(List<FileItem> listFile, String zipFileName) {
		FileItem fileItem = new FileItem();
		fileItem.setFilename(zipFileName);

		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try (ZipOutputStream zipOut = new ZipOutputStream(os);) {

				for (FileItem item : listFile) {
					byte[] data = item.getContent();
					ZipEntry entry = new ZipEntry(item.getFilename());
					entry.setSize(data.length);
					zipOut.putNextEntry(entry);
					zipOut.write(data);
					zipOut.closeEntry();
				}

			} finally {
				fileItem.setContent(os.toByteArray());
				os.close();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return fileItem;
	}

	private FileItem generateExcelFile(List<VesselBookingMasterSearch> content, String fileName) {
		String[] headerVal = { "Vanning Month", "Destination Code", "ETD1", "Final ETA", "RENBAN Code",
				"No. of Container", "", "Ship. Comp.", "Custom Broker", "Booking No.", "Vessel1", "Booking Status" };

		String[] headerValRow2 = { "", "", "", "", "", "20'", "40'", "", "", "", "", "" };

		String[] dataFormatVal = { "-", "-", "-", "-", "-", "-", "-", "-", "xxx(3)", "xxx..(15)", "xxx..(30)", "-" };

		int[] columnWidth = { 4500, 4000, 3500, 3500, 7000, 3500, 3500, 3000, 4000, 4500, 7000, 4500 };

		int rowIdx = 0;

		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = workbook.createSheet("Data");

		setColumnWidth(sheet, columnWidth);

		// header, data format row
		Row header = sheet.createRow(rowIdx++);
		header.setHeightInPoints(22.5F);
		Row headerRow2 = sheet.createRow(rowIdx++);
		headerRow2.setHeightInPoints(22.5F);
		Row dataFormat = sheet.createRow(rowIdx++);
		dataFormat.setHeightInPoints(22.5F);

		// red, green, blue
		byte[] rgb = { (byte) 237, (byte) 251, (byte) 255 };
		XSSFColor bgColor = new XSSFColor(rgb);

		rgb = new byte[] { (byte) 169, (byte) 235, (byte) 255 };
		XSSFColor xHBdColor = new XSSFColor(rgb);

		FontStyle headerFont = new FontStyle(10, true, ConstantUtil.ARIAL_FONT);
		BorderColor headerBdColor = new BorderColor(xHBdColor, xHBdColor, xHBdColor, xHBdColor);
		BorderCustomStyle headerBdStyle = new BorderCustomStyle(BorderStyle.THICK, BorderStyle.THICK, BorderStyle.THICK,
				BorderStyle.THICK, headerBdColor);

		CellStyle headerStyle = getCellStyle(workbook, new CellStyleCustom(bgColor, headerFont,
				HorizontalAlignment.LEFT, VerticalAlignment.CENTER, headerBdStyle, false));
		CellStyle dataFormatStyle = getCellStyle(workbook, new CellStyleCustom(bgColor, headerFont,
				HorizontalAlignment.CENTER, VerticalAlignment.CENTER, headerBdStyle, false));

		prepareHeader(header, headerStyle, headerVal);
		prepareHeader(headerRow2, headerStyle, headerValRow2);
		prepareHeader(dataFormat, dataFormatStyle, dataFormatVal);
		mergeHeader(sheet);

		rgb = new byte[] { (byte) 151, (byte) 185, (byte) 248 };
		XSSFColor xDetLRBdColor = new XSSFColor(rgb);

		rgb = new byte[] { (byte) 218, (byte) 218, (byte) 218 };
		XSSFColor xDetBotBdColor = new XSSFColor(rgb);

		BorderColor detBdColor = new BorderColor(null, xDetBotBdColor, xDetLRBdColor, xDetLRBdColor);
		BorderCustomStyle detBdStyle = new BorderCustomStyle(BorderStyle.NONE, BorderStyle.MEDIUM, BorderStyle.MEDIUM,
				BorderStyle.MEDIUM, detBdColor);

		CellStyle leftStyle = getCellStyle(workbook,
				new CellStyleCustom(null, null, HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM, detBdStyle, false));

		for (VesselBookingMasterSearch data : content) {
			Row rowData = sheet.createRow(rowIdx++);
			rowData.setHeightInPoints(22.5F);

			for (int j = 0; j < headerVal.length; j++) {
				Cell cellData = rowData.createCell(j);

				switch (j) {
				case 0:
					cellData.setCellValue(data.getVanningMonth());
					cellData.setCellStyle(leftStyle);
					break;
				case 1:
					cellData.setCellValue(data.getDestinationCode());
					cellData.setCellStyle(leftStyle);
					break;
				case 2:
					cellData.setCellValue(data.getEtd1());
					cellData.setCellStyle(leftStyle);
					break;
				case 3:
					cellData.setCellValue(data.getFinalEta());
					cellData.setCellStyle(leftStyle);
					break;
				case 4:
					cellData.setCellValue(data.getRenbanCode());
					cellData.setCellStyle(leftStyle);
					break;
				case 5:
					cellData.setCellValue(data.getNoOfContainer20ft());
					cellData.setCellStyle(leftStyle);
					break;
				case 6:
					cellData.setCellValue(data.getNoOfContainer40ft());
					cellData.setCellStyle(leftStyle);
					break;
				case 7:
					cellData.setCellValue(data.getShippingCompany());
					cellData.setCellStyle(leftStyle);
					break;
				case 8:
					cellData.setCellValue(data.getCustomBrokerCode());
					cellData.setCellStyle(leftStyle);
					break;
				case 9:
					cellData.setCellValue(data.getBookingNo());
					cellData.setCellStyle(leftStyle);
					break;
				case 10:
					cellData.setCellValue(data.getVessel1());
					cellData.setCellStyle(leftStyle);
					break;
				case 11:
					cellData.setCellValue(data.getBookingStatus());
					cellData.setCellStyle(leftStyle);
					break;
				default:
					break;
				}
			}
		}

		String path = tpexConfigUtils.getConfigValue(ConstantUtil.VESSEL_BOOK_PATH);

		if (!Files.exists(Paths.get(path))) {
			try {
				Files.createDirectories(Paths.get(path));
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			workbook.write(outputStream);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				workbook.close();
				outputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		return new FileItem(fileName, outputStream.toByteArray());
	}

	private void setColumnWidth(Sheet sheet, int[] columnWidth) {
		for (int i = 0; i < columnWidth.length; i++) {
			sheet.setColumnWidth(i, columnWidth[i]);
		}
	}

	private void prepareHeader(Row header, CellStyle headerStyle, String[] value) {
		for (int i = 0; i < value.length; i++) {
			Cell headerCell = header.createCell(i);
			headerCell.setCellValue(value[i]);
			headerCell.setCellStyle(headerStyle);
		}
	}

	private void mergeHeader(Sheet sheet) {
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 4, 4));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 6));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 7, 7));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 8, 8));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 9, 9));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 10, 10));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 11, 11));
	}

	private CellStyle getCellStyle(XSSFWorkbook workbook, CellStyleCustom csc) {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		if (csc.getBgColor() != null) {
			cellStyle.setFillForegroundColor(csc.getBgColor());
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		cellStyle.setAlignment(csc.getHorizontalAlignment());
		cellStyle.setVerticalAlignment(csc.getVerticalAlignment());
		setBorderStyle(csc, cellStyle);
		cellStyle.setWrapText(csc.isWrapText());

		XSSFFont font = workbook.createFont();
		if (csc.getFontStyle() == null) {
			FontStyle defaultFont = new FontStyle(10, false, ConstantUtil.ARIAL_FONT);
			csc.setFontStyle(defaultFont);
		}
		font.setFontName(csc.getFontStyle().getFontFamily());
		font.setFontHeightInPoints((short) csc.getFontStyle().getFontSize());
		font.setBold(csc.getFontStyle().isBold());
		cellStyle.setFont(font);
		return cellStyle;
	}

	private void setBorderStyle(CellStyleCustom csc, XSSFCellStyle cellStyle) {
		if (csc.getBorderStyle() != null) {
			BorderCustomStyle bdStyle = csc.getBorderStyle();
			cellStyle.setBorderLeft(bdStyle.getLeftStyle());
			cellStyle.setBorderTop(bdStyle.getTopStyle());
			cellStyle.setBorderRight(bdStyle.getRightStyle());
			cellStyle.setBorderBottom(bdStyle.getBottomStyle());

			BorderColor bdColor = bdStyle.getBorderColor();
			if (bdColor != null) {
				if (bdStyle.getBorderColor().getTop() != null) {
					cellStyle.setTopBorderColor(bdStyle.getBorderColor().getTop());
				}
				if (bdStyle.getBorderColor().getLeft() != null) {
					cellStyle.setLeftBorderColor(bdStyle.getBorderColor().getLeft());
				}
				if (bdStyle.getBorderColor().getRight() != null) {
					cellStyle.setRightBorderColor(bdStyle.getBorderColor().getRight());
				}
				if (bdStyle.getBorderColor().getBottom() != null) {
					cellStyle.setBottomBorderColor(bdStyle.getBorderColor().getBottom());
				}
			}
		}
	}

	private void saveOnDisk(String path, FileItem fileItem) {
		// if exists concat (n) prefix
		int num = 0;
		String fileName = fileItem.getFilename();
		String fileNameNoExt = fileItem.getFilename().substring(0, fileItem.getFilename().lastIndexOf("."));
		String ext = fileName.substring(fileName.lastIndexOf("."));
		File file = new File(path, fileItem.getFilename());
		while (file.exists()) {
			fileName = fileNameNoExt + " (" + (++num) + ")" + ext;
			file = new File(path, fileName);
		}

		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(fileItem.getContent());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
