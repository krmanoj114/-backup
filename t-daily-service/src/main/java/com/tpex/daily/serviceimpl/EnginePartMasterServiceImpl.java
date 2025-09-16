package com.tpex.daily.serviceimpl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tpex.daily.service.EnginePartMasterService;
import com.tpex.daily.dto.CarFamilyNameWithCodeDTO;
import com.tpex.daily.dto.DestinationCodeFinalResponseDTO;
import com.tpex.daily.dto.DestinationNameWithCodeDTO;
import com.tpex.daily.dto.EnginePartMasterResponseDTO;
import com.tpex.daily.dto.EnginePartMasterSaveRequestDto;
import com.tpex.daily.dto.EnginePartMstRequestDTO;
import com.tpex.daily.dto.ExporterImporterDataDTO;
import com.tpex.daily.entity.CarFamilyMasterEntity;
import com.tpex.daily.entity.EnginePartMasterEntity;
import com.tpex.daily.entity.EnginePartMasterIdEntity;
import com.tpex.daily.entity.FinalDestEntity;
import com.tpex.daily.entity.PartMasterEntity;
import com.tpex.daily.exception.InvalidInputParametersException;
import com.tpex.daily.exception.ResourceNotFoundException;
import com.tpex.daily.repository.CarFamilyMastRepository;
import com.tpex.daily.repository.EnginePartMasterRepository;
import com.tpex.daily.repository.FinalDestRepository;
import com.tpex.daily.repository.PartMasterRespository;
import com.tpex.daily.util.ConstantUtils;
import com.tpex.daily.util.Util;

@Service
public class EnginePartMasterServiceImpl implements EnginePartMasterService {

	@Autowired
	EntityManager entityManager;

	@Autowired
	CarFamilyMastRepository carFamilyMastRepository;

	@Autowired
	FinalDestRepository finalDestRepository;

	@Autowired
	PartMasterRespository partMasterRespository;

	@Autowired
	EnginePartMasterRepository enginePartMasterRepository;

	private static final String PART_NO = "partNo";

	public DestinationCodeFinalResponseDTO destinationCodeandImporterDtls() {

		DestinationCodeFinalResponseDTO codeFinalResponseDTO = new DestinationCodeFinalResponseDTO();

		List<DestinationNameWithCodeDTO> destinationNameWithCodeDTOList = new ArrayList<>();
		List<FinalDestEntity> finalDestEntityList = finalDestRepository.findAll(Sort.by("destinationCd").ascending());
		for (FinalDestEntity finalDestEntity : finalDestEntityList) {
			DestinationNameWithCodeDTO destinationNameWithCodeDTO = new DestinationNameWithCodeDTO();
			destinationNameWithCodeDTO.setId(finalDestEntity.getDestinationCd());
			destinationNameWithCodeDTO
					.setName(finalDestEntity.getDestinationCd() + "-" + finalDestEntity.getDestinationName());
			destinationNameWithCodeDTOList.add(destinationNameWithCodeDTO);
		}

		List<CarFamilyNameWithCodeDTO> carFamilyNameWithCodeDTOList = new ArrayList<>();
		List<CarFamilyMasterEntity> carFamilyMasterEntityList = carFamilyMastRepository
				.findAll(Sort.by("carFmlyCode").ascending());
		for (CarFamilyMasterEntity carFamilyMasterEntity : carFamilyMasterEntityList) {
			CarFamilyNameWithCodeDTO carFamilyCodeWithCodeDTO = new CarFamilyNameWithCodeDTO();
			carFamilyCodeWithCodeDTO.setId(carFamilyMasterEntity.getCarFmlyCode());
			carFamilyCodeWithCodeDTO
					.setName(carFamilyMasterEntity.getCarFmlyCode() + "-" + carFamilyMasterEntity.getCarFmlySrsName());
			carFamilyNameWithCodeDTOList.add(carFamilyCodeWithCodeDTO);
		}

		codeFinalResponseDTO.setCarFamilyCodeWithCodeDTOs(carFamilyNameWithCodeDTOList);
		codeFinalResponseDTO.setDestinationNameWithCodeDTO(destinationNameWithCodeDTOList);
		return codeFinalResponseDTO;

	}

	public EnginePartMasterResponseDTO enginePartMasterSearchDtls(EnginePartMstRequestDTO enginePartMstRequestDTO) {

		EnginePartMasterResponseDTO enginePartMasterResponseDTO = new EnginePartMasterResponseDTO();

		List<ExporterImporterDataDTO> exporterImporterDataDTOList = new ArrayList<>();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<EnginePartMasterEntity> criteriaQueryEnginePartMaster = criteriaBuilder
				.createQuery(EnginePartMasterEntity.class);
		Root<EnginePartMasterEntity> rootEnginePartMaster = criteriaQueryEnginePartMaster
				.from(EnginePartMasterEntity.class);

		List<Predicate> predicateList = new ArrayList<>();

		if (!StringUtils.isBlank(enginePartMstRequestDTO.getExporterCode())) {

			Predicate predicateForGradeE = criteriaBuilder.equal(
					rootEnginePartMaster.get("id").get(ConstantUtils.EXPORTERCODE),
					enginePartMstRequestDTO.getExporterCode());

			predicateList.add(predicateForGradeE);

		}

		if (!StringUtils.isBlank(enginePartMstRequestDTO.getImporterCode())) {

			Predicate predicateForGradeF = criteriaBuilder.equal(
					rootEnginePartMaster.get("id").get(ConstantUtils.IMPORTERCODE),
					enginePartMstRequestDTO.getImporterCode());

			predicateList.add(predicateForGradeF);

		}

		if (!StringUtils.isBlank(enginePartMstRequestDTO.getCarFamilyCode())) {
			Predicate predicateForGradeG = criteriaBuilder.equal(
					rootEnginePartMaster.get("id").get(ConstantUtils.CRFMLYCODE),
					enginePartMstRequestDTO.getCarFamilyCode());

			predicateList.add(predicateForGradeG);

		}

		if (!StringUtils.isBlank(enginePartMstRequestDTO.getPartNo())) {

			Predicate predicateForGradeH = criteriaBuilder.like(
					rootEnginePartMaster.get("id").get(ConstantUtils.PARTNO), "%" + enginePartMstRequestDTO.getPartNo() + "%");

			predicateList.add(predicateForGradeH);

		}

		if (StringUtils.isBlank(enginePartMstRequestDTO.getCarFamilyCode())
				&& StringUtils.isBlank(enginePartMstRequestDTO.getExporterCode())
				&& StringUtils.isBlank(enginePartMstRequestDTO.getImporterCode())
				&& StringUtils.isBlank(enginePartMstRequestDTO.getPartNo()))

		{
			criteriaQueryEnginePartMaster = criteriaQueryEnginePartMaster.select(rootEnginePartMaster);
		}

		else {
			criteriaQueryEnginePartMaster = criteriaQueryEnginePartMaster.select(rootEnginePartMaster)
					.where(predicateList.toArray(new Predicate[] {}));
		}

		TypedQuery<EnginePartMasterEntity> query = entityManager.createQuery(criteriaQueryEnginePartMaster);
		List<EnginePartMasterEntity> enginePartMasterEntityResultList = query.getResultList();

		if (!enginePartMasterEntityResultList.isEmpty()) {
			for (EnginePartMasterEntity enginePartMasterEntity : enginePartMasterEntityResultList) {
				ExporterImporterDataDTO exporterImporterDataDTO = new ExporterImporterDataDTO();
				exporterImporterDataDTO.setCarFamilyCode(enginePartMasterEntity.getId().getCrFmlyCode());
				exporterImporterDataDTO.setExporterCode(enginePartMasterEntity.getId().getExporterCode());
				exporterImporterDataDTO.setImporterCode(enginePartMasterEntity.getId().getImporterCode());
				exporterImporterDataDTO.setLotModuleCode(enginePartMasterEntity.getId().getLotModuleCode());
				exporterImporterDataDTO.setPartNo(enginePartMasterEntity.getId().getPartNo());

				exporterImporterDataDTOList.add(exporterImporterDataDTO);
			}
		}

		enginePartMasterResponseDTO.setExporterImporterDataDTO(exporterImporterDataDTOList);

		return enginePartMasterResponseDTO;

	}

	@Override
	@Transactional
	public void deleteEnginePartMaster(List<EnginePartMasterSaveRequestDto> enginePartMasterRequestDtoList) {

		for (EnginePartMasterSaveRequestDto enginePartMasterRequestDto : enginePartMasterRequestDtoList) {
			EnginePartMasterIdEntity idEntity = new EnginePartMasterIdEntity();
			idEntity.setCrFmlyCode(enginePartMasterRequestDto.getCrFmlyCode());
			idEntity.setExporterCode(enginePartMasterRequestDto.getExporterCode());
			idEntity.setImporterCode(enginePartMasterRequestDto.getImporterCode());
			idEntity.setLotModuleCode(enginePartMasterRequestDto.getLotModuleCode());
			idEntity.setPartNo(enginePartMasterRequestDto.getPartNo());
			enginePartMasterRepository.deleteById(idEntity);
		}
	}

	@Override
	public boolean saveEnginePartMaster(List<@Valid EnginePartMasterSaveRequestDto> saveRequestList, String userId) {

		List<EnginePartMasterEntity> enginePartMasterEntities = new ArrayList<>();

		for (EnginePartMasterSaveRequestDto enginePartMasterRequestDto : saveRequestList) {

			mandatoryInputsCheck(enginePartMasterRequestDto.getCrFmlyCode(),
					enginePartMasterRequestDto.getImporterCode(), enginePartMasterRequestDto.getExporterCode(),
					enginePartMasterRequestDto.getPartNo());

			EnginePartMasterEntity entity = new EnginePartMasterEntity();
			EnginePartMasterIdEntity idEntity = new EnginePartMasterIdEntity();

			if (enginePartMasterRequestDto.getPartNo().isBlank() || ConstantUtils.PART_NO_NOT_EXIST
					.equals(partNameByPartNo(enginePartMasterRequestDto.getPartNo()))) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put(PART_NO, enginePartMasterRequestDto.getPartNo());
				throw new ResourceNotFoundException(ConstantUtils.ERR_DLY_1049, errorMessageParams);
			}

			idEntity.setImporterCode(enginePartMasterRequestDto.getImporterCode());
			idEntity.setExporterCode(enginePartMasterRequestDto.getExporterCode());
			idEntity.setCrFmlyCode(enginePartMasterRequestDto.getCrFmlyCode());
			idEntity.setLotModuleCode(enginePartMasterRequestDto.getLotModuleCode());
			idEntity.setPartNo(enginePartMasterRequestDto.getPartNo());

			Optional<EnginePartMasterEntity> enginePartMasterEntity = enginePartMasterRepository.findById(idEntity);

			if (enginePartMasterEntity.isPresent()) {

				EnginePartMasterEntity dbEntity = enginePartMasterEntity.get();

				if ((enginePartMasterRequestDto.getQuantity() != null)
						&& enginePartMasterRequestDto.getEngineFlag() != null
						&& dbEntity.getEngineFlag() != null && dbEntity.getQuantity() != null 
						&& dbEntity.getQuantity().equals(enginePartMasterRequestDto.getQuantity()) 
						&& dbEntity.getEngineFlag().equals(enginePartMasterRequestDto.getEngineFlag())) {
					throw new InvalidInputParametersException(ConstantUtils.INFO_CM_3008);
				}

				entity.setId(idEntity);
				entity.setQuantity(enginePartMasterRequestDto.getQuantity());
				entity.setEngineFlag(enginePartMasterRequestDto.getEngineFlag());
				entity.setUpdateBy(userId);
				entity.setUpdateDate(Date.valueOf(LocalDate.now()));
				entity.setCompanyCode(enginePartMasterRequestDto.getCompanyCode());
			} 
			
			 if (enginePartMasterRepository
					.countByIdCrFmlyCodeAndIdImporterCodeAndIdExporterCodeAndIdLotModuleCodeAndIdPartNo(
							enginePartMasterRequestDto.getCrFmlyCode(),
							enginePartMasterRequestDto.getImporterCode(),
							enginePartMasterRequestDto.getExporterCode(),
							enginePartMasterRequestDto.getLotModuleCode(),
							enginePartMasterRequestDto.getPartNo()) > 0) {				 
					Map<String, Object> errorMessageParams = new HashMap<>();
					StringBuilder errorMessage = new StringBuilder("Importer Code : ")
							.append(enginePartMasterRequestDto.getImporterCode()).append(",Car family : ")
							.append(enginePartMasterRequestDto.getCrFmlyCode()).append(",Lot/Module Code : ")
							.append(enginePartMasterRequestDto.getLotModuleCode()).append(",Part Number : ")
							.append(enginePartMasterRequestDto.getPartNo()).append(",Exporter code : ")
							.append(enginePartMasterRequestDto.getExporterCode());
					errorMessageParams.put("keyColumns", errorMessage.toString());
					throw new ResourceNotFoundException(ConstantUtils.ERR_CM_3008, errorMessageParams);
				}

				entity.setId(idEntity);
				entity.setQuantity(enginePartMasterRequestDto.getQuantity());
				entity.setEngineFlag(enginePartMasterRequestDto.getEngineFlag());
				entity.setUpdateBy(userId);
				entity.setUpdateDate(Date.valueOf(LocalDate.now()));
				entity.setCompanyCode(enginePartMasterRequestDto.getCompanyCode());

			
			enginePartMasterEntities.add(entity);
		}

		return !enginePartMasterRepository.saveAll(enginePartMasterEntities).isEmpty();
	}

	private void mandatoryInputsCheck(String carFmlyCode, String impCode, String expCode, String partNo) {
		if (!Util.nullCheck(carFmlyCode) || !Util.nullCheck(impCode) || !Util.nullCheck(expCode)
				|| !Util.nullCheck(partNo)) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}
	}

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
