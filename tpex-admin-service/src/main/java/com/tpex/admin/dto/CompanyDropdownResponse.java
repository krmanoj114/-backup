package com.tpex.admin.dto;

import java.util.List;

import lombok.Data;

@Data
public class CompanyDropdownResponse {

	private String companyDefault;
	private List<CompanyDropdownDTO> companies;
}
