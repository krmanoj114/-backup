package com.tpex.invoice.serviceimpl;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tpex.dto.CommonDropdownDto;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.dto.InvoiceSetupMasterDto;
import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.entity.ExporterCodeMasterEntity;
import com.tpex.entity.InvoiceSetupDetailEntity;
import com.tpex.entity.InvoiceSetupMasterEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InvoiceSetupListResponseDto;
import com.tpex.invoice.dto.InvoiceSetupMasterSaveRequestDto;
import com.tpex.invoice.service.InvoiceSetupMasterService;
import com.tpex.repository.CarFamilyDestinationMasterRepository;
import com.tpex.repository.ExporterCodeMasterRepository;
import com.tpex.repository.InvoiceSetupDetailRepository;
import com.tpex.repository.InvoiceSetupMasterRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

@Service
public class InvoiceSetupMasterServiceImpl implements InvoiceSetupMasterService{
	
	@Autowired
	InvoiceSetupMasterRepository invoiceSetupMasterRepository;
	
	@Autowired
	InvoiceSetupDetailRepository invoiceSetupDetailRepository;
	
	@Autowired
	CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;
	
	@Autowired
	ExporterCodeMasterRepository exporterCodeMasterRepository;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtils.DEFAULT_DATE_FORMATE);

	@Override
	public InvoiceSetupListResponseDto getInvoiceSetupMstDetails(String setupType, String importerCode) {
		
		InvoiceSetupListResponseDto invoiceSetupListResponseDto = new InvoiceSetupListResponseDto();
		
		//Add response
		String [] lineCode = {"1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I",
				"J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		
		List<String> importerCodeList = new ArrayList<>();
		importerCodeList.add(importerCode);

		List<CarFamilyDestinationMasterEntity> carFamilyDestMasterEntityList = carFamilyDestinationMasterRepository.findDistinctByIdDestinationCodeIn(importerCodeList);
		
		List<CommonMultiSelectDropdownDto> cfcList = carFamilyDestMasterEntityList.stream().distinct().map(t -> new CommonMultiSelectDropdownDto(t.getId().getCarFmlyCode(), t.getId().getCarFmlyCode())).collect(Collectors.toList());
		invoiceSetupListResponseDto.setCfcList(cfcList.stream().distinct().collect(Collectors.toList()));
		
		List<CommonMultiSelectDropdownDto> reExporterList = carFamilyDestMasterEntityList.stream().map(t -> new CommonMultiSelectDropdownDto(t.getId().getReExporterCode(), t.getId().getReExporterCode())).collect(Collectors.toList());
		invoiceSetupListResponseDto.setReExpCodeList(reExporterList.stream().distinct().collect(Collectors.toList()));
		
		List<ExporterCodeMasterEntity> exporterEntityList= exporterCodeMasterRepository.findAll();
		
		List<CommonMultiSelectDropdownDto> exporterList = exporterEntityList.stream().distinct().map(t -> new CommonMultiSelectDropdownDto(t.getExpCode(), t.getExpCode())).collect(Collectors.toList());
		invoiceSetupListResponseDto.setExporterCodeList(exporterList.stream().distinct().collect(Collectors.toList()));
		
		ArrayList<String> lineCodes = new ArrayList<> (Arrays.asList(lineCode));
		List<CommonMultiSelectDropdownDto> lineCodesList = new ArrayList<>();
		for (String code : lineCodes) {
			lineCodesList.add(new CommonMultiSelectDropdownDto(code, code));
		}
		invoiceSetupListResponseDto.setLineCodeList(lineCodesList);
		
		List<CommonDropdownDto> packingMonthList = new ArrayList<>();
		packingMonthList.add(new CommonDropdownDto(ConstantUtils.FLAG_Y, ConstantUtils.FLAG_Y));
		packingMonthList.add(new CommonDropdownDto(ConstantUtils.FLAG_N, ConstantUtils.FLAG_N));
		invoiceSetupListResponseDto.setPackingMonthList(packingMonthList);
		
		List<CommonDropdownDto> priceMethodList = new ArrayList<>();
		priceMethodList.add(new CommonDropdownDto("ETD", "ETD"));
		priceMethodList.add(new CommonDropdownDto(ConstantUtils.PCKMNTH, ConstantUtils.PCK_MNTH));
		invoiceSetupListResponseDto.setPriceMethodList(priceMethodList);
		invoiceSetupListResponseDto.setSetupType(setupType);
		invoiceSetupListResponseDto.setImporterCode(importerCode);
		
		//Search response
		List<InvoiceSetupMasterEntity> invoiceSetupMasterEntityList = invoiceSetupMasterRepository.findBySetupTypeAndDestinationCodeIn(setupType, importerCodeList);
		
		List<InvoiceSetupMasterDto> invoiceSetupMasterDtoList = new ArrayList<>();
		
		for(InvoiceSetupMasterEntity invoiceSetupMasterEntity : invoiceSetupMasterEntityList) {
			
			List<InvoiceSetupDetailEntity> invoiceSetupDetailEntityList = invoiceSetupDetailRepository.findByInvSetupId(invoiceSetupMasterEntity.getInvSetupId());
			for(InvoiceSetupDetailEntity invoiceSetupDetail : invoiceSetupDetailEntityList) {
				InvoiceSetupMasterDto invoiceSetupMasterDto = new InvoiceSetupMasterDto();
				
				invoiceSetupMasterDto.setPackingMonth(invoiceSetupDetail.getPackagingMnth());
				invoiceSetupMasterDto.setPriceMethod(invoiceSetupDetail.getPriceMethod());
				invoiceSetupMasterDto.setVanDateFrom(invoiceSetupDetail.getEffFrom().format(formatter));
				invoiceSetupMasterDto.setVanDateTo(invoiceSetupDetail.getEffTo().format(formatter));
			
				invoiceSetupMasterDto.setCfc(invoiceSetupDetail.getCarFamilyCode());
				invoiceSetupMasterDto.setExporterCode(invoiceSetupDetail.getExporterCode());
				invoiceSetupMasterDto.setReExpCode(invoiceSetupDetail.getReExporterCode());
				invoiceSetupMasterDto.setLineCode(invoiceSetupDetail.getLineCode());
				invoiceSetupMasterDto.setInvDetailId(invoiceSetupDetail.getId()); 
				
				invoiceSetupMasterDtoList.add(invoiceSetupMasterDto);
			}
		}
		invoiceSetupListResponseDto.setInvSetupMasterList(invoiceSetupMasterDtoList);
		return invoiceSetupListResponseDto;
	}
	
	@Transactional
	@Override
	public void deleteInvoiceSetupMaster(List<Integer> ids) throws ParseException {

		//Delete all records by ids
		try {
			invoiceSetupDetailRepository.deleteAllById(ids);
		}catch(Exception e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3005, errorMessageParams);
		}
	}
	
	@Override
	public boolean saveInvoiceSetupMaster(InvoiceSetupMasterSaveRequestDto invoiceSetupMasterSaveRequestDto, String userId) throws InvalidInputParametersException {
		
		LocalDate currentDate = LocalDate.now(); 
		InvoiceSetupMasterEntity invoiceSetupMasterNewEntity = null;
		int invSetupId = 0;
		List<InvoiceSetupDetailEntity> invoiceSetupDetailEntityList = new ArrayList<>();
		boolean dupKeyFlag = false;
		
		//Check in InvSetupMaster table if, setupType & importerCode combination exist
		InvoiceSetupMasterEntity invoiceSetupMasterSavedEntity = invoiceSetupMasterRepository.findBySetupTypeAndDestinationCode(invoiceSetupMasterSaveRequestDto.getSetupType(), invoiceSetupMasterSaveRequestDto.getImporterCode());
		//if above combination not exists then Save new 
		if(invoiceSetupMasterSavedEntity == null) {
			
			InvoiceSetupMasterEntity invoiceSetupMasterEntity = new InvoiceSetupMasterEntity();
			invoiceSetupMasterEntity.setSetupType(invoiceSetupMasterSaveRequestDto.getSetupType());
			invoiceSetupMasterEntity.setDestinationCode(invoiceSetupMasterSaveRequestDto.getImporterCode());
			invoiceSetupMasterEntity.setUpdatedBy(userId);
			invoiceSetupMasterEntity.setUpdatedDate(Date.valueOf(currentDate));
			invoiceSetupMasterEntity.setCompanyCode(invoiceSetupMasterSaveRequestDto.getCompanyCode());
			
			try {
				invoiceSetupMasterNewEntity = invoiceSetupMasterRepository.save(invoiceSetupMasterEntity);
			}catch(Exception e) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put(ConstantUtils.ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
			}
			invSetupId = invoiceSetupMasterNewEntity.getInvSetupId();
		}else {
			invSetupId = invoiceSetupMasterSavedEntity.getInvSetupId();
			dupKeyFlag = true;
		}
		
		return saveInvoiceSetupDetail(invoiceSetupMasterSaveRequestDto, userId, currentDate, invSetupId,
				invoiceSetupDetailEntityList, dupKeyFlag);
	}

	/**
	 * @param invoiceSetupMasterSaveRequestDto
	 * @param userId
	 * @param currentDate
	 * @param invSetupId
	 * @param invoiceSetupDetailEntityList
	 * @param dupKeyFlag
	 * @return
	 */
	private boolean saveInvoiceSetupDetail(InvoiceSetupMasterSaveRequestDto invoiceSetupMasterSaveRequestDto,
			String userId, LocalDate currentDate, int invSetupId,
			List<InvoiceSetupDetailEntity> invoiceSetupDetailEntityList, boolean dupKeyFlag) {
		for(InvoiceSetupMasterDto invoiceSetupMasterDto : invoiceSetupMasterSaveRequestDto.getInvSetupMasterList()) {
			
			LocalDate fromDate = LocalDate.parse(invoiceSetupMasterDto.getVanDateFrom(), formatter);
			LocalDate toDate = LocalDate.parse(invoiceSetupMasterDto.getVanDateTo() , formatter);
			
			if (fromDate.compareTo(toDate) > 0) {
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3027);
			}
			
			if(dupKeyFlag) {
				validateInvoiceSetupRequest(invoiceSetupMasterSaveRequestDto, invSetupId, invoiceSetupMasterDto);
			}
			
			InvoiceSetupDetailEntity invoiceSetupDetailEntity = new InvoiceSetupDetailEntity();
			
			invoiceSetupDetailEntity.setCarFamilyCode(invoiceSetupMasterDto.getCfc());
			invoiceSetupDetailEntity.setExporterCode(invoiceSetupMasterDto.getExporterCode());
			invoiceSetupDetailEntity.setReExporterCode(invoiceSetupMasterDto.getReExpCode());
			invoiceSetupDetailEntity.setLineCode(invoiceSetupMasterDto.getLineCode());
			invoiceSetupDetailEntity.setPackagingMnth(invoiceSetupMasterDto.getPackingMonth());
			invoiceSetupDetailEntity.setPriceMethod(invoiceSetupMasterDto.getPriceMethod());
			invoiceSetupDetailEntity.setUpdatedBy(userId);
			invoiceSetupDetailEntity.setUpdatedDate(Date.valueOf(currentDate));
			invoiceSetupDetailEntity.setCompanyCode(invoiceSetupMasterSaveRequestDto.getCompanyCode());
			invoiceSetupDetailEntity.setInvSetupId(invSetupId);
			invoiceSetupDetailEntity.setEffFrom(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,invoiceSetupMasterDto.getVanDateFrom()));
			invoiceSetupDetailEntity.setEffTo(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,invoiceSetupMasterDto.getVanDateTo()));
			
			invoiceSetupDetailEntityList.add(invoiceSetupDetailEntity);
		}
			try {
			 invoiceSetupDetailRepository.saveAll(invoiceSetupDetailEntityList);
			}catch(Exception e) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3005);
			}
			
			return true;
	}

	/**
	 * @param invoiceSetupMasterSaveRequestDto
	 * @param invSetupId
	 * @param invoiceSetupMasterDto
	 */
	private void validateInvoiceSetupRequest(InvoiceSetupMasterSaveRequestDto invoiceSetupMasterSaveRequestDto,
			int invSetupId, InvoiceSetupMasterDto invoiceSetupMasterDto) {
		
		//Validate that only single CFC selected by user
		List<String> cfcList = Arrays.asList(invoiceSetupMasterDto.getCfc().split(","));
		if(cfcList.size() == 1) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("cfcCode", invoiceSetupMasterDto.getCfc());
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1052, errorMessageParams);
		}

		//Validate that only single ExporterCode selected by user
		List<String> expList = Arrays.asList(invoiceSetupMasterDto.getExporterCode().split(","));
		if(expList.size() == 1 && !expList.get(0).equals("")) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("exporterCode", invoiceSetupMasterDto.getExporterCode());
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1053, errorMessageParams);
		}
		
		//Validate that only single ReExpCode selected by user
		List<String> reExpList = Arrays.asList(invoiceSetupMasterDto.getReExpCode().split(","));
		if(reExpList.size() == 1 && !reExpList.get(0).equals("")) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("reExpCode", invoiceSetupMasterDto.getReExpCode());
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1054, errorMessageParams);
		}
		
		//Validate that only single LineCode selected by user
		List<String> lineCodeList = Arrays.asList(invoiceSetupMasterDto.getLineCode().split(","));
		if(lineCodeList.size() == 1 && !lineCodeList.get(0).equals("")) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("lineCode", invoiceSetupMasterDto.getLineCode());
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1055, errorMessageParams);
		}
		
		//Duplicate record found for the same key column 
		InvoiceSetupDetailEntity invoiceDetailEntity = invoiceSetupDetailRepository.findByCarFamilyCodeAndEffFromAndEffToAndInvSetupId(invoiceSetupMasterDto.getCfc(), 
				DateUtil.dateFromStringDateFormateforInvoiceDate(ConstantUtils.DEFAULT_DATE_FORMATE, invoiceSetupMasterDto.getVanDateFrom()),
				DateUtil.dateFromStringDateFormateforInvoiceDate(ConstantUtils.DEFAULT_DATE_FORMATE, invoiceSetupMasterDto.getVanDateTo()),
				invSetupId);
		if(invoiceDetailEntity != null) {
			
			ArrayList<String> dupKeyList = getDuplicateKeyKist(invoiceSetupMasterSaveRequestDto, invoiceSetupMasterDto);
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.KEY_COLUMNS, dupKeyList);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3026, errorMessageParams);
		}
		
		for(String cfc : cfcList) {
			//Validate that 1 “CFC” should not be in more then 1 record, under the same key
			List<InvoiceSetupDetailEntity> invoiceDetailEntity5 = invoiceSetupDetailRepository.findByCarFamilyCodeContainingAndEffFromAndEffToAndInvSetupId(cfc,
					DateUtil.dateFromStringDateFormateforInvoiceDate(ConstantUtils.DEFAULT_DATE_FORMATE, invoiceSetupMasterDto.getVanDateFrom()),
					DateUtil.dateFromStringDateFormateforInvoiceDate(ConstantUtils.DEFAULT_DATE_FORMATE, invoiceSetupMasterDto.getVanDateTo()),
					invSetupId);
			
			if(!invoiceDetailEntity5.isEmpty() && invoiceDetailEntity5.get(0) != null) {
				ArrayList<String> dupKeyList = getDuplicateKeyKist(invoiceSetupMasterSaveRequestDto, invoiceSetupMasterDto);
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put(ConstantUtils.KEY_COLUMNS, dupKeyList);
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3026, errorMessageParams);
			}
			
			//Van Dates From : <Van Date From> and Van Date To : <Van Date To> already exists in existing records.
			List<InvoiceSetupDetailEntity> invoiceDetailEntity6 = invoiceSetupDetailRepository.findByCarFamilyCodeContainingAndEffFromLessThanAndEffToGreaterThanAndInvSetupId(cfc,
					DateUtil.dateFromStringDateFormateforInvoiceDate(ConstantUtils.DEFAULT_DATE_FORMATE, invoiceSetupMasterDto.getVanDateFrom()),
					DateUtil.dateFromStringDateFormateforInvoiceDate(ConstantUtils.DEFAULT_DATE_FORMATE, invoiceSetupMasterDto.getVanDateTo()),
					invSetupId);
			
			if(!invoiceDetailEntity6.isEmpty() && invoiceDetailEntity6.get(0) != null) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put("vanDateFrom", invoiceSetupMasterDto.getVanDateFrom());
				errorMessageParams.put("vanDateTo", invoiceSetupMasterDto.getVanDateTo());
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1051, errorMessageParams);
			}
		}
	}

	/**
	 * @param invoiceSetupMasterSaveRequestDto
	 * @param invoiceSetupMasterDto
	 * @return
	 */
	private ArrayList<String> getDuplicateKeyKist(InvoiceSetupMasterSaveRequestDto invoiceSetupMasterSaveRequestDto,
			InvoiceSetupMasterDto invoiceSetupMasterDto) {
		ArrayList<String> dupKeyList = new ArrayList<>();
		dupKeyList.add(invoiceSetupMasterSaveRequestDto.getSetupType()); 
		dupKeyList.add(invoiceSetupMasterSaveRequestDto.getImporterCode());
		dupKeyList.add(invoiceSetupMasterDto.getCfc());
		dupKeyList.add(invoiceSetupMasterDto.getVanDateFrom());
		dupKeyList.add(invoiceSetupMasterDto.getVanDateTo());
		return dupKeyList;
	}
}
