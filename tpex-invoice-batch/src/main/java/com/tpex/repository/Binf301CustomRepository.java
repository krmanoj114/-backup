package com.tpex.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class Binf301CustomRepository {
	
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final JdbcTemplate jdbcTemplate;
	
	public void deleteStagingData(String compCode) {

		String sql = "DELETE FROM TB_S_ENG_VIN WHERE CMP_CD = :compCode";

		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("compCode", compCode);

		namedParameterJdbcTemplate.update(sql, param);
	}

	public void deleteEngMstData(String compCode) {
		
		String sql = "DELETE FROM TB_M_ENG_VIN WHERE CMP_CD = :compCode";

		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("compCode", compCode);

		namedParameterJdbcTemplate.update(sql, param);
	}

}
