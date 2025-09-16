package com.tpex.invoice.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.entity.InhouseShopMasterEntity;
import com.tpex.entity.PartMasterEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InhouseDropdownResponseDTO;
import com.tpex.invoice.dto.InhouseShopDTO;
import com.tpex.invoice.dto.PartMasterModifyRequestDTO;
import com.tpex.invoice.dto.PartMasterRequestDTO;
import com.tpex.invoice.dto.PartMasterSearchRequestDto;
import com.tpex.invoice.dto.PartMasterSearchResponseDto;
import com.tpex.invoice.service.PartMasterService;
import com.tpex.repository.InhouseShopMasterRepo;
import com.tpex.repository.PartMasterDao;
import com.tpex.repository.PartMasterRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.validator.PartMasterRequestValidatior;

/**
 * The Class PartPriceMasterServiceImpl.
 */
@Service
public class PartMasterServiceImpl implements PartMasterService {

	/** The part master repository. */
	@Autowired
	private PartMasterDao partMasterDao;

	@Autowired
	private PartMasterRepository partMasterRepository;

	@Autowired
	private InhouseShopMasterRepo inhouseShopRepo;

	/**
	 * Part master search list.
	 *
	 * @param partPriceMasterRequestDto the part price master request dto
	 * @return the part price master response dto
	 * @throws ParseException the parse exception
	 */
	@Override
	public List<PartMasterSearchResponseDto> search(PartMasterSearchRequestDto partMasterSearchRequestDto) {

		List<PartMasterSearchResponseDto> partMasterSearchResponseDtoList;
		List<PartMasterEntity> partMasterEntities = partMasterDao.search(partMasterSearchRequestDto);

		partMasterSearchResponseDtoList = partMasterEntities.stream()
				.map(m -> new PartMasterSearchResponseDto(m.getPartNo(), m.getPartName(), m.getType(),
						m.getInhouseShop(), m.getWeight()))
				.collect(Collectors.toList());

		return partMasterSearchResponseDtoList;
	}

	@Override
	public byte[] download(HttpServletResponse reponse, PartMasterSearchRequestDto partMasterSearchRequestDto)
			throws IOException, DecoderException, ParseException, NullPointerException {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet partMaster = workbook.createSheet("Part Master");
			Sheet listPartType = workbook.createSheet("LIST_PART_TYPE");
			Sheet listInhouseShop = workbook.createSheet("LIST_INHOUSE_SHOP");

			String headerRgbS = ConstantUtils.CAR_FMLY_DEST_HEADER_COLOR_CODE;
			byte[] headerRgbB = Hex.decodeHex(headerRgbS);
			XSSFColor headerColor = new XSSFColor(headerRgbB, null);

			String headerBorderRgbS = ConstantUtils.CAR_FMLY_DEST_HEADER_BORDER_COLOR_CODE;
			byte[] headerBorderRgbB = Hex.decodeHex(headerBorderRgbS);
			XSSFColor headerBorderColor = new XSSFColor(headerBorderRgbB, null);

			Font partMasterFont = workbook.createFont();
			partMasterFont.setFontName(ConstantUtils.ARIAL);
			partMasterFont.setFontHeightInPoints((short) 10);
			partMasterFont.setBold(true);

			XSSFCellStyle partMasterStyle = (XSSFCellStyle) workbook.createCellStyle();
			partMasterStyle.setFillForegroundColor(headerColor);
			partMasterStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			partMasterStyle.setBorderBottom(BorderStyle.THIN);
			partMasterStyle.setBorderLeft(BorderStyle.THIN);
			partMasterStyle.setBorderRight(BorderStyle.THIN);
			partMasterStyle.setBorderTop(BorderStyle.THIN);
			partMasterStyle.setFont(partMasterFont);
			partMasterStyle.setAlignment(HorizontalAlignment.CENTER);
			partMasterStyle.setBorderColor(BorderSide.RIGHT, headerBorderColor);
			partMasterStyle.setBorderColor(BorderSide.LEFT, headerBorderColor);
			partMasterStyle.setBorderColor(BorderSide.TOP, headerBorderColor);
			partMasterStyle.setBorderColor(BorderSide.BOTTOM, headerBorderColor);

			Font partMasterFont1 = workbook.createFont();
			partMasterFont1.setFontName(ConstantUtils.ARIAL);
			partMasterFont1.setFontHeightInPoints((short) 10);

			XSSFCellStyle partMasterStyle1 = (XSSFCellStyle) workbook.createCellStyle();
			partMasterStyle1.setFillForegroundColor(headerColor);
			partMasterStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			partMasterStyle1.setBorderBottom(BorderStyle.THIN);
			partMasterStyle1.setBorderLeft(BorderStyle.THIN);
			partMasterStyle1.setBorderRight(BorderStyle.THIN);
			partMasterStyle1.setBorderTop(BorderStyle.THIN);
			partMasterStyle1.setFont(partMasterFont1);
			partMasterStyle1.setAlignment(HorizontalAlignment.CENTER);
			partMasterStyle1.setBorderColor(BorderSide.RIGHT, headerBorderColor);
			partMasterStyle1.setBorderColor(BorderSide.LEFT, headerBorderColor);
			partMasterStyle1.setBorderColor(BorderSide.TOP, headerBorderColor);
			partMasterStyle1.setBorderColor(BorderSide.BOTTOM, headerBorderColor);

			Font gridFont = workbook.createFont();
			gridFont.setFontName(ConstantUtils.ARIAL);
			gridFont.setFontHeightInPoints((short) 10);
			gridFont.setBold(false);

			XSSFCellStyle gridDataStyle = (XSSFCellStyle) workbook.createCellStyle();
			gridDataStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			gridDataStyle.setFillPattern(FillPatternType.NO_FILL);
			gridDataStyle.setFont(gridFont);

			gridDataStyle.setBorderBottom(BorderStyle.THIN);
			gridDataStyle.setBorderLeft(BorderStyle.THIN);
			gridDataStyle.setBorderRight(BorderStyle.THIN);
			gridDataStyle.setBorderTop(BorderStyle.THIN);
			gridDataStyle.setBorderColor(BorderSide.RIGHT, headerBorderColor);
			gridDataStyle.setBorderColor(BorderSide.LEFT, headerBorderColor);
			gridDataStyle.setBorderColor(BorderSide.TOP, headerBorderColor);
			gridDataStyle.setBorderColor(BorderSide.BOTTOM, headerBorderColor);

			partMaster.setDisplayGridlines(true);
			listPartType.setDisplayGridlines(true);
			listInhouseShop.setDisplayGridlines(true);

			Row partMasterrow = partMaster.createRow((short) 0);
			partMasterrow.createCell(0).setCellValue("Part No.");
			partMasterrow.getCell(0).setCellStyle(partMasterStyle);
			partMasterrow.createCell(1).setCellValue("Part Name");
			partMasterrow.getCell(1).setCellStyle(partMasterStyle);
			partMasterrow.createCell(2).setCellValue("Part Type");
			partMasterrow.getCell(2).setCellStyle(partMasterStyle);
			partMasterrow.createCell(3).setCellValue("Inhouse Shop");
			partMasterrow.getCell(3).setCellStyle(partMasterStyle);
			partMasterrow.createCell(4).setCellValue("Part Weight (KG)");
			partMasterrow.getCell(4).setCellStyle(partMasterStyle);

			Row partMasterrow1 = partMaster.createRow(1);
			partMasterrow1.createCell(0).setCellValue("'xx..(12)/key");
			partMasterrow1.getCell(0).setCellStyle(partMasterStyle1);
			partMasterrow1.createCell(1).setCellValue("'xx..(40)");
			partMasterrow1.getCell(1).setCellStyle(partMasterStyle1);
			partMasterrow1.createCell(2).setCellValue("");
			partMasterrow1.getCell(2).setCellStyle(partMasterStyle1);
			partMasterrow1.createCell(3).setCellValue("");
			partMasterrow1.getCell(3).setCellStyle(partMasterStyle1);
			partMasterrow1.createCell(4).setCellValue("'xx..(3,8)");
			partMasterrow1.getCell(4).setCellStyle(partMasterStyle1);

			Sheet sheet = workbook.getSheetAt(0);
			if (!StringUtils.isEmpty(partMasterSearchRequestDto.getPartNo())
					|| !StringUtils.isEmpty(partMasterSearchRequestDto.getPartName())
					|| !StringUtils.isEmpty(partMasterSearchRequestDto.getPartType())) {
				List<PartMasterSearchResponseDto> listt = search(partMasterSearchRequestDto);

				int rowNo = 2;
				int columnIndex = 0;
				for (PartMasterSearchResponseDto m : listt) {
					Row row = sheet.createRow(rowNo);
					row.createCell(0).setCellValue(m.getPartNo());
					row.getCell(0).setCellStyle(gridDataStyle);
					row.createCell(1).setCellValue(m.getPartName());
					row.getCell(1).setCellStyle(gridDataStyle);
					row.createCell(2).setCellValue(m.getPartType());
					row.getCell(2).setCellStyle(gridDataStyle);
					row.createCell(3).setCellValue(m.getInhouseShop());
					row.getCell(3).setCellStyle(gridDataStyle);
					row.createCell(4).setCellValue(m.getPartWeight());
					row.getCell(4).setCellStyle(gridDataStyle);
					rowNo++;
					partMaster.autoSizeColumn(columnIndex);
					columnIndex++;
				}

			}

			Sheet sheet1 = workbook.getSheetAt(1);
			sheet1.createRow(1).createCell(0).setCellValue("1 - Local");
			sheet1.createRow(2).createCell(0).setCellValue("2 - Import");
			sheet1.createRow(3).createCell(0).setCellValue("3 - Inhouse");
			sheet1.createRow(4).createCell(0).setCellValue("4 - JSP");

			Sheet sheet2 = workbook.getSheetAt(2);
			List<InhouseShopMasterEntity> findAll = inhouseShopRepo.findAll();
			List<String> inshopList = null;
			inshopList = findAll.stream().map(m -> ( m.getInsShopCd()+ "-" + m.getDescription())).collect(Collectors.toList());
			int rowNo2 = 1;
			for (InhouseShopMasterEntity r : findAll) {
				Row row = sheet2.createRow(rowNo2);
				row.createCell(0).setCellValue(r.getInsShopCd() + "-" + r.getDescription());
				rowNo2++;
			}
			
			XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper( (XSSFSheet) listPartType);
			XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
					.createExplicitListConstraint(new String[] {"1 - Local","2 - Import","3 - Inhouse", "4 - JSP"});

			int cOOMasterColumnValue = 2;
			CellRangeAddressList addressList = new CellRangeAddressList(partMaster.getFirstRowNum() + 2,
					50, cOOMasterColumnValue, cOOMasterColumnValue);
			XSSFDataValidation datavalidation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint,
					addressList);
			datavalidation.setShowErrorBox(true);
			partMaster.addValidationData(datavalidation);		
			
			listPartType.setSelected(true);
			Sheet inHousehidden = workbook.createSheet(ConstantUtils.IN_HOUSE_HIDDEN_FILE);
			for (int i = 0, length = inshopList.size(); i < length; i++) {
				String name = inshopList.get(i);
				Row row = inHousehidden.createRow(i);
				Cell cell = row.createCell(0);
				cell.setCellValue(name);
			}

			Name namedCell = workbook.createName();
			namedCell.setNameName(ConstantUtils.IN_HOUSE_HIDDEN_FILE);
			namedCell.setRefersToFormula("inHousehidden!$A$1:$A$" + inshopList.size());
			XSSFDataValidationHelper inHouseDvHelper = new XSSFDataValidationHelper((XSSFSheet) inHousehidden);
			XSSFDataValidationConstraint inHouseConstraint = (XSSFDataValidationConstraint) inHouseDvHelper
					.createFormulaListConstraint(ConstantUtils.IN_HOUSE_HIDDEN_FILE);
			workbook.setSheetVisibility(3, SheetVisibility.VERY_HIDDEN);

			int partMasterColumnValue = 3;
			CellRangeAddressList inhouseAddressList = new CellRangeAddressList(partMaster.getFirstRowNum() + 2, 50,
					partMasterColumnValue, partMasterColumnValue);
			XSSFDataValidation inHouseDatavalidation = (XSSFDataValidation) inHouseDvHelper.createValidation(inHouseConstraint,
					inhouseAddressList);
			inHouseDatavalidation.setShowErrorBox(true);
			partMaster.addValidationData(inHouseDatavalidation);

			listPartType.setSelected(false);
			

			workbook.setActiveSheet(0);
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.setSelected(true);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			return bos.toByteArray();

		}
	}

	@Override
	public int deletePartMaster(PartMasterRequestDTO partMasterRequestDTO) {
		return partMasterDao.deletePartMaster(partMasterRequestDTO);

	}

	@Override
	public void savePartMaster(PartMasterModifyRequestDTO partMasterSaveRequestDTO) {

		List<PartMasterEntity> partMasterList = partMasterSaveRequestDTO.getData();
		long countOfPartNo = 0;
		Map<String, Object> errorMessageParams = new HashMap<>();
		// Validate duplicate in input pay load list
		List<String> duplicateColumnValues = PartMasterRequestValidatior.validateDuplicatePartNumber(partMasterList);
		if (duplicateColumnValues.isEmpty()) {
			for (PartMasterEntity partMasterSaveDTO : partMasterList) {
				countOfPartNo = partMasterRepository.countByPartNo(partMasterSaveDTO.getPartNo());

				if (countOfPartNo > 0) {
					duplicateColumnValues.add(partMasterSaveDTO.getPartNo());
				}
			}
		}
		if (!duplicateColumnValues.isEmpty()) {
			errorMessageParams.put(ConstantUtils.KEY_COLUMNS, duplicateColumnValues);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3008, errorMessageParams);
		} else {
			partMasterRepository.saveAll(partMasterList);
		}
	}

	@Override
	public void updatePartMaster(PartMasterModifyRequestDTO partMasterModifyRequestDTO) {
		partMasterDao.updatePartMaster(partMasterModifyRequestDTO.getData());

	}

	@Override
	public InhouseDropdownResponseDTO getInhouseDropdownData(String companyCode) {

		InhouseDropdownResponseDTO inhouseDropdownResponseDTO = new InhouseDropdownResponseDTO();

		List<InhouseShopDTO> inhouseDropdownList = partMasterDao.getInhouseDropdownData(companyCode);

		inhouseDropdownResponseDTO.setInhouseDropdown(inhouseDropdownList);

		return inhouseDropdownResponseDTO;
	}

}
