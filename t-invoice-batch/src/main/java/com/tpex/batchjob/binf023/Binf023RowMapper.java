package com.tpex.batchjob.binf023;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
public class Binf023RowMapper implements RowMapper<Binf023OutputDto> {
    @Override
    public Binf023OutputDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Binf023OutputDto binf023OutputDto = new Binf023OutputDto();

        binf023OutputDto.setRecordId("D");
        binf023OutputDto.setPackingMth(rs.getString("PKG_MTH"));
        binf023OutputDto.setRequireDt(rs.getString("REQ_DT"));
        binf023OutputDto.setContainerSize(rs.getString("CONT_TYP"));
        binf023OutputDto.setKCST2DelryDt(rs.getString("KCST_DT"));
        binf023OutputDto.setInsDt(rs.getString("INSPEC_DT"));
        binf023OutputDto.setEtd1(rs.getString("tETD"));
        binf023OutputDto.setShpCmpnyCd(rs.getString("SHP_COMP_CD"));
        binf023OutputDto.setShpCmpnyNm(rs.getString("SHP_COMP_NM"));
        binf023OutputDto.setBookNo(rs.getString("BOOKING_NO"));
        binf023OutputDto.setVesselNm1(rs.getString("VESSEL"));
        binf023OutputDto.setRenbanCd(rs.getString("RENBAN_CD"));
        binf023OutputDto.setDestCd(rs.getString("DEST_CD"));
        binf023OutputDto.setDestName(rs.getString("DESTINATION"));
        binf023OutputDto.setVanningPlant(rs.getString("VAN_PLNT"));
        binf023OutputDto.setRemark(rs.getString("REMARK"));

        return binf023OutputDto;
    }
}
