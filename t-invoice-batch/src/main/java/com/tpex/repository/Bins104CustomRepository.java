package com.tpex.repository;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tpex.util.GlobalConstants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class Bins104CustomRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public String getValidPrivilegeRange() {

		List<String> result;
		String sql = " SELECT IFNULL(GROUP_CONCAT(PRIV_CD ORDER BY PRIV_CD SEPARATOR ''), '') as privCd "
				+ " FROM TB_M_PRIVILEGE_TYPE ";

		result = namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource(), String.class);

		return CollectionUtils.isNotEmpty(result) ? result.get(0) : GlobalConstants.BLANK;
	}
	
	public void deleteStagingData(String compCode) {

		String sql = "DELETE FROM TB_S_COE_CEPT WHERE CMP_CD = :compCode";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("compCode", compCode);

		namedParameterJdbcTemplate.update(sql, param);
	}

	
	public void deletePkgSpecData(String compCode) {

		String sql = "DELETE FROM TB_M_COE_CEPT WHERE CMP_CD = :compCode";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue("compCode", compCode);

		namedParameterJdbcTemplate.update(sql, param);
	}

}
