package com.tpex.admin.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CPCProcessNameResponseDTO {

	private String processId;
	private String processName;
	private List<ProcessControlParameter> parameters;
	private List<Map<String, Object>> dropDownItems;

}
