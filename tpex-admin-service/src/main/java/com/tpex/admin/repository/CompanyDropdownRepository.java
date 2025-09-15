package com.tpex.admin.repository;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tpex.admin.dto.CompanyPlantMappingDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CompanyDropdownRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<CompanyPlantMappingDTO> findCompanyAndPlantDropdownByUser(String userId){
		String sql = "select tmc.CMP_CD as companyCode, tmc.CMP_NAME as companyName "
				+ " , tmp.PLANT_CD as plantCode, tmp.PLANT_NAME as plantName "
				+ " from TB_M_COMPANY_PLANT_MAPPING tmcpm "
				+ " inner join TB_M_COMPANY tmc on tmc.CMP_CD = tmcpm.CMP_CD "
				+ " inner join TB_M_PLANT tmp on tmp.PLANT_CD = tmcpm.PLANT_CD "
				+ " where tmcpm.USER_ID = :userId";
		
		MapSqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		
		return namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper<>(CompanyPlantMappingDTO.class));
	}
}
