package com.tpex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPCProcessNamesResponseDTO {
	
	private String processId;
	private String processName;

}
