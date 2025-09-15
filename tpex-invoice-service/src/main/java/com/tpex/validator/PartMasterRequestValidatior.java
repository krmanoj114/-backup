package com.tpex.validator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.util.StringUtil;

import com.tpex.entity.PartMasterEntity;
import com.tpex.invoice.dto.PartMasterSearchRequestDto;

public class PartMasterRequestValidatior {

	private PartMasterRequestValidatior() {

	}

	public static boolean validatePartMasterSearchRequest(PartMasterSearchRequestDto partMasterSearchRequestDto) {
		return StringUtil.isBlank(partMasterSearchRequestDto.getPartNo())
				&& StringUtil.isBlank(partMasterSearchRequestDto.getPartName())
				&& StringUtil.isBlank(partMasterSearchRequestDto.getPartType());

	}

	public static List<String> validateDuplicatePartNumber(List<PartMasterEntity> partMasterList) {
		Map<String, Long> partNumberCounts = partMasterList.stream()
				.collect(Collectors.groupingBy(PartMasterEntity::getPartNo, Collectors.counting()));

		return partNumberCounts.entrySet().stream().filter(partMaster -> partMaster.getValue() > 1)
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}

}
