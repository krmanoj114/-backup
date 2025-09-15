package com.tpex.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class CompanyDropdownDTO {

	private String companyCode;
	private String companyName;
	private List<PlantDropdownDTO> plantDropdown;
}
