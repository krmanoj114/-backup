package com.tpex.invoice.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tpex.invoice.service.DownloadInvCntnrLvlReportService;
import com.tpex.repository.InsInvContainerDtlsRepository;
import com.tpex.util.ConstantUtils;

@Service
public class DownloadInvCntnrLvlReportServiceImpl implements DownloadInvCntnrLvlReportService {
	
	@Value("${upload.path}")
	private String fileUploadPath;
	
	@Autowired
	InsInvContainerDtlsRepository insInvContainerDtlsRepository;
	
	public byte[] generateContainerLevelReport(String reportId,
			String reportFormate, String etd, String[] destination) throws IOException, ParseException {
		
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet invoiceCntrLvlReport = workbook.createSheet(ConstantUtils.INVOICE_DATA_DOWNLOAD);
			
			Font cooMasterFont = workbook.createFont();
			cooMasterFont.setFontName(ConstantUtils.CALIBRIFONT);
			cooMasterFont.setFontHeightInPoints((short) 12);
			cooMasterFont.setBold(false);

			CellStyle cooMasterStyle = workbook.createCellStyle();

			cooMasterStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
			cooMasterStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cooMasterStyle.setBorderBottom(BorderStyle.THIN);
			cooMasterStyle.setBorderLeft(BorderStyle.THIN);
			cooMasterStyle.setBorderRight(BorderStyle.THIN);
			cooMasterStyle.setBorderTop(BorderStyle.THIN);
			cooMasterStyle.setFont(cooMasterFont);
			cooMasterStyle.setAlignment(HorizontalAlignment.CENTER);
			
			Font countryCodeFont = workbook.createFont();
			countryCodeFont.setFontName(ConstantUtils.ARIALFONT);
			countryCodeFont.setFontHeightInPoints((short) 11);
			countryCodeFont.setBold(true);

			CellStyle countryCodeStyle = workbook.createCellStyle();

			countryCodeStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			countryCodeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			countryCodeStyle.setBorderBottom(BorderStyle.THIN);
			countryCodeStyle.setBorderLeft(BorderStyle.THIN);
			countryCodeStyle.setBorderRight(BorderStyle.THIN);
			countryCodeStyle.setBorderTop(BorderStyle.THIN);
			countryCodeStyle.setFont(countryCodeFont);

			Font gridFont = workbook.createFont();
			gridFont.setFontName(ConstantUtils.CALIBRIFONT);
			gridFont.setFontHeightInPoints((short) 11);
			gridFont.setBold(false);

			CellStyle gridDataStyle = workbook.createCellStyle();

			gridDataStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			gridDataStyle.setFillPattern(FillPatternType.NO_FILL);
			gridDataStyle.setFont(gridFont);
			gridDataStyle.setBorderBottom(BorderStyle.THIN);
			gridDataStyle.setBorderLeft(BorderStyle.THIN);
			gridDataStyle.setBorderRight(BorderStyle.THIN);
			gridDataStyle.setBorderTop(BorderStyle.THIN);
			

			CreationHelper creationHelper = workbook.getCreationHelper();

			Font dateFont = workbook.createFont();
			dateFont.setFontName(ConstantUtils.CALIBRIFONT);
			dateFont.setFontHeightInPoints((short) 11);
			dateFont.setBold(false);

			CellStyle cellstyleforDate = workbook.createCellStyle();

			cellstyleforDate.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			cellstyleforDate.setFillPattern(FillPatternType.NO_FILL);
			cellstyleforDate.setDataFormat(creationHelper.createDataFormat().getFormat(ConstantUtils.POIDATEFORMATE));
			cellstyleforDate.setFont(dateFont);
			cellstyleforDate.setBorderBottom(BorderStyle.THIN);
			cellstyleforDate.setBorderLeft(BorderStyle.THIN);
			cellstyleforDate.setBorderRight(BorderStyle.THIN);
			cellstyleforDate.setBorderTop(BorderStyle.THIN);
			
			
			Font dateFontVanMonth = workbook.createFont();
			dateFont.setFontName(ConstantUtils.ARIALFONT);
			dateFont.setFontHeightInPoints((short) 11);
			dateFont.setBold(false);

			CellStyle cellstyleforVanMonth = workbook.createCellStyle();

			cellstyleforVanMonth.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			cellstyleforVanMonth.setFillPattern(FillPatternType.NO_FILL);
			cellstyleforVanMonth.setDataFormat(creationHelper.createDataFormat().getFormat(ConstantUtils.POI_VAN_MONTH_FORMATE));
			cellstyleforVanMonth.setFont(dateFontVanMonth);
			cellstyleforVanMonth.setBorderBottom(BorderStyle.THIN);
			cellstyleforVanMonth.setBorderLeft(BorderStyle.THIN);
			cellstyleforVanMonth.setBorderRight(BorderStyle.THIN);
			cellstyleforVanMonth.setBorderTop(BorderStyle.THIN);
		
			invoiceCntrLvlReport.setDisplayGridlines(false);
			
			Row invoiceCntrLvlReportRow = invoiceCntrLvlReport.createRow((short) 0);

			invoiceCntrLvlReportRow.createCell(0).setCellValue(ConstantUtils.IMPORTER_CODE);
			invoiceCntrLvlReportRow.getCell(0).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(1).setCellValue(ConstantUtils.EXPORTER_CODE);
			invoiceCntrLvlReportRow.getCell(1).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(2).setCellValue(ConstantUtils.MSP_ORDER_TYPE);
			invoiceCntrLvlReportRow.getCell(2).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(3).setCellValue(ConstantUtils.PACKING_MONTH);
			invoiceCntrLvlReportRow.getCell(3).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(4).setCellValue(ConstantUtils.CFC);
			invoiceCntrLvlReportRow.getCell(4).setCellStyle(cooMasterStyle);			
			invoiceCntrLvlReportRow.createCell(5).setCellValue(ConstantUtils.RE_EXPORTER_CODE);
			invoiceCntrLvlReportRow.getCell(5).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(6).setCellValue(ConstantUtils.INVOICE_PRIVILEGE);
			invoiceCntrLvlReportRow.getCell(6).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(7).setCellValue(ConstantUtils.CPO_SPO_RUNNING_NO);
			invoiceCntrLvlReportRow.getCell(7).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(8).setCellValue(ConstantUtils.VANNING_MONTH);
			invoiceCntrLvlReportRow.getCell(8).setCellStyle(cooMasterStyle);			
			invoiceCntrLvlReportRow.createCell(9).setCellValue(ConstantUtils.VANNING_PLANT);
			invoiceCntrLvlReportRow.getCell(9).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(10).setCellValue(ConstantUtils.VANNING_DATE);
			invoiceCntrLvlReportRow.getCell(10).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(11).setCellValue(ConstantUtils.ETD_DATE);
			invoiceCntrLvlReportRow.getCell(11).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(12).setCellValue(ConstantUtils.ETA_DATE);
			invoiceCntrLvlReportRow.getCell(12).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(13).setCellValue(ConstantUtils.HAISEN_NO);
			invoiceCntrLvlReportRow.getCell(13).setCellStyle(cooMasterStyle);			
			invoiceCntrLvlReportRow.createCell(14).setCellValue(ConstantUtils.INVOICE);
			invoiceCntrLvlReportRow.getCell(14).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(15).setCellValue(ConstantUtils.INVOICE_DATE);
			invoiceCntrLvlReportRow.getCell(15).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(16).setCellValue(ConstantUtils.AMOUNT_FOB_INVOICE);
			invoiceCntrLvlReportRow.getCell(16).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(17).setCellValue(ConstantUtils.ORDER_TYPE);
			invoiceCntrLvlReportRow.getCell(17).setCellStyle(cooMasterStyle);			
			invoiceCntrLvlReportRow.createCell(18).setCellValue(ConstantUtils.PACKING_PATTERN);
			invoiceCntrLvlReportRow.getCell(18).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(19).setCellValue(ConstantUtils.RENBAN_NO);
			invoiceCntrLvlReportRow.getCell(19).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(20).setCellValue(ConstantUtils.CONTAINER_SIZE);
			invoiceCntrLvlReportRow.getCell(20).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(21).setCellValue(ConstantUtils.ISO_CONTAINER_NO);
			invoiceCntrLvlReportRow.getCell(21).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(22).setCellValue(ConstantUtils.SEAL_NO);
			invoiceCntrLvlReportRow.getCell(22).setCellStyle(cooMasterStyle);			
			invoiceCntrLvlReportRow.createCell(23).setCellValue(ConstantUtils.QTY_MODULE_TOTAL);
			invoiceCntrLvlReportRow.getCell(23).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(24).setCellValue(ConstantUtils.QTY_MODULE_ROUNDUSE);
			invoiceCntrLvlReportRow.getCell(24).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(25).setCellValue(ConstantUtils.PART_BOX_MAT);
			invoiceCntrLvlReportRow.getCell(25).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(26).setCellValue(ConstantUtils.NET_WEIGHT);
			invoiceCntrLvlReportRow.getCell(26).setCellStyle(cooMasterStyle);			
			invoiceCntrLvlReportRow.createCell(27).setCellValue(ConstantUtils.GROSS_WEIGHT);
			invoiceCntrLvlReportRow.getCell(27).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(28).setCellValue(ConstantUtils.M3);
			invoiceCntrLvlReportRow.getCell(28).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(29).setCellValue(ConstantUtils.CONTAINER_FOB);
			invoiceCntrLvlReportRow.getCell(29).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(30).setCellValue(ConstantUtils.INVOICE_STATUS);
			invoiceCntrLvlReportRow.getCell(30).setCellStyle(cooMasterStyle);
			invoiceCntrLvlReportRow.createCell(31).setCellValue(ConstantUtils.DG_CONTAINER);
			invoiceCntrLvlReportRow.getCell(31).setCellStyle(cooMasterStyle);			
			invoiceCntrLvlReportRow.createCell(32).setCellValue(ConstantUtils.BOOKING);
			invoiceCntrLvlReportRow.getCell(32).setCellStyle(cooMasterStyle);

			for(int i =0 ; i<=32; i++) {
			
			invoiceCntrLvlReport.autoSizeColumn(i);
			
			}
			
			invoiceCntrLvlReport.setSelected(true);
			
			String additionalParamter = getReportfeildItems(etd, destination);
			
			List<String> listDestination = Arrays.asList(destination);
			
			SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);
			SimpleDateFormat sdf2 = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE_FOR_POI);
			String etdFormatted = null;

			if (etd != null) {
				etdFormatted = sdf2.format(sdf.parse(etd));
			}
			
			List<String[]> containerListData =   insInvContainerDtlsRepository.getDataForContinerList(etdFormatted, listDestination);
			
			int rowIndex = 1;
			int columnIndex = 0;
			
			for (String[] containerListDataInstance : containerListData) {
				Row dataRow = invoiceCntrLvlReport.createRow(rowIndex);
				dataRow.createCell(0).setCellValue(containerListDataInstance[0]);
				dataRow.getCell(0).setCellStyle(gridDataStyle);
				dataRow.createCell(1).setCellValue(containerListDataInstance[1]);
				dataRow.getCell(1).setCellStyle(gridDataStyle);
				dataRow.createCell(2).setCellValue(containerListDataInstance[2]);
				dataRow.getCell(2).setCellStyle(gridDataStyle);
				dataRow.createCell(3).setCellValue(containerListDataInstance[3]);
				dataRow.getCell(3).setCellStyle(gridDataStyle);
				dataRow.createCell(4).setCellValue(containerListDataInstance[4]);
				dataRow.getCell(4).setCellStyle(gridDataStyle);
				dataRow.createCell(5).setCellValue(containerListDataInstance[5]);
				dataRow.getCell(5).setCellStyle(gridDataStyle);
				dataRow.createCell(6).setCellValue(containerListDataInstance[6]);
				dataRow.getCell(6).setCellStyle(gridDataStyle);
				dataRow.createCell(7).setCellValue(containerListDataInstance[7]);
				dataRow.getCell(7).setCellStyle(gridDataStyle);
				dataRow.createCell(8).setCellValue(new SimpleDateFormat(ConstantUtils.SQL_VAN_MONTH).parse(containerListDataInstance[8] + ""));
				dataRow.getCell(8).setCellStyle(cellstyleforVanMonth);
				dataRow.createCell(9).setCellValue(containerListDataInstance[9]);
				dataRow.getCell(9).setCellStyle(gridDataStyle);
				dataRow.createCell(10).setCellValue(new SimpleDateFormat(ConstantUtils.SQL_DATE_FORMAT).parse(containerListDataInstance[10] + ""));
				dataRow.getCell(10).setCellStyle(cellstyleforDate);
				dataRow.createCell(11).setCellValue(new SimpleDateFormat(ConstantUtils.SQL_DATE_FORMAT).parse(containerListDataInstance[11] + ""));
				dataRow.getCell(11).setCellStyle(cellstyleforDate);
				dataRow.createCell(12).setCellValue(new SimpleDateFormat(ConstantUtils.SQL_DATE_FORMAT).parse(containerListDataInstance[12] + ""));
				dataRow.getCell(12).setCellStyle(cellstyleforDate);
				dataRow.createCell(13).setCellValue(containerListDataInstance[13]);
				dataRow.getCell(13).setCellStyle(gridDataStyle);
				dataRow.createCell(14).setCellValue(containerListDataInstance[14]);
				dataRow.getCell(14).setCellStyle(gridDataStyle);
				dataRow.createCell(15).setCellValue(new SimpleDateFormat(ConstantUtils.SQL_DATE_FORMAT).parse(containerListDataInstance[15] + ""));
				dataRow.getCell(15).setCellStyle(cellstyleforDate);
				dataRow.createCell(16).setCellValue(containerListDataInstance[16]);
				dataRow.getCell(16).setCellStyle(gridDataStyle);
				dataRow.createCell(17).setCellValue(containerListDataInstance[17]);
				dataRow.getCell(17).setCellStyle(gridDataStyle);
				dataRow.createCell(18).setCellValue(containerListDataInstance[18]);
				dataRow.getCell(18).setCellStyle(gridDataStyle);
				dataRow.createCell(19).setCellValue(containerListDataInstance[19]);
				dataRow.getCell(19).setCellStyle(gridDataStyle);
				dataRow.createCell(20).setCellValue(containerListDataInstance[20]);
				dataRow.getCell(20).setCellStyle(gridDataStyle);
				dataRow.createCell(21).setCellValue(containerListDataInstance[21]);
				dataRow.getCell(21).setCellStyle(gridDataStyle);
				dataRow.createCell(22).setCellValue(containerListDataInstance[22]);
				dataRow.getCell(22).setCellStyle(gridDataStyle);
				dataRow.createCell(23).setCellValue(containerListDataInstance[23]);
				dataRow.getCell(23).setCellStyle(gridDataStyle);
				dataRow.createCell(24).setCellValue(containerListDataInstance[24]);
				dataRow.getCell(24).setCellStyle(gridDataStyle);
				dataRow.createCell(25).setCellValue(containerListDataInstance[25]);
				dataRow.getCell(25).setCellStyle(gridDataStyle);
				dataRow.createCell(26).setCellValue(containerListDataInstance[26]);
				dataRow.getCell(26).setCellStyle(gridDataStyle);
				dataRow.createCell(27).setCellValue(containerListDataInstance[27]);
				dataRow.getCell(27).setCellStyle(gridDataStyle);
				dataRow.createCell(28).setCellValue(containerListDataInstance[28]);
				dataRow.getCell(28).setCellStyle(gridDataStyle);
				dataRow.createCell(29).setCellValue(containerListDataInstance[29]);
				dataRow.getCell(29).setCellStyle(gridDataStyle);
				dataRow.createCell(30).setCellValue(containerListDataInstance[30]);
				dataRow.getCell(30).setCellStyle(gridDataStyle);
				dataRow.createCell(31).setCellValue(containerListDataInstance[31]);
				dataRow.getCell(31).setCellStyle(gridDataStyle);
				dataRow.createCell(32).setCellValue(containerListDataInstance[32]);
				dataRow.getCell(32).setCellStyle(gridDataStyle);
				
				invoiceCntrLvlReport.autoSizeColumn(columnIndex);	
			
				columnIndex++;
				rowIndex++;
				}
			StringBuilder filePathConcat = new StringBuilder();
			filePathConcat.append(fileUploadPath);
			filePathConcat.append(ConstantUtils.SLASH);
			filePathConcat.append(ConstantUtils.INVOICE_CONTAINER_LEVEL_FILENAME);
			filePathConcat.append(additionalParamter);
			filePathConcat.append(ConstantUtils.INVOICE_CONTAINER_LEVEL_EXTENSION);
			
			FileOutputStream out = new FileOutputStream(filePathConcat.toString());
			workbook.write(out);  
	
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			return bos.toByteArray();					
		}
		
	}
	
	public String getReportfeildItems(String etd, String[] destination) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);
		SimpleDateFormat sdf2 = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE_FOR_POI);
		String etdFormatted = null;

		List<String> listDestination = Arrays.asList(destination);

		if (etd != null) {
			etdFormatted = sdf2.format(sdf.parse(etd));
		}

		return insInvContainerDtlsRepository.getReportNameDtls(etdFormatted, listDestination);

	}

}
