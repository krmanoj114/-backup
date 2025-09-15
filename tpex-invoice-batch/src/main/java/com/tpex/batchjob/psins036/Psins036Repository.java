package com.tpex.batchjob.psins036;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class Psins036Repository {
	
	private static final String P_I_V_INVOICE_NO = "p_i_v_invoice_no";
	private static final String P_I_V_USER_ID = "p_i_v_user_id";
	private static final String P_I_V_CMP_CD = "p_i_v_cmp_cd";
	
	private SimpleJdbcCall simpleJdbcCall;
	private final JdbcTemplate jdbcTemplate;
	
	public Map<String, Object> callPsinsRep(String invoiceNo, String userId, String companyCode) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("PSINS036");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(P_I_V_INVOICE_NO, invoiceNo)
				.addValue(P_I_V_USER_ID, userId)
				.addValue(P_I_V_CMP_CD, companyCode);
		
		return simpleJdbcCall.execute(in);
	}

}
