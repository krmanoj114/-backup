package com.tpex.invoice.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.entity.CustomLabelEntity;
import com.tpex.entity.PackingAndVanningEntity;
import com.tpex.entity.SsLineGroupBoxEntity;
import com.tpex.entity.VanningPlantEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.CustomLabelDTO;
import com.tpex.invoice.dto.CustomLabelResponseDTO;
import com.tpex.invoice.dto.PackingAndVanningDTO;
import com.tpex.invoice.dto.PackingAndVanningResponseDTO;
import com.tpex.invoice.dto.PackingVanningReportDTO;
import com.tpex.invoice.dto.PackingVanningReportResponseDTO;
import com.tpex.invoice.dto.SsLineGroupBoxDTO;
import com.tpex.invoice.dto.SsLineGroupBoxResponseDTO;
import com.tpex.invoice.dto.VanningPlantDTO;
import com.tpex.invoice.dto.VanningPlantResponseDTO;
import com.tpex.invoice.service.PackingAndVanningService;
import com.tpex.repository.CustomLabelRepository;
import com.tpex.repository.PackingAndVanningRepository;
import com.tpex.repository.SsLineGroupBoxRepository;
import com.tpex.repository.VanningPlantRepository;
import com.tpex.util.ConstantUtils;

@Service
public class PackingAndVanningServiceImpl implements PackingAndVanningService {

	@Autowired
	PackingAndVanningRepository packingAndVanningRepository;
	@Autowired
	VanningPlantRepository vanningPlantRepository;
	@Autowired
	CustomLabelRepository customLabelRepository;
	@Autowired
	SsLineGroupBoxRepository ssLineGroupBoxRepository;

	@Override
	public PackingAndVanningResponseDTO getPackingAndVanningResponseData(String companyCode) {
		PackingAndVanningResponseDTO packingAndVanningResponseDTO = new PackingAndVanningResponseDTO();
		List<PackingAndVanningEntity> packingAndVanningEntityList = packingAndVanningRepository
				.findByCmpCode(companyCode);
		packingAndVanningResponseDTO.setContainerDestination(packingAndVanningEntityList.stream()
				.map(m -> new PackingAndVanningDTO(m.getDestinationCode(), m.getDestinationName()))
				.collect(Collectors.toList()));
		return packingAndVanningResponseDTO;
	}

	@Override
	public VanningPlantResponseDTO getVanningPlantResponseData(String companyCode, String planningFlag) {
		VanningPlantResponseDTO vanningPlantResponseDTO = new VanningPlantResponseDTO();

		List<VanningPlantEntity> vanningPlantEntityList;

		String packingFlag;
		String vanningFlag;
	
		if (planningFlag.equalsIgnoreCase("p")) {
			packingFlag = "Y";

			vanningPlantEntityList = vanningPlantRepository.findByCompanyCodeAndPackingFlag(companyCode, packingFlag);
		} else {
			vanningFlag = "Y";

			vanningPlantEntityList = vanningPlantRepository.findByCompanyCodeAndVanningFlag(companyCode, vanningFlag);
		}

		vanningPlantResponseDTO.setVanningPlantCodeName(vanningPlantEntityList.stream()
				.map(m -> new VanningPlantDTO(m.getPlantCode(), m.getPlantName())).collect(Collectors.toList()));

		return vanningPlantResponseDTO;
	}

	@Override
	public CustomLabelResponseDTO getCustomLabelResponseData(String companyCode) {
		CustomLabelResponseDTO customLabelResponseDTO = new CustomLabelResponseDTO();

		List<CustomLabelEntity> customLabelEntity = customLabelRepository.findByCmpCode(companyCode);
		customLabelResponseDTO.setCustomLabelDescription(customLabelEntity.stream()
				.map(m -> new CustomLabelDTO(m.getLabelCode(), m.getLabelDescription(), m.getBarcodeInputRequired())).collect(Collectors.toList()));

		return customLabelResponseDTO;
	}

	@Override
	public SsLineGroupBoxResponseDTO getSsLineGroupBoxResponseData() {
		SsLineGroupBoxResponseDTO ssLineGroupBoxResponseDTO = new SsLineGroupBoxResponseDTO();
		List<SsLineGroupBoxEntity> ssLineGroupBoxEntity = ssLineGroupBoxRepository.findAll();
		ssLineGroupBoxResponseDTO.setSSLinegroupCode(ssLineGroupBoxEntity.stream().distinct()
				.map(m -> new SsLineGroupBoxDTO(m.getGroupCode())).collect(Collectors.toList()));
		return ssLineGroupBoxResponseDTO;
	}

	@Override
	public PackingVanningReportResponseDTO getPackingVanningReport() throws IOException {
		PackingVanningReportResponseDTO packingVanningReportResponseDTO = new PackingVanningReportResponseDTO();
		File file = ResourceUtils.getFile(ConstantUtils.REPORT_TYPES_JSON_FOR_PACKINGVANNING);
		if (!file.exists()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.FILEPATH, ConstantUtils.REPORT_TYPES_JSON_FOR_PACKINGVANNING);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018, errorMessageParams);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		List<PackingVanningReportDTO> packingVanningReportList = Arrays
				.asList(objectMapper.readValue(file, PackingVanningReportDTO[].class));
		packingVanningReportResponseDTO.setPackingVanningReportList(packingVanningReportList);
		return packingVanningReportResponseDTO;
	}

}
