package com.tpex.auth.repository;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tpex.auth.dto.CompanyPlantMappingDTO;
import com.tpex.auth.dto.FunctionRoleMappingDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomAuthRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String USER_ID = "userId";
	
	public List<CompanyPlantMappingDTO> findCompanyAndPlantDropdownByUserId(String userId){
		String sql = "select tmc.CMP_CD as companyCode, tmc.CMP_NAME as companyName "
				+ " , tmp.PLANT_CD as plantCode, tmp.PLANT_NAME as plantName "
				+ " from TB_M_COMPANY_PLANT_MAPPING tmcpm "
				+ " inner join TB_M_COMPANY tmc on tmc.CMP_CD = tmcpm.CMP_CD "
				+ " inner join TB_M_PLANT tmp on tmp.PLANT_CD = tmcpm.PLANT_CD "
				+ " where tmcpm.USER_ID = :userId";
		
		return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(USER_ID, userId), new BeanPropertyRowMapper<>(CompanyPlantMappingDTO.class));
	}
	
	public List<FunctionRoleMappingDTO> findFunctionPermissionByUserId(String userId){
		String sql = " select distinct tmf.FUNC_NAME as functionName , tmfc.COMPO_NAME as compoName "
				+ " from tb_m_role_function_mapping tmrfm "
				+ " inner join tb_m_role r on r.ROLE_ID = tmrfm.ROLE_ID "
				+ " inner join tb_m_function tmf on tmf.FUNC_ID = tmrfm.FUNC_ID "
				+ " inner join tb_m_function_component tmfc on tmfc.FUNC_COMPO_ID = tmrfm.FUNC_COMPO_ID "
				+ " where r.ROLE_ID in (select tmrum.ROLE_ID "
				+ " from tb_m_role_user_mapping tmrum "
				+ " where tmrum.USER_ID = :userId) ";
		
		return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(USER_ID, userId), new BeanPropertyRowMapper<>(FunctionRoleMappingDTO.class));
	}
	
	public List<String> findRoleNameByUserId(String userId) {
		String sql = "select r.ROLE_NAME "
				+ " from tb_m_role_user_mapping tmrum "
				+ " inner join tb_m_role r on r.ROLE_ID = tmrum.ROLE_ID "
				+ " where tmrum.USER_ID = :userId ";
		
		return namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource().addValue(USER_ID, userId), String.class);
	}

}
