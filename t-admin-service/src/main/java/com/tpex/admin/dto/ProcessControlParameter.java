package com.tpex.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessControlParameter {

	private String parameterName; 
	private String controlType;
	private String isRequired;
	private String maxLength;
	private String parameterTitle;
}
