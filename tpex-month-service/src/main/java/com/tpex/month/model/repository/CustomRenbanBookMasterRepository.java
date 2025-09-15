package com.tpex.month.model.repository;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tpex.month.model.dto.MixedVesselBooking;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomRenbanBookMasterRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String VAN_MONTH = "vanMonth%d";
	private static final String BOOK_NO = "bookNo%d";
	
	public List<MixedVesselBooking> findDuplicateBookNo(List<MixedVesselBooking> list){
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource param = new MapSqlParameterSource();
		
		sql.append("SELECT CONT_VAN_MTH, BOOK_NO FROM TB_R_MTH_RENBAN_BOOKING_H"
				+ " WHERE (CONT_VAN_MTH, BOOK_NO) IN (");
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				sql.append(",");
			sql.append("(:" + String.format(VAN_MONTH, i) + ",:" + String.format(BOOK_NO, i) + ")");
			MixedVesselBooking in = list.get(i);
			param.addValue(String.format(VAN_MONTH, i), in.getVanningMonth()).addValue(String.format(BOOK_NO, i), in.getBookingNo());
		}
		sql.append(") ");
		
		return namedParameterJdbcTemplate.query(sql.toString(), param, (rs, rowNum) -> {
			MixedVesselBooking out = new MixedVesselBooking();
			out.setVanningMonth(rs.getString("CONT_VAN_MTH"));
			out.setBookingNo(rs.getString("BOOK_NO"));
			return out;
		});
	}
}
