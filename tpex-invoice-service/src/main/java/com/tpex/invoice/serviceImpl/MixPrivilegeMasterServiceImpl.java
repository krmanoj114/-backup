package com.tpex.invoice.serviceImpl;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.entity.MixPrivilegeMasterEntity;
import com.tpex.entity.PrivilegeMasterEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.MixPrivilegeDetailsDto;
import com.tpex.invoice.dto.MixPrivilegeDetailsListResponseDto;
import com.tpex.invoice.dto.MixPrivilegeMasterSaveRequestDto;
import com.tpex.invoice.dto.PriorityDto;
import com.tpex.invoice.dto.ReExporterCodeDto;
import com.tpex.invoice.service.MixPrivilegeMasterService;
import com.tpex.repository.CarFamilyDestinationMasterRepository;
import com.tpex.repository.MixPrivilegeMasterRepository;
import com.tpex.repository.PrivilegeMasterRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;
import com.tpex.util.Util;

@Service
public class MixPrivilegeMasterServiceImpl implements MixPrivilegeMasterService{

	@Autowired 
	MixPrivilegeMasterRepository mixPrivilegeMasterRepository;

	@Autowired
	CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;

	@Autowired
	PrivilegeMasterRepository privilegeMasterRepository;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	LocalDate currentDate = LocalDate.now(); 

	public MixPrivilegeDetailsListResponseDto fetchMixPrivilegeDetails(String carFmlyCode,String destCode) {

		MixPrivilegeDetailsListResponseDto response = new MixPrivilegeDetailsListResponseDto();

		List<MixPrivilegeDetailsDto> privList = new ArrayList<>();

		List<MixPrivilegeMasterEntity> mixPrivilegeMasterList = mixPrivilegeMasterRepository.findByCarFmlyCodeAndDestinationCode(carFmlyCode,destCode);

		for(MixPrivilegeMasterEntity entity : mixPrivilegeMasterList) {

			MixPrivilegeDetailsDto dto = new MixPrivilegeDetailsDto();

			dto.setDestCode(destCode);
			dto.setCrFmlyCode(carFmlyCode);
			dto.setPrivMstId(entity.getPrivMstId());
			dto.setEffFrom(entity.getEffFrom().format(formatter));
			dto.setEffTo(entity.getEffTo().format(formatter));

			String reExporterCode = entity.getExporterCode();
			String reExporterName = carFamilyDestinationMasterRepository.findReExporterName(destCode,carFmlyCode,reExporterCode);
            
			if(reExporterName.equalsIgnoreCase(ConstantUtils.REEXPORTERCODE_ALL)) {
				dto.setReExporterCode(reExporterName);
			}else {
				dto.setReExporterCode(reExporterCode.concat("-").concat(reExporterName));
			}

			List<String> priorityOne = Stream.of(entity.getPriorityOne().split(",")).collect(Collectors.toList());
			dto.setPriorityOne(priorityOne);

			List<String> priorityTwo = Stream.of(entity.getPriorityTwo().split(",")).collect(Collectors.toList());
			dto.setPriorityTwo(priorityTwo);

			List<String> priorityThree = Stream.of(entity.getPriorityThree().split(",")).collect(Collectors.toList());
			dto.setPriorityThree(priorityThree);

			List<String> priorityFour = Stream.of(entity.getPriorityFour().split(",")).collect(Collectors.toList());
			dto.setPriorityFour(priorityFour);

			List<String> priorityFive = Stream.of(entity.getPriorityFive().split(",")).collect(Collectors.toList());
			dto.setPriorityFive(priorityFive);

			if(entity.getEffTo().compareTo(currentDate) < 0)
				dto.setFlag(true);

			privList.add(dto);
		}

		response.setMixPrivilegeDetails(privList);

		List<CarFamilyDestinationMasterEntity> carFamilyDestinationMasterEntity = carFamilyDestinationMasterRepository.findByIdCarFmlyCodeAndIdDestinationCode(carFmlyCode,destCode);

		List<ReExporterCodeDto> reExporterCodeDto = carFamilyDestinationMasterEntity.stream().map(u -> new ReExporterCodeDto(u.getId().getReExporterCode(),u.getSrsName())).collect(Collectors.toList());

		response.setReExporterCode(reExporterCodeDto);

		List<PrivilegeMasterEntity> privilegeList = privilegeMasterRepository.findPriorities();

		List<PriorityDto> priority = privilegeList.stream().map(p->new PriorityDto(p.getId().getPrivilegeCode(),p.getId().getPrivilegeName())).collect(Collectors.toList());
		response.setPriority(priority);

		return response;
	}

	@Transactional
	@Override
	public void deleteMixPrivilegeMaster(List<Integer> ids) throws ParseException {

		Map<String,Object[]> errorMessageParamsArray = new HashMap<>();

		List<String> etdFromList = new ArrayList<>();

		for(Integer id : ids) {
			Optional<MixPrivilegeMasterEntity> entity = mixPrivilegeMasterRepository.findById(id);
			if(entity.isPresent()) {
				MixPrivilegeMasterEntity mixPrivilegeMasterEntity = entity.get();
				LocalDate etdFrom = mixPrivilegeMasterEntity.getEffFrom();
				if (etdFrom != null && etdFrom.compareTo(currentDate) <= 0) {
					etdFromList.add(etdFrom.toString());
				}
			}
		}
		if(!etdFromList.isEmpty()) {
			String[] etdFrom = etdFromList.toArray(new String[0]);
			errorMessageParamsArray.put("vanDateFrom",etdFrom);
			throw new InvalidInputParametersException(errorMessageParamsArray,ConstantUtils.ERR_IN_1024);
		}

		//Delete all records by id
		mixPrivilegeMasterRepository.deleteAllById(ids);
	}

	@Override
	public boolean saveMixPrivilegeMaster(List<MixPrivilegeMasterSaveRequestDto> mixPrivilegeMstSaveRequestDtoList,String userId) throws InvalidInputParametersException {

		MixPrivilegeMasterEntity entity = new MixPrivilegeMasterEntity();

		for(MixPrivilegeMasterSaveRequestDto dto: mixPrivilegeMstSaveRequestDtoList){

			if(!dto.getConfirmFlag().equalsIgnoreCase(ConstantUtils.NOT_CONFIRMED)) {

				if(dto.getPrivMstId()!=null) {

					Optional<MixPrivilegeMasterEntity> optEntity = mixPrivilegeMasterRepository.findById(dto.getPrivMstId());

					if(optEntity.isPresent()){

						entity = optEntity.get();

						if(!dto.getConfirmFlag().equalsIgnoreCase(ConstantUtils.CONFIRMED)){

							String priorityOne = entity.getPriorityOne();
							String inputPriorityOne = dto.getPriorityOne().stream().collect(Collectors.joining(","));
							String priorityTwo = entity.getPriorityTwo();
							String inputPriorityTwo = dto.getPriorityTwo().stream().collect(Collectors.joining(","));
							String priorityThree = entity.getPriorityThree();
							String inputPriorityThree = dto.getPriorityThree().stream().collect(Collectors.joining(","));
							String priorityFour = entity.getPriorityFour();
							String inputPriorityFour = dto.getPriorityFour().stream().collect(Collectors.joining(","));
							String priorityFive = entity.getPriorityFive();
							String inputPriorityFive = dto.getPriorityFive().stream().collect(Collectors.joining(","));

							Date entityEffFrom = Date.valueOf(entity.getEffFrom());
							Date inputEffFrom = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,dto.getEffFromDate());
							Date entityEffTo = Date.valueOf(entity.getEffTo());
							Date inputEffTo = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,dto.getEffToDate());

							if((inputEffFrom.compareTo(entityEffFrom) == 0) && (inputEffTo.compareTo(entityEffTo) == 0) && (priorityOne.equals(inputPriorityOne))
									&& (priorityTwo.equals(inputPriorityTwo)) && (priorityThree.equals(inputPriorityThree)) && (priorityFour.equals(inputPriorityFour))
									&& (priorityFive.equals(inputPriorityFive))){
								throw new InvalidInputParametersException(ConstantUtils.INFO_CM_3008);

							}

							validateMixPrivilegeMstRequest(dto);
						}

						entity.setCompanyCode(dto.getCompanyCode());
						entity.setEffFrom(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,dto.getEffFromDate()));
						entity.setEffTo(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,dto.getEffToDate()));
						entity.setPriorityOne(dto.getPriorityOne().stream().collect(Collectors.joining(",")));
						entity.setPriorityTwo(dto.getPriorityTwo().stream().collect(Collectors.joining(",")));
						entity.setPriorityThree(dto.getPriorityThree().stream().collect(Collectors.joining(",")));
						entity.setPriorityFour(dto.getPriorityFour().stream().collect(Collectors.joining(",")));
						entity.setPriorityFive(dto.getPriorityFive().stream().collect(Collectors.joining(",")));
						entity.setUpdatedBy(userId);
						entity.setUpdatedDate(Date.valueOf(currentDate));

						try {
							//Save all records
							if(dto.getConfirmFlag().isBlank() || dto.getConfirmFlag().equalsIgnoreCase(ConstantUtils.CONFIRMED)) {
								return mixPrivilegeMasterRepository.save(entity) != null;
							}
						} catch(Exception e){
							return false;
						}
					}

				}else {

					if(!dto.getConfirmFlag().equalsIgnoreCase(ConstantUtils.CONFIRMED)){
						validateMixPrivilegeMstRequest(dto);
					}

					entity.setDestinationCode(dto.getDestCode());
					entity.setCarFmlyCode(dto.getCrFmlyCode());
					entity.setExporterCode(dto.getReExpCode());
					entity.setCompanyCode(dto.getCompanyCode());
					entity.setEffFrom(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,dto.getEffFromDate()));
					entity.setEffTo(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,dto.getEffToDate()));
					entity.setPriorityOne(dto.getPriorityOne().stream().collect(Collectors.joining(",")));
					entity.setPriorityTwo(dto.getPriorityTwo().stream().collect(Collectors.joining(",")));
					entity.setPriorityThree(dto.getPriorityThree().stream().collect(Collectors.joining(",")));
					entity.setPriorityFour(dto.getPriorityFour().stream().collect(Collectors.joining(",")));
					entity.setPriorityFive(dto.getPriorityFive().stream().collect(Collectors.joining(",")));
					entity.setUpdatedBy(userId);
					entity.setUpdatedDate(Date.valueOf(currentDate));

					try {
						//Save all records
						if(dto.getConfirmFlag().isBlank() || dto.getConfirmFlag().equalsIgnoreCase(ConstantUtils.CONFIRMED)) {
							return mixPrivilegeMasterRepository.save(entity) != null;
						}
					} catch(Exception e) {
						return false;
					}
				}
			}
		}

		return false;

	}

	private void validateMixPrivilegeMstRequest(MixPrivilegeMasterSaveRequestDto mixPrivilegeMstSaveRequestDto) throws InvalidInputParametersException{

		String effFrom = mixPrivilegeMstSaveRequestDto.getEffFromDate();
		String effTo = mixPrivilegeMstSaveRequestDto.getEffToDate();
		String destCode = mixPrivilegeMstSaveRequestDto.getDestCode();
		String crFmlycode = mixPrivilegeMstSaveRequestDto.getCrFmlyCode();
		String reExpCode = mixPrivilegeMstSaveRequestDto.getReExpCode();
        
		if(!Util.nullCheck(destCode) || !Util.nullCheck(crFmlycode) || !Util.nullCheck(reExpCode) || 
				!Util.nullCheck(effFrom) || !Util.nullCheck(effTo) || mixPrivilegeMstSaveRequestDto.getPriorityOne().isEmpty()) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}
		
		Date effFromDate = DateUtil.dateFromStringDateFormateforInvoiceDate(ConstantUtils.DEFAULT_DATE_FORMATE, effFrom);
		Date effToDate = DateUtil.dateFromStringDateFormateforInvoiceDate(ConstantUtils.DEFAULT_DATE_FORMATE, effTo);

		if(effFromDate.compareTo(effToDate) > 0) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1036);
		}

		Map<String, Object> errorMessageParams = new HashMap<>();
		Map<String,Object[]> errorMessageparamsArray = new HashMap<>();

		if(mixPrivilegeMstSaveRequestDto.getPrivMstId()==null) {

			if((effFromDate.compareTo(Date.valueOf(currentDate)) < 0) || (effToDate.compareTo(Date.valueOf(currentDate)) < 0)) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1037);
			}

			Optional<MixPrivilegeMasterEntity> entity = mixPrivilegeMasterRepository.findBydestinationCodeAndCarFmlyCodeAndExporterCodeAndEffFromAndEffTo
					(destCode,crFmlycode,reExpCode,effFromDate,effToDate);
			if(entity.isPresent()) {
				List<String> keyColumns = new ArrayList<>();
				keyColumns.add("ImporterCode");
				keyColumns.add("CarFamilyCode");
				keyColumns.add("Re-exportCode");
				keyColumns.add("VanDateFrom");
				keyColumns.add("VanDateTo");
				errorMessageParams.put("keyColumns",keyColumns);
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3008,errorMessageParams);
			}
		}

		List<MixPrivilegeMasterEntity> listEntity = mixPrivilegeMasterRepository.findByDestinationCodeAndCarFmlyCodeAndExporterCode(destCode,crFmlycode,reExpCode);

		if(!listEntity.isEmpty()) {
			for(MixPrivilegeMasterEntity e : listEntity) {

				Date entityEffFromDate = new Date(0);
				Date entityEffToDate = new Date(0);

				if(e.getEffFrom() != null) {
					entityEffFromDate = Date.valueOf(e.getEffFrom()); 
				}
				if(e.getEffTo() != null) {
					entityEffToDate = Date.valueOf(e.getEffTo());
				}

				if(effFromDate.before(entityEffFromDate) && effToDate.after(entityEffFromDate) ||
						effFromDate.before(entityEffToDate) && effToDate.after(entityEffToDate) ||
						effFromDate.before(entityEffFromDate) && effToDate.after(entityEffToDate) ||
						effFromDate.after(entityEffFromDate) && effToDate.before(entityEffToDate) ) {
					errorMessageParams.put("Re-ExporterCode","Re-Export Code:"+reExpCode); 
					throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1038,errorMessageParams);
				}

			}
		}

		List<String> priorityOne   = mixPrivilegeMstSaveRequestDto.getPriorityOne();
		List<String> priorityTwo   = mixPrivilegeMstSaveRequestDto.getPriorityTwo();
		List<String> priorityThree = mixPrivilegeMstSaveRequestDto.getPriorityThree();
		List<String> priorityFour  = mixPrivilegeMstSaveRequestDto.getPriorityFour();
		List<String> priorityFive  = mixPrivilegeMstSaveRequestDto.getPriorityFive();

		List<List<String>> priorities = new ArrayList<>();
		priorities.add(priorityOne);
		priorities.add(priorityTwo);
		priorities.add(priorityThree);
		priorities.add(priorityFour);
		priorities.add(priorityFive);

		boolean lessThanTwoPrivilegeFlag = false;
		List<Integer> lessThanTwoPrivilegeList = new ArrayList<>();

		Integer priorityNum1 = 0;
		for(List<String> priority : priorities) {

			priorityNum1++;
			if(!priority.isEmpty() && priority.size() <2){
				lessThanTwoPrivilegeList.add(priorityNum1);
				lessThanTwoPrivilegeFlag = true;
			}
		}
		if(lessThanTwoPrivilegeFlag){
			Integer[] lessThanTwoPrivilegePriorities = lessThanTwoPrivilegeList.toArray(new Integer[0]);
			errorMessageparamsArray.put("Priority_N",lessThanTwoPrivilegePriorities);
			throw new InvalidInputParametersException(errorMessageparamsArray,ConstantUtils.ERR_IN_1039);
		}

		List<PrivilegeMasterEntity> priorityList = privilegeMasterRepository.findAll();

		Map<String, List<String>> listOfPrivileges = priorityList.stream().collect(Collectors.groupingBy(e->e.getId().getPrivilegeName(), 
				Collectors.mapping( e->e.getId().getPrivilegeName()+" "+e.getId().getPrivilegeCode(),Collectors.toList())));

		List<String> keyList = new ArrayList<>(listOfPrivileges.keySet());
		List<String> privType1 = new ArrayList<>();
		List<String> privType2 = new ArrayList<>();
		List<String> privType3 = new ArrayList<>();
		List<String> privType4 = new ArrayList<>();
		List<String> privType5 = new ArrayList<>();

		if(!keyList.isEmpty()) {
			privType1 = listOfPrivileges.get(keyList.get(0));
			privType2 = listOfPrivileges.get(keyList.get(1));
			privType3 = listOfPrivileges.get(keyList.get(2));
			privType4 = listOfPrivileges.get(keyList.get(3));
			privType5 = listOfPrivileges.get(keyList.get(4));
		}

		boolean samePrivlegeFlag = false;
		List<Integer> samePrivlegeTypeList = new ArrayList<>();
		Integer priorityNum =1;

		if(!priorityOne.isEmpty() && ((priorityOne.size()==priorityTwo.size() && (priorityOne.stream().allMatch(priorityTwo::contains))) || 
				(priorityOne.size()==priorityThree.size() && priorityOne.stream().allMatch(priorityThree::contains))||
				(priorityOne.size()==priorityFour.size() && priorityOne.stream().allMatch(priorityFour::contains)) || 
				(priorityOne.size()==priorityFive.size() && priorityOne.stream().allMatch(priorityFive::contains))
				)){
			samePrivlegeFlag = true;
			samePrivlegeTypeList.add(priorityNum);

		}
		priorityNum++;
		if(!priorityTwo.isEmpty() && ((priorityTwo.size()==priorityOne.size() && priorityTwo.stream().allMatch(priorityOne::contains)) || 
				(priorityTwo.size()==priorityThree.size() && priorityTwo.stream().allMatch(priorityThree::contains))||
				(priorityTwo.size()==priorityFour.size() && priorityTwo.stream().allMatch(priorityFour::contains)) || 
				(priorityTwo.size()==priorityFive.size() && priorityTwo.stream().allMatch(priorityFive::contains))
				)) {
			samePrivlegeFlag = true;
			samePrivlegeTypeList.add(priorityNum);

		}
		priorityNum++;
		if(!priorityThree.isEmpty() && ((priorityThree.size()==priorityOne.size() && priorityThree.stream().allMatch(priorityOne::contains)) || 
				(priorityThree.size()==priorityTwo.size() && priorityThree.stream().allMatch(priorityTwo::contains))||
				(priorityThree.size()==priorityFour.size() && priorityThree.stream().allMatch(priorityFour::contains)) || 
				(priorityThree.size()==priorityFive.size() && priorityThree.stream().allMatch(priorityFive::contains))
				)) {
			samePrivlegeFlag = true;
			samePrivlegeTypeList.add(priorityNum);

		}
		priorityNum++;
		if(!priorityFour.isEmpty() && ((priorityFour.size()==priorityOne.size() && priorityFour.stream().allMatch(priorityOne::contains)) || 
				(priorityFour.size()==priorityTwo.size() && priorityFour.stream().allMatch(priorityTwo::contains))||
				(priorityFour.size()==priorityThree.size() && priorityFour.stream().allMatch(priorityThree::contains)) || 
				(priorityFour.size()==priorityFive.size() && priorityFour.stream().allMatch(priorityFive::contains))
				)) {
			samePrivlegeFlag = true;
			samePrivlegeTypeList.add(priorityNum);
		}
		priorityNum++;
		if(!priorityFive.isEmpty() && ((priorityFive.size()==priorityOne.size() && priorityFive.stream().allMatch(priorityOne::contains)) || 
				(priorityFive.size()==priorityTwo.size() && priorityFive.stream().allMatch(priorityTwo::contains))||
				(priorityFive.size()==priorityThree.size() && priorityFive.stream().allMatch(priorityThree::contains)) || 
				(priorityFive.size()==priorityFour.size() && priorityFive.stream().allMatch(priorityFour::contains))
				)) {
			samePrivlegeFlag = true;
			samePrivlegeTypeList.add(priorityNum);
		}

		if(samePrivlegeFlag) {
			Integer[] samePrivlegeTypePriorities = samePrivlegeTypeList.toArray(new Integer[0]);
			errorMessageparamsArray.put("Priority_N",samePrivlegeTypePriorities);
			throw new InvalidInputParametersException(errorMessageparamsArray,ConstantUtils.ERR_IN_1040);
		}


		boolean moreThanTwoPrivlegeTypeFlag = false;
		List<Integer> moreThanTwoPrivlegeTypeList = new ArrayList<>();

		Integer priorityCount = 0;

		for(List<String> priority : priorities) {
			priorityCount++;
			if((!priority.isEmpty() && priority.size() >2) && (((priority.stream().anyMatch(privType1::contains)) && (priority.stream().anyMatch(privType2::contains)) && 
					(priority.stream().anyMatch(privType3::contains))) || ((priority.stream().anyMatch(privType2::contains)) && (priority.stream().anyMatch(privType3::contains)) && 
							(priority.stream().anyMatch(privType4::contains))) || ((priority.stream().anyMatch(privType3::contains)) && (priority.stream().anyMatch(privType4::contains)) && 
									(priority.stream().anyMatch(privType5::contains))) || ((priority.stream().anyMatch(privType4::contains)) && (priority.stream().anyMatch(privType5::contains)) && 
											(priority.stream().anyMatch(privType1::contains))) || ((priority.stream().anyMatch(privType5::contains)) && (priority.stream().anyMatch(privType1::contains)) && 
													(priority.stream().anyMatch(privType2::contains))))) {
				moreThanTwoPrivlegeTypeFlag = true;
				moreThanTwoPrivlegeTypeList.add(priorityCount);

			}
		}

		if(moreThanTwoPrivlegeTypeFlag) {
			Integer[] moreThanTwoPrivTypesPriorities = moreThanTwoPrivlegeTypeList.toArray(new Integer[0]);
			errorMessageparamsArray.put("Priority_N",moreThanTwoPrivTypesPriorities);
			throw new InvalidInputParametersException(errorMessageparamsArray,ConstantUtils.WARN_IN_1040);
		}


		List<Integer> expectNonPriorityList = new ArrayList<>();
		List<String> expectNonPrivlegeNameList = new ArrayList<>();

		Integer count = 0;
		Integer priotityNumber = 0;

		String privilegeTypeNon = ConstantUtils.PRIVILEGETYPE_NON;

		for(List<String> priority : priorities) {
			if(!priority.isEmpty()) {
				priotityNumber++;
				count = 0;
				if(privType1.stream().noneMatch(p->p.contains(privilegeTypeNon)) && count<2) {
					if(priority.stream().anyMatch(privType1::contains)){
						count++;
						if(count >= 2) {
							expectNonPrivlegeNameList.add(privType1.get(0));
						}
					}
				}
				if(privType2.stream().noneMatch(p->p.contains(privilegeTypeNon)) && count<2) {
					if(priority.stream().anyMatch(privType2::contains)){
						count++;
						if(count >= 2) {
							expectNonPrivlegeNameList.add(privType2.get(0));
						}
					}
				}
				if(privType3.stream().noneMatch(p->p.contains(privilegeTypeNon)) && count<2) {
					if(priority.stream().anyMatch(privType3::contains)){
						count++;
						if(count >= 2) {
							expectNonPrivlegeNameList.add(privType3.get(0));
						}
					}
				}
				if(privType4.stream().noneMatch(p->p.contains(privilegeTypeNon)) && count<2) {
					if(priority.stream().anyMatch(privType4::contains)){
						count++;
						if(count >= 2) {
							expectNonPrivlegeNameList.add(privType4.get(0));
						}
					}
				}
				if(privType5.stream().noneMatch(p->p.contains(privilegeTypeNon)) && count<2) {
					if(priority.stream().anyMatch(privType5::contains)){
						count++;
						if(count >= 2) {
							expectNonPrivlegeNameList.add(privType5.get(0));
						}
					}
				}
				if(count >= 2) {
					expectNonPriorityList.add(priotityNumber);
				}
			}

			if(count >= 2) {
				Integer[] priorityTypes = expectNonPriorityList.toArray(new Integer[0]);
				String[] privilegeTypes = expectNonPrivlegeNameList.toArray(new String[0]);

				errorMessageparamsArray.put("Priority_N",priorityTypes);
				errorMessageparamsArray.put("Privilege_Names",privilegeTypes);

				throw new InvalidInputParametersException(errorMessageparamsArray,ConstantUtils.WARN_IN_1041);
			}
		}



	}


}


