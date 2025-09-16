package com.tpex.invoice.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.entity.ReturnablePackingMasterEntity;
import com.tpex.entity.ReturnablePackingMasterIdEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.ReturnablePackingMasterDetailsDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterImporterDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterSaveRequestDTO;
import com.tpex.invoice.service.ReturnablePackingMasterService;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.PlantMstRepository;
import com.tpex.repository.ReturnablePackingMasterDetailsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

@Service
public class ReturnablePackingMasterServiceImpl implements ReturnablePackingMasterService {

	@Autowired
	private PlantMstRepository plantMstRepository;

	@Autowired
	private OemFnlDstMstRepository oemFnlDstMstRepository;

	@Autowired
	private CarFamilyMastRepository carFamilyMastRepository;

	@Autowired
	private ReturnablePackingMasterDetailsRepository returnablePackingMasterDetailsRepository;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtils.DEFAULT_DATE_FORMATE);

	@Override
	public List<OemFnlDstMstEntity> destinationCodeList() {
		return oemFnlDstMstRepository.findAllByOrderByFdDstCdAsc();
	}

	@Override
	public List<PlantMstEntity> packingPlantList(String cmpCd) {
		return plantMstRepository.findByCmpCdOrderByPlantCdAsc(cmpCd);
	}

	@Override
	public List<CarFamilyMasterEntity> carFamilyList() {
		return carFamilyMastRepository.findAllByOrderByCarFmlyCodeAsc();
	}

	@Override
	public ReturnablePackingMasterImporterDTO fetchReturnablePackingMasterDetails(String packingPlant,
			String moduleType, String cmpCd, String returnableType, String vanDateFrom, String vanDateTo,
			List<String> importerCode) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);
		SimpleDateFormat sdf2 = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);
		String fromDate = null;

		String toDate = null;

		if (vanDateFrom != null) {
			fromDate = sdf2.format(sdf.parse(vanDateFrom));
		}
		if (vanDateTo != null) {
			toDate = sdf2.format(sdf.parse(vanDateTo));
		}

		ReturnablePackingMasterImporterDTO response = new ReturnablePackingMasterImporterDTO();
		List<ReturnablePackingMasterDetailsDTO> list = new ArrayList<>();
		List<Object[]> objList = returnablePackingMasterDetailsRepository.getData(packingPlant, moduleType, cmpCd,
				returnableType, fromDate, toDate, importerCode);

		if (!objList.isEmpty()) {
			for (Object[] obj : objList) {
				ReturnablePackingMasterDetailsDTO dto = new ReturnablePackingMasterDetailsDTO();
				setPackingDetails(sdf, sdf2, obj, dto);
				list.add(dto);
			}
		}
		response.setReturnablePackingMasterDetails(list);

		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		List<PlantMstEntity> plantMstEntityList = plantMstRepository.findByCmpCdOrderByPlantCdAsc(cmpCd);
		plantMstEntityList.stream().forEach(c -> returnablePackingMasterDtoList
				.add(new CommonMultiSelectDropdownDto(c.getPlantCd(), c.getPlantCd() + "-" + c.getPlantName())));

		response.setPackingPlantList(returnablePackingMasterDtoList.stream().distinct().collect(Collectors.toList()));

		List<CommonMultiSelectDropdownDto> returnableFinalDestEntityList = new ArrayList<>();
		List<OemFnlDstMstEntity> finalDestEntityList = oemFnlDstMstRepository.findAllByOrderByFdDstCdAsc();
		finalDestEntityList.stream().forEach(c -> returnableFinalDestEntityList
				.add(new CommonMultiSelectDropdownDto(c.getFdDstCd(), c.getFdDstCd() + "-" + c.getFdDstNm())));

		response.setImporterCodeList(returnableFinalDestEntityList.stream().distinct().collect(Collectors.toList()));
		response.setRrackType(returnableType);
		return response;

	}

	private void setPackingDetails(SimpleDateFormat sdf, SimpleDateFormat sdf2, Object[] obj,
			ReturnablePackingMasterDetailsDTO dto) throws ParseException {
		if (obj[0] != null)
			dto.setPackingPlant(obj[0].toString());
		if (obj[1] != null)
			dto.setImporterCode(obj[1].toString());
		if (obj[2] != null)
			dto.setModuleType(obj[2].toString());
		if (obj[3] != null) {
			dto.setModuleDesciption(obj[3].toString());
		}

		if (obj[4] != null) {
			dto.setVanningDateFrom(sdf.format(sdf2.parse(obj[4].toString())));
		}
		if (obj[5] != null)
			dto.setVanningDateTo(sdf.format(sdf2.parse(obj[5].toString())));
	}

	@Override
	public boolean saveReturnablePackingMaster(
			ReturnablePackingMasterSaveRequestDTO returnablePackingMasterSaveRequestDTO, String userId) {

		LocalDate currentDate = LocalDate.now();
		List<ReturnablePackingMasterEntity> returnablePackingMasterEntities = new ArrayList<>();
		for (ReturnablePackingMasterDetailsDTO returnablePackingMasterDetailsDTO : returnablePackingMasterSaveRequestDTO
				.getReturnablePackingMasterDetailsDTOList()) {
			LocalDate fromDate = LocalDate.parse(returnablePackingMasterDetailsDTO.getVanningDateFrom(), formatter);
			LocalDate toDate = LocalDate.parse(returnablePackingMasterDetailsDTO.getVanningDateTo(), formatter);

			if (fromDate.compareTo(toDate) > 0) {
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3027);
			}

			if (returnablePackingMasterDetailsRepository.validateVanningDateFrom(toDate) >= 1
					|| returnablePackingMasterDetailsRepository.validateVanningDateTo(toDate) >= 1) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				StringBuilder errorMessage = new StringBuilder("Seq No ").append("VanningDateFrom: ").append(fromDate)
						.append(" VanningDateTo: ").append(toDate).append(" under ")
						.append(returnablePackingMasterDetailsDTO.getPackingPlant()).append(", ")
						.append(returnablePackingMasterDetailsDTO.getImporterCode()).append(", ")
						.append(returnablePackingMasterDetailsDTO.getModuleType())
						.append(" should not be overlapped with existing or new record in GRID");
				errorMessageParams.put("keyColumns", errorMessage.toString());
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3029, errorMessageParams);
			}

			ReturnablePackingMasterEntity returnablePackingMasterEntity = new ReturnablePackingMasterEntity();
			ReturnablePackingMasterIdEntity returnablePackingMasterIdEntity = new ReturnablePackingMasterIdEntity();
			returnablePackingMasterIdEntity.setImpCd(returnablePackingMasterDetailsDTO.getImporterCode());
			returnablePackingMasterIdEntity.setModType(returnablePackingMasterDetailsDTO.getModuleType());
			returnablePackingMasterIdEntity.setPlantCd(returnablePackingMasterDetailsDTO.getPackingPlant());
			returnablePackingMasterIdEntity.setVanFrom(DateUtil.dateFromStringSqlDateFormate(
					ConstantUtils.DEFAULT_DATE_FORMATE, returnablePackingMasterDetailsDTO.getVanningDateFrom()));
			returnablePackingMasterEntity.setId(returnablePackingMasterIdEntity);

			if ("Y".equals(returnablePackingMasterSaveRequestDTO.getIsNewRow())
					&& returnablePackingMasterDetailsRepository.countByIdPlantCdAndIdImpCdAndIdModType(
							returnablePackingMasterDetailsDTO.getPackingPlant(),
							returnablePackingMasterDetailsDTO.getImporterCode(),
							returnablePackingMasterDetailsDTO.getModuleType()) > 0) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				StringBuilder errorMessage = new StringBuilder("Packing Plant Code: ")
						.append(returnablePackingMasterDetailsDTO.getPackingPlant()).append(",Importer Code :")
						.append(returnablePackingMasterDetailsDTO.getImporterCode()).append(" & Module Type :")
						.append(returnablePackingMasterDetailsDTO.getModuleType());
				errorMessageParams.put("keyColumns", errorMessage.toString());
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3008, errorMessageParams);
			}

			returnablePackingMasterEntity.setModDescription(returnablePackingMasterDetailsDTO.getModuleDesciption());
			returnablePackingMasterEntity.setVanTo(DateUtil.dateFromStringSqlDateFormate(
					ConstantUtils.DEFAULT_DATE_FORMATE, returnablePackingMasterDetailsDTO.getVanningDateTo()));
			returnablePackingMasterEntity.setCmpCd(returnablePackingMasterSaveRequestDTO.getCmpCd());
			returnablePackingMasterEntity.setCreateBy(userId);
			returnablePackingMasterEntity.setCreateDate(java.sql.Date.valueOf(currentDate));
			returnablePackingMasterEntity.setRrackType(returnablePackingMasterSaveRequestDTO.getRrackType());
			returnablePackingMasterEntity.setUpdBy(userId);
			returnablePackingMasterEntity.setUpdDate(java.sql.Date.valueOf(currentDate));
			returnablePackingMasterEntities.add(returnablePackingMasterEntity);
			try {
				return !returnablePackingMasterDetailsRepository.saveAll(returnablePackingMasterEntities).isEmpty();
			} catch (Exception e) {
				return false;
			}

		}
		return false;
	}

	@Override
	public void deleteReturnablePackingMaster(
			List<ReturnablePackingMasterDetailsDTO> returnablePackingMasterDetailsDTOList) {
		List<ReturnablePackingMasterIdEntity> returnablePackingMasterEntities = returnablePackingMasterDetailsDTOList
				.stream()
				.map(s -> new ReturnablePackingMasterIdEntity(s.getImporterCode(), s.getModuleType(), DateUtil
						.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE, s.getVanningDateFrom()),
						s.getPackingPlant()))
				.collect(Collectors.toList());
		returnablePackingMasterDetailsRepository.deleteAllById(returnablePackingMasterEntities);
	}
}
