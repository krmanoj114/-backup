package com.tpex.invoice.serviceImpl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.entity.ReturnablePackingMasterEntity;
import com.tpex.invoice.dto.ReturnablePackingMasterDetailsDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterImporterDTO;
import com.tpex.invoice.service.ReturnablePackingMasterService;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.PlantMstRepository;
import com.tpex.repository.ReturnablePackingMasterDetailsRepository;
import com.tpex.util.ConstantUtils;

@Service
public class ReturnablePackingMasterServiceImpl implements ReturnablePackingMasterService{
	
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
	public List<PlantMstEntity> packingPlantList(String cmpCd){
		return plantMstRepository.findByCmpCdOrderByPlantCdAsc(cmpCd);
	}
	
	@Override
	public List<CarFamilyMasterEntity> carFamilyList(){
		return carFamilyMastRepository.findAllByOrderByCarFmlyCodeAsc();
	}

	@Override
	public ReturnablePackingMasterImporterDTO fetchReturnablePackingMasterDetails(String packingPlant,
			String importerCode, String moduleType, String cmpCd) {
		
		ReturnablePackingMasterImporterDTO response = new ReturnablePackingMasterImporterDTO();
		List<ReturnablePackingMasterDetailsDTO> list = new ArrayList<>();
		List<ReturnablePackingMasterEntity> returnablePackingMasterList = returnablePackingMasterDetailsRepository.findByIdPlantCdAndIdImpCdAndIdModType(packingPlant, importerCode, moduleType);
		
		for(ReturnablePackingMasterEntity entity : returnablePackingMasterList) {
			ReturnablePackingMasterDetailsDTO dto = new ReturnablePackingMasterDetailsDTO();
			dto.setImporterCode(importerCode);
			dto.setPackingPlant(packingPlant);
			dto.setModuleType(moduleType);
			dto.setModuleDesciption(entity.getModDescription());
			dto.setVanningDateFrom(entity.getId().getVanFrom().format(formatter));
			dto.setVanningDateTo(entity.getVanTo().format(formatter));
			list.add(dto);
		}
		response.setReturnablePackingMasterDetails(list);
		
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		List<PlantMstEntity> plantMstEntityList = plantMstRepository.findByCmpCdOrderByPlantCdAsc(cmpCd);
		plantMstEntityList.stream().forEach(c -> returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto(c.getPlantCd(), c.getPlantCd() + "-" + c.getPlantName())));
		
		response.setPackingPlantList(returnablePackingMasterDtoList.stream().distinct()
				.collect(Collectors.toList()));

		List<OemFnlDstMstEntity> finalDestEntityList = oemFnlDstMstRepository.findAllByOrderByFdDstCdAsc();   
		finalDestEntityList.stream().forEach(c -> returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto(c.getFdDstCd(), c.getFdDstCd() + "-" + c.getFdDstNm())));
		
		response.setImporterCodeList(returnablePackingMasterDtoList.stream().distinct()
				.collect(Collectors.toList()));
		
		return response;
		
	}
	
	
}
