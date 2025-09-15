package com.tpex.repository;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.dto.InvoiceErrorInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class Binf016CustomRepository {
	
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String INV_NO = "invNo";
	
	public List<InvoiceErrorInfo> getInvoiceError(String invNo, String haisenNo, String haisenYearMonth) {
		
		List<InvoiceErrorInfo> result;
		String sql = "SELECT FINAL_QUERY.INV_NO as invNo, FINAL_QUERY.ERROR_ID as errorCode\r\n"
				+ "FROM (\r\n"
				+ "    SELECT FILTERED_QUERY.INV_NO, FILTERED_QUERY.ERROR_ID, \r\n"
				+ "    ROW_NUMBER() OVER (PARTITION BY FILTERED_QUERY.INV_NO ORDER BY FILTERED_QUERY.INV_NO, FILTERED_QUERY.ERROR_ID ) SR_NO  \r\n"
				+ "    FROM (\r\n"
				+ "        SELECT t.INV_NO, t.ERROR_ID\r\n"
				+ "        FROM (\r\n"
				+ "            SELECT INNER_QUERY.INV_NO,\r\n"
				+ "                   CASE WHEN INNER_QUERY.AICO_FLG = '3' AND (INNER_QUERY.HS_CD1 IS NULL OR INNER_QUERY.ORG_CRITERIA1 IS NULL) THEN 1\r\n"
				+ "                        WHEN IFNULL(INNER_QUERY.HS_CD1, '*') <> IFNULL(INNER_QUERY.HS_CD, '*') THEN 2\r\n"
				+ "                        WHEN IFNULL(INNER_QUERY.ORG_CRITERIA1, '*') <> IFNULL(INNER_QUERY.ORG_CRITERIA, '*') THEN 3\r\n"
				+ "                        ELSE 0\r\n"
				+ "                   END AS ERROR_ID\r\n"
				+ "            FROM (\r\n"
				+ "                SELECT DISTINCT PARTS.INV_NO, PARTS.AICO_FLG, PARTS.HS_CD as HS_CD1, COE.HS_CD, PARTS.ORG_CRITERIA as ORG_CRITERIA1, coe.ORG_CRITERIA\r\n"
				+ "                FROM (\r\n"
				+ "                    SELECT CF_CD,\r\n"
				+ "                           SERIES,\r\n"
				+ "                           PART_NO,\r\n"
				+ "                           IMP_CMP,\r\n"
				+ "                           EXP_CMP,\r\n"
				+ "                           PART_FLG,\r\n"
				+ "                           DATE_FORMAT(PART_EFF_DT, '%Y%m') FROM_MTH,\r\n"
				+ "                           DATE_FORMAT(PART_EXP_DT, '%Y%m') TO_MTH,\r\n"
				+ "                           HS_CD,\r\n"
				+ "                           ORG_CRITERIA\r\n"
				+ "                    FROM TB_M_COE_CEPT\r\n"
				+ "                ) COE\r\n"
				+ "                left JOIN (\r\n"
				+ "                    SELECT DISTINCT IND.INV_NO,\r\n"
				+ "                                   INP.CF_CD INP_CF_CD,\r\n"
				+ "                                   INP.SERIES INP_SERIES,\r\n"
				+ "                                   SUBSTRING(INP.PART_NO, 1, 10) INP_PART_NO,\r\n"
				+ "                                   IND.BUYER IND_BUYER,\r\n"
				+ "                                   FD.BUY_CD INV_EXP_CMP,\r\n"
				+ "                                   INP.PCK_MTH INP_PCK_MTH,\r\n"
				+ "                                   INP.AICO_FLG,\r\n"
				+ "                                   INP.HS_CD,\r\n"
				+ "                                   INP.ORG_CRITERIA\r\n"
				+ "                    FROM TB_R_INV_INVOICE_H IND,\r\n"
				+ "                         TB_R_INV_PART_D INP,\r\n"
				+ "                         TB_M_FINAL_DESTINATION FD,\r\n"
				+ "                         TB_M_PROD_GRP IPG,\r\n"
				+ "                         TB_M_PORT PM,\r\n"
				+ "                         TB_M_TPT TM\r\n"
				+ "                    WHERE IND.INV_NO = INP.INV_NO\r\n"
				+ "                        AND IND.IXOS_FLG = 'S'\r\n"
				+ "                        AND IND.PRINT_FLG = 'Y'\r\n"
				+ "                        AND IND.SC_INV_FLG = 'Y'\r\n"
				+ "                        AND INP.EXP_CD = FD.DST_CD\r\n"
				+ "                        AND IND.PROD_GRP_CD = IPG.PROD_GRP_CD\r\n"
				+ "                        AND IND.DEST_PORT_CD = PM.CD\r\n"
				+ "                        AND IND.TRNSPRT_CD = TM.TPT_CD\r\n"
				+ "                        AND IND.INV_NO = ifnull(:invNo, IND.INV_NO) "
				+ "                        AND IND.HAISEN_NO = ifnull(:haisenNo, IND.HAISEN_NO) "
				+ "                        AND IND.HAISEN_YR_MTH = ifnull(:haisenYearMonth, IND.HAISEN_YR_MTH) "
				+ "                ) PARTS\r\n"
				+ "                 ON PARTS.INP_CF_CD = COE.CF_CD\r\n"
				+ "                            AND PARTS.INP_SERIES = COE.SERIES\r\n"
				+ "                            AND PARTS.INP_PART_NO = COE.PART_NO\r\n"
				+ "                            AND PARTS.IND_BUYER = COE.IMP_CMP\r\n"
				+ "                            AND PARTS.INV_EXP_CMP = COE.EXP_CMP\r\n"
				+ "                            AND PARTS.INP_PCK_MTH >= COE.FROM_MTH\r\n"
				+ "                            AND PARTS.INP_PCK_MTH <= COE.TO_MTH\r\n"
				+ "            \r\n"
				+ "        ) INNER_QUERY\r\n"
				+ "       )t "
				+ "    ) FILTERED_QUERY\r\n"
				+ ") FINAL_QUERY\r\n"
				+ "WHERE SR_NO = 1 AND INV_NO is not null;";		
		
		Map<String, Object> param = new HashMap<>();
		param.put(INV_NO, invNo);
		param.put("haisenNo", haisenNo);
		param.put("haisenYearMonth", haisenYearMonth);
		
		result = namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper<>(InvoiceErrorInfo.class));
		
		return result;
	}
	
	public List<InvoiceErrorInfo> getPartNetWeight(String invNo, String haisenNo, String haisenYearMonth) {
		
		List<InvoiceErrorInfo> result;
		String sql = "SELECT DISTINCT t.INV_NO as invNo \r\n"
				+ "                      FROM(\r\n"
				+ "  SELECT A.INV_NO,\r\n"
				+ "  A.INV_DT,\r\n"
				+ "  A.CF_CD,\r\n"
				+ "  A.PART_NO,\r\n"
				+ "  COUNT(DISTINCT A.NET_WT)\r\n"
				+ "    FROM TB_R_INV_PART_D A,\r\n"
				+ "  TB_R_INV_INVOICE_H B,\r\n"
				+ "  TB_M_PROD_GRP D,\r\n"
				+ "  TB_M_PORT E,\r\n"
				+ "  TB_M_TPT F\r\n"
				+ "   WHERE B.INV_NO      = A.INV_NO\r\n"
				+ "     AND B.IXOS_FLG    = 'S'\r\n"
				+ "     AND B.PRINT_FLG   = 'Y'\r\n"
				+ "     AND D.PROD_GRP_CD = B.PROD_GRP_CD\r\n"
				+ "     AND E.CD           = B.DEST_PORT_CD\r\n"
				+ "     AND F.TPT_CD       = B.TRNSPRT_CD\r\n"
				+ "     AND B.INV_NO = ifnull(:invNo, B.INV_NO) "
				+ "     AND B.HAISEN_NO = ifnull(:haisenNo, B.HAISEN_NO) "
				+ "     AND B.HAISEN_YR_MTH = ifnull(:haisenYearMonth, B.HAISEN_YR_MTH) "
				+ "    GROUP BY A.INV_NO, \r\n"
				+ "  A.INV_DT, \r\n"
				+ "  A.CF_CD, \r\n"
				+ "  A.PART_NO\r\n"
				+ "  HAVING COUNT(DISTINCT A.NET_WT) > 1\r\n"
				+ " )t";
		
		Map<String, Object> param = new HashMap<>();
		param.put(INV_NO, invNo);
		param.put("haisenNo", haisenNo);
		param.put("haisenYearMonth", haisenYearMonth);
		
		result = namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper<>(InvoiceErrorInfo.class));
		
		return result;
	}

	@Transactional
	public void updateInvIxosFlag(String invNo, String ixosFlag, Date date) {

		String sql = "UPDATE tb_r_inv_invoice_h SET IXOS_FLG = :ixosFlag, IXOS_SEND_DT = :ixosSendDate WHERE INV_NO = :invNo";

		MapSqlParameterSource param = new MapSqlParameterSource().addValue(INV_NO, invNo)
				.addValue("ixosFlag", ixosFlag)
				.addValue("ixosSendDate", date);

		namedParameterJdbcTemplate.update(sql, param);
	}
	
}
