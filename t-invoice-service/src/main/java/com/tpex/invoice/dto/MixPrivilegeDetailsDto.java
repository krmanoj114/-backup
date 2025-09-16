package com.tpex.invoice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MixPrivilegeDetailsDto {
    
	private int privMstId;
	private String destCode;
	private String crFmlyCode;
	private String effFrom;
	private String effTo;
	private String reExporterCode;
	private List<String> priorityOne;
	private List<String> priorityTwo;
	private List<String> priorityThree;
	private List<String> priorityFour;
	private List<String> priorityFive;
	private boolean flag;

}
