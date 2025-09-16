package com.tpex.batchjob.bins011;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class Bins011RowMapper implements RowMapper<Bins011OutputDto> {

	@Override
	public Bins011OutputDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		Bins011OutputDto bins006OutputDto = new Bins011OutputDto();

		bins006OutputDto.setRecId(rs.getString("REC_ID"));
		bins006OutputDto.setInvNo(rs.getString("INV_NO"));
		bins006OutputDto.setInvDate(rs.getString("INV_DT"));
		bins006OutputDto.setAicoFlg(rs.getString("AICO_FLG"));
		bins006OutputDto.setInvAmt(rs.getString("INV_AMT"));
		bins006OutputDto.setGrossWt(rs.getString("GROSS_WT"));
		bins006OutputDto.setPartNo(rs.getString("PART_NO"));
		bins006OutputDto.setNetWt(rs.getString("NET_WT"));
		bins006OutputDto.setNoCases(rs.getString("NO_CASES"));
		bins006OutputDto.setQty(rs.getString("QTY"));
		bins006OutputDto.setFob(rs.getString("FOB"));
		bins006OutputDto.setCfCd(rs.getString("CF_CD"));
		bins006OutputDto.setSeries(rs.getString("SERIES"));
		bins006OutputDto.setPckMth(rs.getString("PCK_MTH"));
		bins006OutputDto.setCoCd(rs.getString("CO_CD"));

		return bins006OutputDto;
	}

}
