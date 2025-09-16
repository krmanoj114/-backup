package com.tpex.auth.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tpex.auth.dto.CompanyDropdownDTO;
import com.tpex.auth.dto.CompanyPlantMappingDTO;
import com.tpex.auth.dto.FunctionPermissionDTO;
import com.tpex.auth.dto.FunctionRoleMappingDTO;
import com.tpex.auth.dto.PlantDropdownDTO;
import com.tpex.auth.repository.CustomAuthRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	
	private final CustomAuthRepository customAuthRepository;

	public List<CompanyDropdownDTO> getCompanyAndPlantDropdownByUserId(String userId) {
		List<CompanyPlantMappingDTO> compPlantList = customAuthRepository.findCompanyAndPlantDropdownByUserId(userId);
		
		List<CompanyDropdownDTO> dropdown = compPlantList.stream().sorted(Comparator.comparing(CompanyPlantMappingDTO::getCompanyName)).map(cp -> {
			CompanyDropdownDTO cd = new CompanyDropdownDTO();
			cd.setCompanyCode(cp.getCompanyCode());
			cd.setCompanyName(cp.getCompanyName());
			return cd;
		}).distinct().collect(Collectors.toList());
		
		dropdown.forEach(c -> {
			List<CompanyPlantMappingDTO> plantList = compPlantList.stream().filter(cp -> cp.getCompanyCode().equals(c.getCompanyCode())).collect(Collectors.toList());
			c.setPlantDropdown(plantList.stream().sorted(Comparator.comparing(CompanyPlantMappingDTO::getPlantName)).map(p -> {
				PlantDropdownDTO pd = new PlantDropdownDTO();
				pd.setPlantCode(p.getPlantCode());
				pd.setPlantName(p.getPlantName());
				return pd;
			}).collect(Collectors.toList()));
		});
		
		return dropdown;
	}
	
	public List<FunctionPermissionDTO> getFunctionPermissionByUserId(String userId) {
		List<FunctionRoleMappingDTO> funcRoleList = customAuthRepository.findFunctionPermissionByUserId(userId);

		List<String> funcList = funcRoleList.stream().map(FunctionRoleMappingDTO::getFunctionName).distinct()
				.collect(Collectors.toList());

		return funcList.stream().map(f -> {
			FunctionPermissionDTO fp = new FunctionPermissionDTO();
			fp.setFunctionName(f);
			fp.setPermission(funcRoleList.stream().filter(fr -> fr.getFunctionName().equals(f))
					.map(FunctionRoleMappingDTO::getCompoName).collect(Collectors.toList()));
			return fp;
		}).collect(Collectors.toList());
	}
}
