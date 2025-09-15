package com.tpex.invoice.serviceImpl;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.dto.FinalDestinationAndCarFamilyCodesDTO;
import com.tpex.dto.PartPriceMasterDeleteRequestDto;
import com.tpex.dto.PartPriceMasterDto;
import com.tpex.dto.PartPriceMasterRequestDto;
import com.tpex.dto.PartPriceMasterResponseDto;
import com.tpex.entity.OemCurrencyMstEntity;
import com.tpex.entity.PartMasterEntity;
import com.tpex.entity.PartPriceMasterEntity;
import com.tpex.entity.PartPriceMasterIdEntity;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.service.PartPriceMasterService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.InsInvPartsDetailsRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.repository.PartPriceMasterRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;
import com.tpex.util.TpexConfigurationUtil;

import net.sf.jasperreports.engine.JRException;

/**
 * The Class PartPriceMasterServiceImpl.
 */
@Service
public class PartPriceMasterServiceImpl implements PartPriceMasterService {

	/** The Constant YYYY_MM. */
	private static final String YYYY_MM ="yyyy/MM";

	/** The Constant YYYYMM. */
	private static final String YYYYMM ="yyyyMM";
	
	private static final String DDMMYYYY ="dd/MM/yyyy";
	
	private static final String YYYYMMDD ="yyyy-MM-dd";

	/** The Constant CONFIG. */
	private static final String CONFIG = "config";
	private static final String FILE_NAME = "fileName";
	private static final String FILE_TEMPLATE_NAME = "fileTemplateName";
	private static final String DATA_LIST = "dataList";
	private static final String PARAMETERS = "parameters";
	private static final String PART_NO = "partNo";

	/** The part price master repository. */
	@Autowired
	private PartPriceMasterRepository partPriceMasterRepository;

	/** The oem currency mst repository. */
	@Autowired
	private OemCurrencyMstRepository oemCurrencyMstRepository;

	/** The oem fnl dst mst repository. */
	@Autowired
	private OemFnlDstMstRepository oemFnlDstMstRepository;

	/** The car family mast repository. */
	@Autowired
	private CarFamilyMastRepository carFamilyMastRepository;

	/** The tpex config repository. */
	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	/** The tpex configuration util. */
	@Autowired
	private TpexConfigurationUtil tpexConfigurationUtil;

	/** The jasper report service. */
	@Autowired
	private JasperReportService jasperReportService;
	
	@Autowired
	private InsInvPartsDetailsRepository insInvPartsDetailsRepository;
	
	@Autowired
	private PartMasterRespository partMasterRespository;

	/** The df. */
	DecimalFormat df = new DecimalFormat("0.00");

	/**
	 * Part price master list.
	 *
	 * @param partPriceMasterRequestDto the part price master request dto
	 * @return the part price master response dto
	 * @throws ParseException the parse exception
	 */
	@Override
	public PartPriceMasterResponseDto partPriceMasterList(PartPriceMasterRequestDto partPriceMasterRequestDto) throws ParseException {
		String effMonth = (partPriceMasterRequestDto.getEffectiveMonth() == null || partPriceMasterRequestDto.getEffectiveMonth().isBlank()) 
				? null : DateUtil.getStringDate(partPriceMasterRequestDto.getEffectiveMonth(), YYYY_MM, YYYYMM);
		String partNum = (partPriceMasterRequestDto.getPartNo() == null || partPriceMasterRequestDto.getPartNo().isBlank()) ? null : partPriceMasterRequestDto.getPartNo().replace("-", "");
		List<PartPriceMasterEntity> partPriceMasterEntities = partPriceMasterRepository.findPartPriceDetails(partPriceMasterRequestDto.getCarFamilyCode(), 
				partPriceMasterRequestDto.getImporterCode(), effMonth, partNum, partPriceMasterRequestDto.getUserId());
		return getPartPriceMasterResponse(partPriceMasterEntities, oemCurrencyMstRepository.findAllByCompanyCodeOrderByCrmCdAsc(partPriceMasterRequestDto.getUserId()));
	}

	/**
	 * Gets the part price master response.
	 *
	 * @param partPriceMasterEntities the part price master entities
	 * @return the part price master response
	 */
	private PartPriceMasterResponseDto getPartPriceMasterResponse(List<PartPriceMasterEntity> partPriceMasterEntities, List<OemCurrencyMstEntity> oemCurrencyMstRepositories) {
		return new PartPriceMasterResponseDto(partPriceMasterEntities.stream().map(m -> {
			try {
				return new PartPriceMasterDto(m.getId().getCfCode()+m.getId().getDestCode()+m.getId().getEffFromMonth()+m.getId().getPartNo()+m.getId().getCurrencyCode(),
						m.getId().getCfCode(), m.getId().getDestCode(),
						DateUtil.getStringDate(m.getId().getEffFromMonth(), YYYYMM, YYYY_MM), 
						DateUtil.getStringDate(m.getEffToMonth(), YYYYMM, YYYY_MM), 
						String.format("%s-%s-%s", m.getId().getPartNo().substring(0,5), m.getId().getPartNo().substring(5,10), m.getId().getPartNo().substring(10)),
						m.getPartName(), new BigDecimal(df.format(m.getPartPrice())), m.getId().getCurrencyCode());
			} catch (ParseException e) {
				return null;
			}
		}).collect(Collectors.toList()),
				oemCurrencyMstRepositories.stream().map(c -> new CommonMultiSelectDropdownDto(c.getCrmCd(), c.getCrmCd() + "-" + c.getCrmDesc())).collect(Collectors.toList()));
	}

	/**
	 * Destination and carfamily codes.
	 *
	 * @param userId the user id
	 * @return the final destination and car family codes DTO
	 */
	@Override
	public FinalDestinationAndCarFamilyCodesDTO destinationAndCarfamilyCodes(String userId) {
		List<CommonMultiSelectDropdownDto> destCodeAndName = oemFnlDstMstRepository.findAllByCompanyCodeOrderByFdDstCdAsc(userId).stream()
				.map(u -> new CommonMultiSelectDropdownDto(u.getFdDstCd(), u.getFdDstCd() + "-" + u.getFdDstNm())).collect(Collectors.toList());

		List<CommonMultiSelectDropdownDto> carFmlyCodeAndName = carFamilyMastRepository.findAllByCompanyCodeOrderByCarFmlyCodeAsc(userId).stream()
				.map(u -> new CommonMultiSelectDropdownDto(u.getCarFmlyCode(), u.getCarFmlyCode() + "-" + u.getCarFmlySrsName())).collect(Collectors.toList());

		return new FinalDestinationAndCarFamilyCodesDTO(destCodeAndName, carFmlyCodeAndName);
	}

	/**
	 * Download part price master details.
	 *
	 * @param carFamilyCode the car family code
	 * @param importerCode the importer code
	 * @param partNo the part no
	 * @param effectiveMonth the effective month
	 * @param userId the user id
	 * @return the map
	 * @throws ParseException the parse exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws JRException the JR exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> downloadPartPriceMasterDetails(String carFamilyCode, String importerCode,
			String partNo, String effectiveMonth, String userId) throws ParseException, FileNotFoundException, JRException {

		if (carFamilyCode == null || carFamilyCode.isBlank() ||importerCode == null || importerCode.isBlank() || userId == null || userId.isBlank()) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}

		Map<String, Object> resultMap = new HashMap<>();
		String effMonth = (effectiveMonth == null || effectiveMonth.isBlank()) ? null : 
			DateUtil.getStringDate(effectiveMonth, YYYY_MM, YYYYMM);
		String partNum = (partNo == null || partNo.isBlank()) ? null : partNo.replace("-", "");

		int partPriceMasterEntityCount = partPriceMasterRepository.findPartPriceMasterCount(carFamilyCode, 
				importerCode, effMonth, partNum, userId);

		int reportSizeLimit = Integer.parseInt(tpexConfigRepository.findByName("invoiceGeneration.report.size.limit").getValue());

		//If file size is greater than configured size then store file in directory and return path
		if(partPriceMasterEntityCount > reportSizeLimit) {
			downloadPartPriceMasterDetailsOffline(carFamilyCode, importerCode, partNum, effMonth, userId);
			resultMap.put("message", ConstantUtils.INFO_IN_1004);
			resultMap.put("status", "offline");
			return resultMap;
		} else {
			//If file size is less than configured size then download file directly
			resultMap = (Map<String, Object>) downloadPartPriceMasterDetailsOnline(carFamilyCode, importerCode, partNum, effMonth, userId);
			resultMap.put("status", "online");
			return resultMap;
		}
	}

	/**
	 * Download part price master details online.
	 *
	 * @param carFamilyCode the car family code
	 * @param importerCode the importer code
	 * @param partNo the part no
	 * @param effectiveMonth the effective month
	 * @param userId the user id
	 * @return the object
	 * @throws FileNotFoundException the file not found exception
	 * @throws JRException the JR exception
	 */
	@SuppressWarnings("unchecked")
	private Object downloadPartPriceMasterDetailsOnline(String carFamilyCode, String importerCode,
			String partNo, String effectiveMonth, String userId) throws FileNotFoundException, JRException {

		Map<String, Object> partPriceDetailDataMap = getDownloadPartPriceDetailData(carFamilyCode, 
				importerCode, partNo, effectiveMonth, userId);
		return jasperReportService.getJasperReportDownloadOnline((List<?>) partPriceDetailDataMap.get(DATA_LIST), "xlsx", partPriceDetailDataMap.get(FILE_TEMPLATE_NAME).toString(), partPriceDetailDataMap.get(FILE_NAME).toString(), (Map<String, Object>) partPriceDetailDataMap.get(PARAMETERS), (Map<String, Object>) partPriceDetailDataMap.get(CONFIG));
	}


	/**
	 * Download part price master details offline.
	 *
	 * @param carFamilyCode the car family code
	 * @param importerCode the importer code
	 * @param partNo the part no
	 * @param effectiveMonth the effective month
	 * @param userId the user id
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	@Async
	public String downloadPartPriceMasterDetailsOffline(String carFamilyCode, String importerCode,
			String partNo, String effectiveMonth, String userId) {

		Map<String, Object> partPriceDetailDataMap = getDownloadPartPriceDetailData(carFamilyCode, 
				importerCode, partNo, effectiveMonth, userId);

		Map<String, Object> config = (Map<String, Object>) partPriceDetailDataMap.get(CONFIG);
		String fileTemplateName = partPriceDetailDataMap.get(FILE_TEMPLATE_NAME).toString();

		StringBuilder filePath = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(partPriceDetailDataMap.get(FILE_NAME));

		RddDownLocDtlEntity savedRddDownLocDtlEntity = jasperReportService.saveOfflineDownloadDetail(fileTemplateName, config, filePath);
		int reportId = savedRddDownLocDtlEntity != null ? savedRddDownLocDtlEntity.getReportId() :0;

		//If file size is greater than configured size then store file in directory and return path
		jasperReportService.getJasperReportDownloadOffline((List<?>) partPriceDetailDataMap.get(DATA_LIST), "xlsx", fileTemplateName, (Map<String, Object>) partPriceDetailDataMap.get(PARAMETERS), config, reportId, filePath);
		return fileTemplateName+"."+"xlsx";
	}

	/**
	 * Gets the download part price detail data.
	 *
	 * @param carFamilyCode the car family code
	 * @param importerCode the importer code
	 * @param partNo the part no
	 * @param effectiveMonth the effective month
	 * @param userId the user id
	 * @return the download part price detail data
	 */
	private Map<String, Object> getDownloadPartPriceDetailData(String carFamilyCode, String importerCode,
			String partNo, String effectiveMonth, String userId) {

		List<PartPriceMasterEntity> partPriceMasterEntities = partPriceMasterRepository.findPartPriceDetails(carFamilyCode, 
				importerCode, effectiveMonth, partNo, userId);

		List<PartPriceMasterDto> partPriceMasterEntityList = partPriceMasterEntities.stream().map(m -> 
		{
			try {
				return new PartPriceMasterDto(m.getId().getCfCode()+m.getId().getDestCode()+m.getId().getEffFromMonth()+m.getId().getPartNo()+m.getId().getCurrencyCode(),
						m.getId().getCfCode(), m.getId().getDestCode(),
						DateUtil.getStringDate(m.getId().getEffFromMonth(), YYYYMM, YYYY_MM), 
						DateUtil.getStringDate(m.getEffToMonth(), YYYYMM, YYYY_MM), 
						String.format("%s-%s-%s", m.getId().getPartNo().substring(0,5), m.getId().getPartNo().substring(5,10), m.getId().getPartNo().substring(10)),
						m.getPartName(), new BigDecimal(df.format(m.getPartPrice())), m.getId().getCurrencyCode());
			} catch (ParseException e) {
				return null;
			}
		}).collect(Collectors.toList());

		//Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setAutoFitPageHeight", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", "xlsx");
		config.put("reportSizeLimit", tpexConfigRepository.findByName("invoiceGeneration.report.size.limit").getValue());
		config.put("storeInDB", "true");
		//Change to userId dynamic when fixed on On-demand screen
		config.put("loginUserId", ConstantUtils.TEST_USER);

		String fileTemplateName = tpexConfigRepository.findByName("part.price.master.details.download.file.template.name").getValue();
		//PxP Price_"+<Selected Car Family Code>+”_”+<Selected Importer Code>
		StringBuilder fileName = new StringBuilder(fileTemplateName);
		fileName.append("_").append(carFamilyCode).append("_").append(importerCode).append(".xlsx");

		Map<String, Object> result = new HashMap<>();
		result.put(FILE_TEMPLATE_NAME, fileTemplateName);
		result.put(FILE_NAME, fileName.toString());
		result.put(DATA_LIST, partPriceMasterEntityList);
		result.put(CONFIG, config);
		result.put(PARAMETERS, tpexConfigurationUtil.getReportDynamicPrameters());
		return result;
	}

	@Override
	@Transactional
	public List<PartPriceMasterDto> deletePartPriceMasterDetails(List<PartPriceMasterDto> partPriceMasterDtoList) throws ParseException {

		List<PartPriceMasterDto> partPriceMasterDtos = new ArrayList<>();
		for (PartPriceMasterDto partPriceMasterDeleteRequestDto : partPriceMasterDtoList) {
			if (insInvPartsDetailsRepository.countInvoiceGeneratedForPartPrice(
					partPriceMasterDeleteRequestDto.getImporterCode(), 
					partPriceMasterDeleteRequestDto.getCarFamilyCode(), 
					partPriceMasterDeleteRequestDto.getPartNo().replace("-", ""), 
					partPriceMasterDeleteRequestDto.getEffectiveFromMonth(),
					partPriceMasterDeleteRequestDto.getEffectiveToMonth()) == 0) {
			
			try {
				
				partPriceMasterRepository.deleteByIdCfCodeAndIdDestCodeAndIdCurrencyCodeAndIdPartNoAndIdEffFromMonth(
						partPriceMasterDeleteRequestDto.getCarFamilyCode(), 
						partPriceMasterDeleteRequestDto.getImporterCode(), 
						partPriceMasterDeleteRequestDto.getCurrency(), 
						partPriceMasterDeleteRequestDto.getPartNo().replace("-", ""), 
						DateUtil.getStringDate(partPriceMasterDeleteRequestDto.getEffectiveFromMonth(), YYYY_MM, YYYYMM));
			} catch (InvalidInputParametersException e) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1045);
			}
			} else {
				partPriceMasterDtos.add(partPriceMasterDeleteRequestDto);
			}
		}
		return partPriceMasterDtos;
	}
	
	@Override
	public boolean savePxpPartPriceMaster(PartPriceMasterDeleteRequestDto requestDto) throws ParseException {
		List<PartPriceMasterEntity> partPriceMasterEntityEntities = new ArrayList<>();
		for (PartPriceMasterDto partPriceMasterDto: requestDto.getData()) {
			
			String effMonth = DateUtil.getStringDate(partPriceMasterDto.getEffectiveFromMonth(), YYYY_MM, YYYYMM);
			String effToMonth = partPriceMasterDto.getEffectiveToMonth() == null ? null : DateUtil.getStringDate(partPriceMasterDto.getEffectiveToMonth(), YYYY_MM, YYYYMM);
			String partNum = partPriceMasterDto.getPartNo().replace("-", "");
			
			validateEffDate(effMonth, effToMonth);
			
			if (partPriceMasterDto.getPartPrice().compareTo(BigDecimal.valueOf(1)) < 0) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put(PART_NO, partPriceMasterDto.getPartNo());
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1050, errorMessageParams);
			}
			
			if (partPriceMasterDto.getPartName().isBlank() || ConstantUtils.PART_NO_NOT_EXIST.equals(partPriceMasterDto.getPartName())) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put(PART_NO, partPriceMasterDto.getPartNo());
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1049, errorMessageParams);
			}

			PartPriceMasterEntity partPriceMasterEntity = new PartPriceMasterEntity();
			
			//Set primary key fields
			PartPriceMasterIdEntity partPriceMasterIdEntity = new PartPriceMasterIdEntity();
			partPriceMasterIdEntity.setCfCode(partPriceMasterDto.getCarFamilyCode());
			partPriceMasterIdEntity.setDestCode(partPriceMasterDto.getImporterCode());
			partPriceMasterIdEntity.setCurrencyCode(partPriceMasterDto.getCurrency());
			partPriceMasterIdEntity.setPartNo(partNum);
			partPriceMasterIdEntity.setEffFromMonth(effMonth);
			
			//Set primary key to OemShippingCtrlMstEntity
			partPriceMasterEntity.setId(partPriceMasterIdEntity);
			
			if (partPriceMasterRepository.countByIdCfCodeAndIdDestCodeAndIdPartNoAndIdEffFromMonthAndEffToMonth(partPriceMasterDto.getCarFamilyCode(), 
					partPriceMasterDto.getImporterCode(), partNum, effMonth, effToMonth) > 0) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				StringBuilder errorMessage = new StringBuilder("Effective From Month : ").append(partPriceMasterDto.getEffectiveFromMonth())
						.append(",Effective To Month :").append(partPriceMasterDto.getEffectiveToMonth())
						.append("& Part No. :").append(partPriceMasterDto.getPartNo());
				errorMessageParams.put("keyColumns", errorMessage.toString());
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3008, errorMessageParams);  
			}
			
			if (partPriceMasterRepository.countByIdCfCodeAndIdDestCodeAndIdPartNo(partPriceMasterDto.getCarFamilyCode(), 
					partPriceMasterDto.getImporterCode(), partNum) > 0) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				StringBuilder errorMessage = new StringBuilder("Car Family Code : ").append(partPriceMasterDto.getCarFamilyCode())
						.append(",Importer Code : ").append(partPriceMasterDto.getImporterCode())
						.append("& Part No. :").append(partPriceMasterDto.getPartNo());
				errorMessageParams.put("keyColumns", errorMessage.toString());
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1046, errorMessageParams);  
			}
			
			//Set other fields
			partPriceMasterEntity.setPartName(partPriceMasterDto.getPartName());
			partPriceMasterEntity.setPartPrice(partPriceMasterDto.getPartPrice().doubleValue());
			partPriceMasterEntity.setEffToMonth(effToMonth);
			partPriceMasterEntity.setUpdateBy(requestDto.getUserId());
			partPriceMasterEntity.setUpdateDate(DateUtil.getStringDate(DateUtil.toDate(LocalDateTime.now()), DDMMYYYY, YYYYMMDD));

			//Add record to list
			partPriceMasterEntityEntities.add(partPriceMasterEntity);
		}
		
		//Save all records
		return !partPriceMasterRepository.saveAll(partPriceMasterEntityEntities).isEmpty();
	}
	
	private void validateEffDate(String effMonth, String effToMonth) {
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMM);
        String nowDate = formatter.format(Date.valueOf(LocalDate.now()));

		if (effMonth.compareTo(nowDate) < 0)
			throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1047);
		
		if (effToMonth != null && effToMonth.compareTo(nowDate) < 0)
			throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1048);
		
		if (effMonth.compareTo(effToMonth) > 0)
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3002);
	}
	
	@Override
	public boolean updatePxpPartPriceMaster(PartPriceMasterDeleteRequestDto requestDto) throws ParseException {
		List<PartPriceMasterEntity> partPriceMasterEntities = new ArrayList<>();
		for (PartPriceMasterDto partPriceMasterDto: requestDto.getData()) {
			
			String effMonth = DateUtil.getStringDate(partPriceMasterDto.getEffectiveFromMonth(), YYYY_MM, YYYYMM);
			String effToMonth = partPriceMasterDto.getEffectiveToMonth() == null ? null : DateUtil.getStringDate(partPriceMasterDto.getEffectiveToMonth(), YYYY_MM, YYYYMM);
			String partNum = partPriceMasterDto.getPartNo().replace("-", "");
			
			if (effMonth.compareTo(effToMonth) > 0)
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3002);
			
			if (partPriceMasterDto.getPartPrice().compareTo(BigDecimal.valueOf(1)) < 0) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put(PART_NO, partPriceMasterDto.getPartNo());
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1050, errorMessageParams);
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat(YYYYMM);
			if (effToMonth != null && effToMonth.compareTo(formatter.format(Date.valueOf(LocalDate.now()))) < 0)
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1048);
			
			//Set primary key fields
			PartPriceMasterIdEntity partPriceMasterIdEntity = new PartPriceMasterIdEntity();
			partPriceMasterIdEntity.setCfCode(partPriceMasterDto.getCarFamilyCode());
			partPriceMasterIdEntity.setDestCode(partPriceMasterDto.getImporterCode());
			partPriceMasterIdEntity.setCurrencyCode(partPriceMasterDto.getCurrency());
			partPriceMasterIdEntity.setPartNo(partNum);
			partPriceMasterIdEntity.setEffFromMonth(effMonth);
			
			//Set primary key to OemShippingCtrlMstEntity
			Optional<PartPriceMasterEntity> partPriceMasterEntityOptional = partPriceMasterRepository.findById(partPriceMasterIdEntity);
			if (partPriceMasterEntityOptional.isPresent()) {
				
				PartPriceMasterEntity partPriceMasterEntity = partPriceMasterEntityOptional.get();
				//Set other fields
				partPriceMasterEntity.setPartName(partPriceMasterDto.getPartName());
				partPriceMasterEntity.setPartPrice(partPriceMasterDto.getPartPrice().doubleValue());
				partPriceMasterEntity.setEffToMonth(effToMonth);
				partPriceMasterEntity.setUpdateBy(requestDto.getUserId());
				partPriceMasterEntity.setUpdateDate(DateUtil.getStringDate(DateUtil.toDate(LocalDateTime.now()), DDMMYYYY, YYYYMMDD));
	
				//Add record to list
				partPriceMasterEntities.add(partPriceMasterEntity);
			}
		}
		
		//Save all records
		return !partPriceMasterRepository.saveAll(partPriceMasterEntities).isEmpty();
	}

	@Override
	public String partNameByPartNo(String partNo) {
		String partName = ConstantUtils.PART_NO_NOT_EXIST;
		Optional<PartMasterEntity> partMasterEntityOptional = partMasterRespository.findById(partNo.replace("-", ""));
		if (partMasterEntityOptional.isPresent()) {
			PartMasterEntity partMasterEntity = partMasterEntityOptional.get();
			partName = partMasterEntity.getPartName();
		}
		return partName;
	}

}

