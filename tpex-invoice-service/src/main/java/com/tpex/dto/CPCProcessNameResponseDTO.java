package com.tpex.dto;

import java.util.List;

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

}
