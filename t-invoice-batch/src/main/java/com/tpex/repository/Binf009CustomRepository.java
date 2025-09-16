package com.tpex.repository;

import javax.transaction.Transactional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class Binf009CustomRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public void deleteStagingData(String compCode) {

		String sql = "DELETE FROM TB_S_VPR_PKG_SPEC WHERE CMP_CD = :compCode";

		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("compCode", compCode);

		namedParameterJdbcTemplate.update(sql, param);
	}

	@Transactional
	public void deletePkgSpecData(String compCode) {

		String sql = "DELETE FROM TB_M_SPEC_VPR_PKG WHERE CMP_CD = :compCode";

		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("compCode", compCode);

		namedParameterJdbcTemplate.update(sql, param);
	}

	@Transactional
	public void insertPkgSpecData() {

		String sql = " INSERT INTO TB_M_SPEC_VPR_PKG "
				+ "( "
				+ "MOD_EXP_CD, "
				+ "SUP_PLNT_CD, "
				+ "SUP_DOC_CD, "
				+ "MOD_IMP_CD, "
				+ "MOD_RECV_PLNT, "
				+ "PART_NO, "
				+ "CF_CD, "
				+ "IMP_LN_CD, "
				+ "REXP_CD, "
				+ "LOT_CD, "
				+ "CASE_NO, "
				+ "MOD_CD, "
				+ "ORD_TY, "
				+ "BOX_NO, "
				+ "EFF_FROM, "
				+ "AICO_FLG, "
				+ "EFF_TO, "
				+ "PCK_PLNT, "
				+ "QTY_BOX, "
				+ "BOX_GROSS_WT, "
				+ "BOX_M3, "
				+ "IMP_DOCK_CD, "
				+ "PRT_NM2, "
				+ "PADD, "
				+ "BOX_MAT_CD, "
				+ "MAT_PRT_NO, "
				+ "MAT_SRC_CD, "
				+ "MAT_DOC_CD, "
				+ "MAT_ADD, "
				+ "BOX_MAT_QTY, "
				+ "MAT_CD1, "
				+ "MAT_PRT_NO1, "
				+ "MAT_SRC_CD1, "
				+ "MAT_DOC_CD1, "
				+ "MAT_ADD1, "
				+ "BOX_MAT_QTY1, "
				+ "MAT_CD2, "
				+ "MAT_PRT_NO2, "
				+ "MAT_SRC_CD2, "
				+ "MAT_DOC_CD2, "
				+ "MAT_ADD2, "
				+ "BOX_MAT_QTY2, "
				+ "MAT_CD3, "
				+ "MAT_PRT_NO3, "
				+ "MAT_SRC_CD3, "
				+ "MAT_DOC_CD3, "
				+ "MAT_ADD3, "
				+ "BOX_MAT_QTY3, "
				+ "MAT_CD4, "
				+ "MAT_PRT_NO4, "
				+ "MAT_SRC_CD4, "
				+ "MAT_DOC_CD4, "
				+ "MAT_ADD4, "
				+ "MAT_QTY4, "
				+ "MAT_CD5, "
				+ "MAT_PRT_NO5, "
				+ "MAT_SRC_CD5, "
				+ "MAT_DOC_CD5, "
				+ "MAT_ADD5, "
				+ "MAT_QTY5, "
				+ "MAT_CD6, "
				+ "MAT_PRT_NO6, "
				+ "MAT_SRC_CD6, "
				+ "MAT_DOC_CD6, "
				+ "MAT_ADD6, "
				+ "MAT_QTY6, "
				+ "MAT_CD7, "
				+ "MAT_PRT_NO7, "
				+ "MAT_SRC_CD7, "
				+ "MAT_DOC_CD7, "
				+ "MAT_ADD7, "
				+ "MAT_QTY7, "
				+ "MAT_CD8, "
				+ "MAT_PRT_NO8, "
				+ "MAT_SRC_CD8, "
				+ "MAT_DOC_CD8, "
				+ "MAT_ADD8, "
				+ "MAT_QTY8, "
				+ "MAT_CD9, "
				+ "MAT_PRT_NO9, "
				+ "MAT_SRC_CD9, "
				+ "MAT_DOC_CD9, "
				+ "MAT_ADD9, "
				+ "MAT_QTY9, "
				+ "LBL_OPT_CTL, "
				+ "PCK_ZONE_CD, "
				+ "STAK_ZONE_CD, "
				+ "KBN_NO, "
				+ "TMC_SEP_NO, "
				+ "TMC_BOX, "
				+ "TMC_KUMI, "
				+ "TMC_PAT, "
				+ "TMC_VLIC, "
				+ "TMC_CONT_SHI_KUB, "
				+ "TMC_BCK_KUB, "
				+ "TMC_BUN_HAK, "
				+ "TMC_KAN_CD, "
				+ "TMC_GEN_JYO_BUL, "
				+ "UPD_BY, "
				+ "UPD_DT, "
				+ "CMP_CD "
				+ ") "
				+ "SELECT "
				+ "MOD_EXP_CD, "
				+ "SUP_PLNT_CD, "
				+ "SUP_DOC_CD, "
				+ "MOD_IMP_CD, "
				+ "MOD_RECV_PLNT, "
				+ "PART_NO, "
				+ "CF_CD, "
				+ "IMP_LN_CD, "
				+ "REXP_CD, "
				+ "LOT_CD, "
				+ "CASE_NO, "
				+ "MOD_CD, "
				+ "ORD_TY, "
				+ "BOX_NO, "
				+ "EFF_FROM, "
				+ "AICO_FLG, "
				+ "EFF_TO, "
				+ "PCK_PLNT, "
				+ "QTY_BOX, "
				+ "BOX_GROSS_WT, "
				+ "BOX_M3, "
				+ "IMP_DOCK_CD, "
				+ "PRT_NM2, "
				+ "ADDI, "
				+ "BOX_MAT_CD, "
				+ "MAT_PRT_NO, "
				+ "MAT_SRC_CD, "
				+ "MAT_DOC_CD, "
				+ "MAT_ADD, "
				+ "BOX_MAT_QTY, "
				+ "MAT_CD1, "
				+ "MAT_PRT_NO1, "
				+ "MAT_SRC_CD1, "
				+ "MAT_DOC_CD1, "
				+ "MAT_ADD1, "
				+ "BOX_MAT_QTY1, "
				+ "MAT_CD2, "
				+ "MAT_PRT_NO2, "
				+ "MAT_SRC_CD2, "
				+ "MAT_DOC_CD2, "
				+ "MAT_ADD2, "
				+ "BOX_MAT_QTY2, "
				+ "MAT_CD3, "
				+ "MAT_PRT_NO3, "
				+ "MAT_SRC_CD3, "
				+ "MAT_DOC_CD3, "
				+ "MAT_ADD3, "
				+ "BOX_MAT_QTY3, "
				+ "MAT_CD4, "
				+ "MAT_PRT_NO4, "
				+ "MAT_SRC_CD4, "
				+ "MAT_DOC_CD4, "
				+ "MAT_ADD4, "
				+ "MAT_QTY4, "
				+ "MAT_CD5, "
				+ "MAT_PRT_NO5, "
				+ "MAT_SRC_CD5, "
				+ "MAT_DOC_CD5, "
				+ "MAT_ADD5, "
				+ "MAT_QTY5, "
				+ "MAT_CD6, "
				+ "MAT_PRT_NO6, "
				+ "MAT_SRC_CD6, "
				+ "MAT_DOC_CD6, "
				+ "MAT_ADD6, "
				+ "MAT_QTY6, "
				+ "MAT_CD7, "
				+ "MAT_PRT_NO7, "
				+ "MAT_SRC_CD7, "
				+ "MAT_DOC_CD7, "
				+ "MAT_ADD7, "
				+ "MAT_QTY7, "
				+ "MAT_CD8, "
				+ "MAT_PRT_NO8, "
				+ "MAT_SRC_CD8, "
				+ "MAT_DOC_CD8, "
				+ "MAT_ADD8, "
				+ "MAT_QTY8, "
				+ "MAT_CD9, "
				+ "MAT_PRT_NO9, "
				+ "MAT_SRC_CD9, "
				+ "MAT_DOC_CD9, "
				+ "MAT_ADD9, "
				+ "MAT_QTY9, "
				+ "LBL_OPT_CTL, "
				+ "PCK_ZONE_CD, "
				+ "STAK_ZONE_CD, "
				+ "KBN_NO, "
				+ "TMC_SEP_NO, "
				+ "TMC_BOX, "
				+ "TMC_KUMI, "
				+ "TMC_PAT, "
				+ "TMC_VLIC, "
				+ "TMC_CONT_SHI_KUB, "
				+ "TMC_BCK_KUB, "
				+ "TMC_BUN_HAK, "
				+ "TMC_KAN_CD, "
				+ "TMC_GEN_JYO_BUL, "
				+ "UPD_BY, "
				+ "UPD_DT, "
				+ "CMP_CD "
				+ "FROM TB_S_VPR_PKG_SPEC";

		jdbcTemplate.update(sql);

	}

}
