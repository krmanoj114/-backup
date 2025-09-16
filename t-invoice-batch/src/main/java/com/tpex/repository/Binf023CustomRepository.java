package com.tpex.repository;

import com.tpex.util.GlobalConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class Binf023CustomRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    MapSqlParameterSource param = new MapSqlParameterSource();

    public String getVanningMonth(){
        List<String> result;
        String sql = "SELECT MAX(PKG_MTH) FROM TB_R_VPR_MTH_MODULE WHERE PKG_MTH <> '******'";

        result = namedParameterJdbcTemplate.queryForList(sql, param, String.class);

        return CollectionUtils.isNotEmpty(result) ? result.get(0) : GlobalConstants.BLANK;
    }

    public void updateVesselBookingMaster(File file, String vanMonth) {
        String mstrSubmitTime = file.getName().substring(8,22);
        String query = "UPDATE \tTB_R_MTH_RENBAN_BOOKING_H\n" +
                "SET \t\t\n" +
                "CB_FLG = 'Y',\n" +
                "UPD_BY = ':mstrUserId_' ,\n" +
                "UPD_DT = STR_TO_DATE(':mstrSubmitTime_', '%Y%m%d%H%i%s')\n" +
                "WHERE\n" +
                "CONT_VAN_MTH = ':mstrVanMonth_'";

        if(mstrSubmitTime!=null && vanMonth!=null){
            query = query.replace(":mstrVanMonth_", vanMonth).replace(":mstrUserId_","TPEX").replace(":mstrSubmitTime_", mstrSubmitTime);
            namedParameterJdbcTemplate.update(query, param);
        }

    }
}

