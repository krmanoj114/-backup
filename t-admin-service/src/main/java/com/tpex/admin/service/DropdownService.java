package com.tpex.admin.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.admin.dto.CompanyDropdownDTO;
import com.tpex.admin.dto.CompanyDropdownResponse;
import com.tpex.admin.dto.CompanyPlantMappingDTO;
import com.tpex.admin.dto.ContainerDestinationPlantCodeDTO;
import com.tpex.admin.dto.ContainerDestinationWithCodeDTO;
import com.tpex.admin.dto.PlantDropdownDTO;
import com.tpex.admin.dto.PlantMasterDTO;
import com.tpex.admin.entity.FinalDestEntity;
import com.tpex.admin.entity.TbMPlant;
import com.tpex.admin.entity.TbMUserEntity;
import com.tpex.admin.repository.CompanyDropdownRepository;
import com.tpex.admin.repository.FinalDestRepository;
import com.tpex.admin.repository.PlantMasterRepository;
import com.tpex.admin.repository.TbMUserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DropdownService {
	
	@Autowired
	PlantMasterRepository plantMasterRepository;

	@Autowired
	FinalDestRepository finalDestRepository;
	
	private final CompanyDropdownRepository companyDropdownRepository;
	private final TbMUserRepository tbMUserRepository;

	private static final Logger log = LoggerFactory.getLogger(DropdownService.class);

	public CompanyDropdownResponse getCompanyAndPlantDropdownByUserId(String userId) {
		log.info("DropdownService::getCompanyAndPlantDropdownByUserId: Started");
		CompanyDropdownResponse res = new CompanyDropdownResponse();
		List<CompanyPlantMappingDTO> compPlantList = companyDropdownRepository.findCompanyAndPlantDropdownByUser(userId);
		
		List<CompanyDropdownDTO> dropdown = compPlantList.stream().sorted(Comparator.comparing(CompanyPlantMappingDTO::getCompanyName)).map(cp -> {
			CompanyDropdownDTO cd = new CompanyDropdownDTO();
			cd.setCode(cp.getCompanyCode());
			cd.setName(cp.getCompanyName());
			return cd;
		}).distinct().collect(Collectors.toList());
		
		dropdown.forEach(c -> {
			List<CompanyPlantMappingDTO> plantList = compPlantList.stream().filter(cp -> cp.getCompanyCode().equals(c.getCode())).collect(Collectors.toList());
			c.setPlants(plantList.stream().sorted(Comparator.comparing(CompanyPlantMappingDTO::getPlantName)).map(p -> {
				PlantDropdownDTO pd = new PlantDropdownDTO();
				pd.setCode(p.getPlantCode());
				pd.setName(p.getPlantName());
				return pd;
			}).collect(Collectors.toList()));
		});
		
		res.setCompanies(dropdown);
		
		Optional<TbMUserEntity> optUser = tbMUserRepository.findById(userId);
		if(optUser.isPresent()) {
			TbMUserEntity user = optUser.get();
			res.setCompanyDefault("tmap-em".equals(user.getCompanyCode()) ? "tmt" : user.getCompanyCode());
		}
		log.info("DropdownService::getCompanyAndPlantDropdownByUserId: Ended");
		return res;
	}

	public ContainerDestinationPlantCodeDTO vanningPlantAndContainerDtls(String cmpCd) {

		log.info("DropdownService::vanningPlantAndContainerDtls: Started");
		ContainerDestinationPlantCodeDTO containerDestinationPlantCodeDto = new ContainerDestinationPlantCodeDTO();

		List<PlantMasterDTO> plantMasterDtoList = new ArrayList<>();
		List<TbMPlant> tbMPlant = plantMasterRepository.findByCompanyCode(cmpCd);

		for (TbMPlant tbMPlantEntity : tbMPlant) {
			PlantMasterDTO plantMasterDto = new PlantMasterDTO();
			plantMasterDto.setId(tbMPlantEntity.getPlantCode());
			plantMasterDto.setName(tbMPlantEntity.getPlantName());
			plantMasterDtoList.add(plantMasterDto);
		}

		containerDestinationPlantCodeDto.setPlantMasterDTO(plantMasterDtoList);

		List<ContainerDestinationWithCodeDTO> containerDestinationWithCodedto = new ArrayList<>();
		List<FinalDestEntity> finalDestEntityList = finalDestRepository.findByCmpCd(cmpCd);

		for (FinalDestEntity finalDestEntity : finalDestEntityList) {
			ContainerDestinationWithCodeDTO containerDestinationWithCodeDTO = new ContainerDestinationWithCodeDTO();
			containerDestinationWithCodeDTO.setId(finalDestEntity.getDestinationCd());
			containerDestinationWithCodeDTO
					.setName(finalDestEntity.getDestinationCd() + "-" + finalDestEntity.getDestinationName());
			containerDestinationWithCodedto.add(containerDestinationWithCodeDTO);
		}
		containerDestinationPlantCodeDto.setContainerDestinationWithCodeDTO(containerDestinationWithCodedto);
		log.info("DropdownService::vanningPlantAndContainerDtls: Ended");
		return containerDestinationPlantCodeDto;
	}

}
