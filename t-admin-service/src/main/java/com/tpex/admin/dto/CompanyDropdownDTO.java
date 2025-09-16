package com.tpex.admin.dto;

import java.util.List;

import lombok.Data;

@Data
public class CompanyDropdownDTO {

	private String code;
	private String name;
	private List<PlantDropdownDTO> plants;
}
