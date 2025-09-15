package com.tpex.daily.serviceimpl;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.daily.dto.ContainerDestinationPlantCodeDTO;
import com.tpex.daily.dto.ContainerDestinationWithCodeDTO;
import com.tpex.daily.dto.IsoContainerDataDto;
import com.tpex.daily.dto.IsoContainerFinalResponseDTO;
import com.tpex.daily.dto.IsoContainerMasterDto;
import com.tpex.daily.dto.IsoContainerRequestDTO;
import com.tpex.daily.dto.PlantMasterDTO;
import com.tpex.daily.entity.FinalDestEntity;
import com.tpex.daily.entity.IsoContainerMasterEntity;
import com.tpex.daily.entity.IsoContainerMasterIdEntity;
import com.tpex.daily.entity.TbMPlant;
import com.tpex.daily.exception.ItemNotFoundException;
import com.tpex.daily.exception.ResourceNotFoundException;
import com.tpex.daily.repository.FinalDestRepository;
import com.tpex.daily.repository.IsoContainerMasterRepo;
import com.tpex.daily.repository.PlantMasterRepository;
import com.tpex.daily.service.IsoConatinerNoService;
import com.tpex.daily.util.ConstantUtils;

import io.netty.util.internal.StringUtil;

@Service
public class IsoContainerNoServiceImpl implements IsoConatinerNoService {

	@Autowired
	PlantMasterRepository plantMasterRepository;

	@Autowired
	FinalDestRepository finalDestRepository;

	@Autowired
	EntityManager entityManager;

	@Autowired
	IsoContainerMasterRepo isoContainerMasterRepo;

	public ContainerDestinationPlantCodeDTO vanningPlantAndContainerDtls() {

		ContainerDestinationPlantCodeDTO containerDestinationPlantCodeDto = new ContainerDestinationPlantCodeDTO();

		List<PlantMasterDTO> plantMasterDtoList = new ArrayList<>();
		List<TbMPlant> tbMPlant = plantMasterRepository.findAll();

		for (TbMPlant tbMPlantEntity : tbMPlant) {
			PlantMasterDTO plantMasterDto = new PlantMasterDTO();
			plantMasterDto.setId(tbMPlantEntity.getPlantCode());
			plantMasterDto.setName(tbMPlantEntity.getPlantName());
			plantMasterDtoList.add(plantMasterDto);
		}

		containerDestinationPlantCodeDto.setPlantMasterDTO(plantMasterDtoList);

		List<ContainerDestinationWithCodeDTO> containerDestinationWithCodedto = new ArrayList<>();
		List<FinalDestEntity> finalDestEntityList = finalDestRepository.findAll();

		for (FinalDestEntity finalDestEntity : finalDestEntityList) {
			ContainerDestinationWithCodeDTO containerDestinationWithCodeDTO = new ContainerDestinationWithCodeDTO();
			containerDestinationWithCodeDTO.setId(finalDestEntity.getDestinationCd());
			containerDestinationWithCodeDTO
					.setName(finalDestEntity.getDestinationCd() + "-" + finalDestEntity.getDestinationName());
			containerDestinationWithCodedto.add(containerDestinationWithCodeDTO);
		}
		containerDestinationPlantCodeDto.setContainerDestinationWithCodeDTO(containerDestinationWithCodedto);
		return containerDestinationPlantCodeDto;
	}

	public IsoContainerFinalResponseDTO vanningPlantAndContainerSearch(IsoContainerRequestDTO containerRequestDTO)
			throws ParseException {

		SimpleDateFormat inputMonthFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_INPUT);
		SimpleDateFormat outPutMonthFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_OUTPUT);

		String effectiveFromMonth = null;
		if (!StringUtil.isNullOrEmpty(containerRequestDTO.getVanningMonth())) {
			effectiveFromMonth = outPutMonthFormat
					.format(inputMonthFormat.parse(containerRequestDTO.getVanningMonth()));
		}
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);
		SimpleDateFormat outPutDateFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);
		
		String etdFormatted = null;
		String ranbanCode = null;				
		if (StringUtil.isNullOrEmpty(containerRequestDTO.getContinerRanbanNo())){
			ranbanCode = null;
		}
		else 
		{
			ranbanCode = containerRequestDTO.getContinerRanbanNo();
		}		
		if (!StringUtil.isNullOrEmpty(containerRequestDTO.getEtd())) {
			etdFormatted = outPutDateFormat.format(inputDateFormat.parse(containerRequestDTO.getEtd()));
		}
		
		IsoContainerFinalResponseDTO containerFinalResponseDTO = new IsoContainerFinalResponseDTO();
                 
		List<String[]> conatinerData = plantMasterRepository.getConstainerDetails(effectiveFromMonth,
				containerRequestDTO.getVanningPlant(), containerRequestDTO.getContainerDestination(), etdFormatted,
				ranbanCode);

		if (conatinerData.isEmpty()) {

			throw new ResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}

		List<IsoContainerDataDto> containerDataDtoList = new ArrayList<>();
		for (String[] isoConatinerDataEntity : conatinerData) {
			IsoContainerDataDto containerDataDto = new IsoContainerDataDto();
			if (!StringUtil.isNullOrEmpty(isoConatinerDataEntity[0])) {
				containerDataDto.setEtd(inputDateFormat.format(outPutDateFormat.parse(isoConatinerDataEntity[0])));
			}
			containerDataDto.setBookingNo(isoConatinerDataEntity[1]);
			containerDataDto.setContainerRanbanNo(isoConatinerDataEntity[2]);
			containerDataDto.setIsoContainerNo(isoConatinerDataEntity[3]);
			containerDataDto.setContainerType(isoConatinerDataEntity[4]);
			containerDataDto.setSealNo(isoConatinerDataEntity[5]);
			containerDataDto.setTareWeight(isoConatinerDataEntity[6]);
			containerDataDto.setContainerSize(isoConatinerDataEntity[7]);
			containerDataDto.setShipComp(isoConatinerDataEntity[8]);
			containerDataDto.setVanningStatus(isoConatinerDataEntity[9]);
			containerDataDtoList.add(containerDataDto);
		}

		containerFinalResponseDTO.setIsoContainerDataDto(containerDataDtoList);

		return containerFinalResponseDTO;

	}

	@Override
	public Boolean save(List<@Valid IsoContainerMasterDto> isoContainerMasterDtoList) throws ParseException {

		List<IsoContainerMasterEntity> containerMasterEntities = new ArrayList<>();

		for (IsoContainerMasterDto isoContainerMasterDto : isoContainerMasterDtoList) {

			SimpleDateFormat inputDateFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);
			SimpleDateFormat outPutDateFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);
			String etdFormatted = null;
			if (!StringUtil.isNullOrEmpty(isoContainerMasterDto.getEtd())) {
				etdFormatted = outPutDateFormat.format(inputDateFormat.parse(isoContainerMasterDto.getEtd()));
			}
			Date date = Date.valueOf(etdFormatted);
			
			

			SimpleDateFormat inputMonthFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_INPUT);
			SimpleDateFormat outPutMonthFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_OUTPUT);

			String effectiveFromMonth = null;
			if (!StringUtil.isNullOrEmpty(isoContainerMasterDto.getVanMth())) {
				effectiveFromMonth = outPutMonthFormat
						.format(inputMonthFormat.parse(isoContainerMasterDto.getVanMth()));
			}
			
			

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);
			String currentDateTime = LocalDateTime.now().format(formatter);
			Date date1 = Date.valueOf(currentDateTime);
			
			mandatoryFeildCheck(isoContainerMasterDto, date, effectiveFromMonth);			 

			IsoContainerMasterEntity isoContainerMasterEntity = saveEntityList(isoContainerMasterDto, date,
					effectiveFromMonth, date1);

			containerMasterEntities.add(isoContainerMasterEntity);

		}
		return !isoContainerMasterRepo.saveAll(containerMasterEntities).isEmpty();
	}

	private IsoContainerMasterEntity saveEntityList(IsoContainerMasterDto isoContainerMasterDto, Date date,
			String effectiveFromMonth, Date date1) {
		IsoContainerMasterIdEntity isoContainerMasterIdEntity = new IsoContainerMasterIdEntity();
		isoContainerMasterIdEntity.setBookingNo(isoContainerMasterDto.getBookingNo());
		isoContainerMasterIdEntity.setContainerSerialNo(isoContainerMasterDto.getContainerRanbanNo());
		isoContainerMasterIdEntity.setContDestCd(isoContainerMasterDto.getDstCd());
		isoContainerMasterIdEntity.setContVanningMnth(effectiveFromMonth);
		isoContainerMasterIdEntity.setEtd1(date);

		IsoContainerMasterEntity isoContainerMasterEntity = new IsoContainerMasterEntity();
		isoContainerMasterEntity.setCompanyCode(isoContainerMasterDto.getCmpCd());
		isoContainerMasterEntity.setConatinerType(isoContainerMasterDto.getContainerType());
		isoContainerMasterEntity.setContainerSize(isoContainerMasterDto.getContainerSize().substring(0, 2));
		isoContainerMasterEntity.setId(isoContainerMasterIdEntity);
		isoContainerMasterEntity.setIsoContainerNo(isoContainerMasterDto.getIsoContainerNo());
		isoContainerMasterEntity.setShipName(isoContainerMasterDto.getShipComp());
		isoContainerMasterEntity.setSealNo(isoContainerMasterDto.getSealNo());
		isoContainerMasterEntity.setTareWeight(isoContainerMasterDto.getTareWeight());
		isoContainerMasterEntity.setUpdatedBy(isoContainerMasterDto.getUpdBy());
		isoContainerMasterEntity.setUpdatedDate(date1);
		isoContainerMasterRepo.save(isoContainerMasterEntity);
		return isoContainerMasterEntity;
	}

	private void mandatoryFeildCheck(IsoContainerMasterDto isoContainerMasterDto, Date date,
			String effectiveFromMonth) {

		IsoContainerMasterIdEntity isoContainerMasterIdEntity = new IsoContainerMasterIdEntity();
		isoContainerMasterIdEntity.setBookingNo(isoContainerMasterDto.getBookingNo());
		isoContainerMasterIdEntity.setContainerSerialNo(isoContainerMasterDto.getContainerRanbanNo());
		isoContainerMasterIdEntity.setContDestCd(isoContainerMasterDto.getDstCd());
		isoContainerMasterIdEntity.setContVanningMnth(effectiveFromMonth);
		isoContainerMasterIdEntity.setEtd1(date);

		Optional<IsoContainerMasterEntity> isoConatinerMasterEntityFromRepo = isoContainerMasterRepo
				.findById(isoContainerMasterIdEntity);
         
		if (isoConatinerMasterEntityFromRepo.isPresent()) {

			IsoContainerMasterEntity dbEntity = isoConatinerMasterEntityFromRepo.get();

			if (!Objects.equals(isoContainerMasterDto.getIsoContainerNo(), dbEntity.getIsoContainerNo())) {

				Boolean isoContainerFlag = checkDuplicateIsoContainerForIsoContainer(date,
						isoContainerMasterDto.getDstCd(), isoContainerMasterDto.getIsoContainerNo());

				validationCheckIsoCont(isoContainerFlag);
			}

			if ((!Objects.equals(isoContainerMasterDto.getSealNo(), dbEntity.getSealNo()))) {

				Boolean sealNoFlag = checkDuplicateIsoContainerForSealNo(date, isoContainerMasterDto.getDstCd(),
						isoContainerMasterDto.getIsoContainerNo());

				validationCheckSealNo(sealNoFlag);
			}		
		}

		if (StringUtil.isNullOrEmpty(isoContainerMasterDto.getVanMth())
				|| StringUtil.isNullOrEmpty(isoContainerMasterDto.getDstCd())
				|| StringUtil.isNullOrEmpty(isoContainerMasterDto.getEtd())
				|| StringUtil.isNullOrEmpty(isoContainerMasterDto.getBookingNo())
				|| StringUtil.isNullOrEmpty(isoContainerMasterDto.getSno())) {

			throw new ItemNotFoundException(ConstantUtils.ERR_CM_3083);
		}

		if (StringUtil.isNullOrEmpty(isoContainerMasterDto.getBookingNo())
				|| StringUtil.isNullOrEmpty(isoContainerMasterDto.getShipComp())) {

			throw new ItemNotFoundException(ConstantUtils.ERR_CM_3080);
		}
		
	}
	
	private void validationCheckSealNo(Boolean isoContainerFlag) {
		
		if(Boolean.TRUE.equals(isoContainerFlag)) {
		throw new ItemNotFoundException(ConstantUtils.ERR_CM_3082);
		
		}	
		
	}

	private void validationCheckIsoCont(Boolean sealNoFlag) {
		
		if(Boolean.TRUE.equals(sealNoFlag)) {
		throw new ItemNotFoundException(ConstantUtils.ERR_CM_3081);
		
		}	
		
	}
	public boolean checkDuplicateIsoContainerForIsoContainer(Date etd, String dstCode, String isoContainerNo) {

		List<IsoContainerMasterEntity> checkContainerNo = null;
		List<IsoContainerMasterEntity> findAll = isoContainerMasterRepo.findByIdEtd1AndIdContDestCd(etd, dstCode);
		if (!StringUtil.isNullOrEmpty(isoContainerNo)) {
			checkContainerNo = findAll.stream().filter(x -> x.getIsoContainerNo().contentEquals(isoContainerNo))
					.collect(Collectors.toList());
			return !checkContainerNo.isEmpty();
		}

		return false;
	}

	public boolean checkDuplicateIsoContainerForSealNo(Date etd, String dstCode, String sealNo) {

		List<IsoContainerMasterEntity> checkSealNo = null;
		List<IsoContainerMasterEntity> findAll = isoContainerMasterRepo.findByIdEtd1AndIdContDestCd(etd, dstCode);

		if (!StringUtil.isNullOrEmpty(sealNo)) {
			checkSealNo = findAll.stream().filter(x -> x.getSealNo().contentEquals(sealNo))
					.collect(Collectors.toList());
			return !checkSealNo.isEmpty();
		}
		return false;
	}

}
