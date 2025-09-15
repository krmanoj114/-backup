package com.tpex.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.tpex.dto.InterfaceSetupDTO;
import com.tpex.util.GlobalConstants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class Binf005CustomRepository {

	private static final String IN_USER_ID = "in_user_id";
	private static final String IN_CMP_CD = "in_cmp_cd";
	private static final String IN_CORRELATION_ID = "in_correlation_id";
	private static final String IN_PROGRAM_ID = "in_program_id";
	private static final String IN_EXECUTE_DATE = "in_execute_date";
	private static final String IN_PROC_CTRL_ID = "in_proc_ctrl_id";
	private static final String IN_PROC_NAME = "in_proc_name";
	private static final String OUT_IS_ERROR = "out_is_error";
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private final JdbcTemplate jdbcTemplate;
	
	private SimpleJdbcCall simpleJdbcCall;
	
	public String getIsoContNo(String vanningDate, String destinationCode, String etd1,
			String contSno){
		
		List<String> result;
		String sql = "SELECT TRIM(ISO_CONT_NO) as isoContNo "
				+ " FROM TB_M_ISO_CONTAINER "
				+ " WHERE CONT_VAN_MTH = SUBSTR(:vanningDate, 1, 6) "
				+ " AND CONT_DST_CD = :destinationCode "
				+ " AND ETD1 = STR_TO_DATE(:etd1,'%Y%m%d') "
				+ " AND CONT_SNO = :contSno "
				+ " LIMIT 1 ";
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("vanningDate", vanningDate)
				.addValue("destinationCode", destinationCode)
				.addValue("etd1", etd1)
				.addValue("contSno", contSno);
		
		result = namedParameterJdbcTemplate.queryForList(sql, param, String.class);
		
		return CollectionUtils.isNotEmpty(result) ? result.get(0) : GlobalConstants.BLANK;
	}
	
	public InterfaceSetupDTO querySetupByBatchId(String batchId) {
		
		List<InterfaceSetupDTO> result;
		String sql = "SELECT "
				+ " FILE_NM as fileNm , "
				+ " RPAD(SUBSTR(INTR_NM, 1, 7), 7, ' ') as intrNm , "
				+ " RPAD(SUBSTR(FROM_SYS, 1, 3), 3, ' ') as fromSys , "
				+ " RPAD(SUBSTR(TO_SYS, 1, 3), 3, ' ') as toSys , "
				+ " RPAD(DATA_LEN , 5, '0') as dataLen , "
				+ " CMP_CD as cmpCd , "
				+ " INTERFACE_TYPE as interfaceType "
				+ " FROM TB_M_INTERFACE_SETUP "
				+ " WHERE BATCH_ID = :batchId ";
		
		result = namedParameterJdbcTemplate.query(sql
				, new MapSqlParameterSource().addValue("batchId", batchId)
				, new BeanPropertyRowMapper<>(InterfaceSetupDTO.class));
		
		return CollectionUtils.isNotEmpty(result) ? result.get(0) : new InterfaceSetupDTO();
	}
	
	public String getValidPrivilegeRange() {
		
		List<String> result;
		String sql = " SELECT IFNULL(GROUP_CONCAT(PRIV_MAP_CD ORDER BY PRIV_MAP_CD SEPARATOR ''), '') as privMapCd "
				+ " FROM TB_M_PRIVILEGE_TYPE ";
		
		result = namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource(), String.class);
		
		return CollectionUtils.isNotEmpty(result) ? result.get(0) : GlobalConstants.BLANK;
	}
	
	public String getLocalPrivilegeCode(String aicoFlag) {
		
		List<String> result;
		String sql = " SELECT PRIV_CD as privCd "
				+ " FROM TB_M_PRIVILEGE_TYPE "
				+ " WHERE PRIV_MAP_CD LIKE :aicoFlag ";
		
		result = namedParameterJdbcTemplate.queryForList(sql
				, new MapSqlParameterSource().addValue("aicoFlag", "%" + aicoFlag + "%")
				, String.class);
		
		return CollectionUtils.isNotEmpty(result) ? result.get(0) : GlobalConstants.BLANK;
	}
	
	public String getPartSeriesName(String cfCd, String dstCd, String rexpCd) {
		List<String> result;
		String sql = "SELECT SRS_NM "
				+ " FROM TB_M_CAR_FAMILY_DESTINATION "
				+ " WHERE CF_CD  = :cfCd "
				+ " AND DST_CD = :dstCd "
				+ " AND REXP_CD= :rexpCd ";
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("cfCd", cfCd)
				.addValue("dstCd", dstCd)
				.addValue("rexpCd", rexpCd);
		
		result = namedParameterJdbcTemplate.queryForList(sql, param, String.class);
		
		return CollectionUtils.isNotEmpty(result) ? result.get(0) : GlobalConstants.BLANK;
	}
	
	public Boolean callBinf006A(LocalDateTime executeDate, String programId, String correlationId, String compCode, Integer procCtrlId, String procName) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_BINF006_A");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_PROGRAM_ID, programId)
				.addValue(IN_CORRELATION_ID, correlationId)
				.addValue(IN_CMP_CD, compCode)
				.addValue(IN_PROC_CTRL_ID, procCtrlId)
				.addValue(IN_PROC_NAME, procName);
		
		Map<String, Object> out = simpleJdbcCall.execute(in);
		return out.get(OUT_IS_ERROR) != null && (boolean) out.get(OUT_IS_ERROR);
	}
	
	public void processContTemp(LocalDateTime executeDate, String compCode, String userId) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_PROCESS_CONT_TEMP");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_CMP_CD, compCode)
				.addValue(IN_USER_ID, userId);
		
		simpleJdbcCall.execute(in);
	}
	
	public void processModuleTemp(LocalDateTime executeDate, String compCode, String userId) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_PROCESS_MODULE_TEMP");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_CMP_CD, compCode)
				.addValue(IN_USER_ID, userId);
		
		simpleJdbcCall.execute(in);
	}
	
	public void processPartTemp(LocalDateTime executeDate, String compCode, String userId) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_PROCESS_PART_TEMP");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_CMP_CD, compCode)
				.addValue(IN_USER_ID, userId);
		
		simpleJdbcCall.execute(in);
	}
	
	public void checkFclLcl(LocalDateTime executeDate) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_CHECK_FCL_LCL");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate);
		
		simpleJdbcCall.execute(in);
	}
	
	public void processInvVin(LocalDateTime executeDate, String compCode, String userId) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_PROCESS_INV_VIN");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_CMP_CD, compCode)
				.addValue(IN_USER_ID, userId);
		
		simpleJdbcCall.execute(in);
	}
	
	public void updInvAico(LocalDateTime executeDate, String compCode) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_UPD_INV_AICO");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_CMP_CD, compCode);
		
		simpleJdbcCall.execute(in);
	}
	
	public void recalContModWt(LocalDateTime executeDate, String compCode) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_RECAL_CONT_MOD_WT");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_CMP_CD, compCode);
		
		simpleJdbcCall.execute(in);
	}
	
	public void updDlyPxPSrsNm(LocalDateTime executeDate, String compCode) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_UPD_DLY_PXP_SRS_NM");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_CMP_CD, compCode);
		
		simpleJdbcCall.execute(in);
	}
	
	public Boolean updVltActDtTm(LocalDateTime executeDate, String programId, String correlationId, String compCode, Integer procCtrlId, String procName) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("P_UPD_VLT_ACT_DT_TM");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_PROGRAM_ID, programId)
				.addValue(IN_CORRELATION_ID, correlationId)
				.addValue(IN_CMP_CD, compCode)
				.addValue(IN_PROC_CTRL_ID, procCtrlId)
				.addValue(IN_PROC_NAME, procName);
		
		Map<String, Object> out = simpleJdbcCall.execute(in);
		return out.get(OUT_IS_ERROR) != null && (boolean) out.get(OUT_IS_ERROR);
	}

	public void genLotPartShortageInfo(LocalDateTime executeDate, String programId, String correlationId, String compCode, Integer procCtrlId, String procName) {
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("GEN_LOT_PART_SHORTAGE_INFO");
		
		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue(IN_EXECUTE_DATE, executeDate)
				.addValue(IN_PROGRAM_ID, programId)
				.addValue(IN_CORRELATION_ID, correlationId)
				.addValue(IN_CMP_CD, compCode)
				.addValue(IN_PROC_CTRL_ID, procCtrlId)
				.addValue(IN_PROC_NAME, procName);
		
		// update batch parameter in procedure
		simpleJdbcCall.execute(in);
	}
	
}
