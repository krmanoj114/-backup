package com.tpex.month.model.repository;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tpex.month.model.dto.MixedVesselBooking;
import com.tpex.month.model.dto.VesselBookingMasterSearchRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearch;
import com.tpex.month.util.ConstantUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
@SuppressWarnings("squid:S1192")
public class CustomVesselBookingMasterRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String VANNING_MONTH = "vanningMonth";
	private static final String DESTINATION_CODE = "destinationCode";
	private static final String ETD_FROM = "etdFrom";
	private static final String ETD_TO = "etdTo";
	private static final String ETD = "etd";
	private static final String SHIPPING_COMPANY = "shippingCompany";
	private static final String KEIHEN_REV_NO = "keihenRevNo";
	
	public Page<VesselBookingMasterSearch> findRenbanBookingData(VesselBookingMasterSearchRequest input, Pageable pagination) {
		String sqlSelect = "SELECT TAB_1.PKG_MTH, TAB_1.CONT_DST_CD, DATE_FORMAT(TAB_1.ETD1,'%d/%m/%Y') ETD, DATE_FORMAT(TAB_1.ETA,'%d/%m/%Y') ETA "
				+ " , TAB_1.CONT_20, TAB_1.CONT_40 , TAB_1.SHP_NM "
				+ " , TAB_1.CB_CD, (select CBM.CB_NM from TB_M_CUSTOM_BROCKER CBM where CBM.CB_CD = TAB_1.CB_CD) CB_NM "
				+ " , TAB_1.BOOK_NO, TAB_1.VESSEL, TAB_1.BOOK_STAT "
				+ " , DATE_FORMAT(TAB_1.VAN_END_DT,'%Y%m%d%H%i%s') VAN_END_DT, TAB_1.SR_NO, TAB_1.GROUP_ID   "
				+ " , SIGN(DATE_FORMAT(TAB_1.ETD1,'%Y%m') - DATE_FORMAT(SYSDATE(),'%Y%m') ) SIGN_FLAG "
				+ " , TAB_1.CONT_GRP_CD, TAB_1.UPD_DT, TAB_1.CB_FLG "
				+ " , CASE WHEN TAB_1.ETD1 < DATE_SUB(CURDATE(), INTERVAL DAYOFMONTH(CURDATE())-1 DAY) THEN true ELSE false END ETD_IS_PASSED "
				+ " , TAB_1.VESSEL OLD_VESSEL, TAB_1.CB_CD OLD_CB_CD, TAB_1.BOOK_NO OLD_BOOK_NO ";
		
		StringBuilder sqlFromWhere = new StringBuilder();
		sqlFromWhere.append(" FROM (  "
				+ " SELECT TAB_2.PKG_MTH, TAB_2.CB_CD, TAB_2.SHP_NM , "
				+ " TAB_2.CONT_40 , "
				+ " TAB_2.CONT_20 , "
				+ " TAB_2.BOOK_NO , TAB_2.ETD1 , TAB_2.ETA , TAB_2.VESSEL , TAB_2.BOOK_STAT , TAB_2.CONT_GRP_CD, "
				+ " MAX(TAB_2.VAN_END_DT) OVER ( PARTITION BY TAB_2.ETD1 , TAB_2.SHP_NM , TAB_2.GROUP_ID ) VAN_END_DT , "
				+ " ROW_NUMBER() OVER ( PARTITION BY TAB_2.ETD1 , TAB_2.SHP_NM , TAB_2.GROUP_ID "
				+ " ORDER BY TAB_2.ETD1 , TAB_2.SHP_NM , TAB_2.GROUP_ID ) SR_NO , "
				+ " TAB_2.GROUP_ID, TAB_2.UPD_DT, TAB_2.CB_FLG, TAB_2.CONT_DST_CD   "
				+ " FROM (  "
				+ " SELECT  A.PKG_MTH, B.CB_CD AS CB_CD, CASE WHEN B.SHP_NM_1 IS NULL THEN A.SHP_NM_1 ELSE B.SHP_NM_1 END SHP_NM   ,   "
				+ "       CASE WHEN B.CONT_40 IS NULL THEN A.CONT_40 ELSE B.CONT_40 END CONT_40  ,   "
				+ "       CASE WHEN B.CONT_20 IS NULL THEN A.CONT_20 ELSE B.CONT_20 END CONT_20  ,  "
				+ "       CASE WHEN B.BOOK_NO IS NULL THEN '' ELSE B.BOOK_NO END BOOK_NO   ,  "
				+ "       CASE WHEN B.ETD1 IS NULL THEN A.ETD_1 ELSE B.ETD1 END ETD1 ,  "
				+ "       CASE WHEN B.ETA IS NULL THEN A.ETA_1 ELSE B.ETA END ETA ,  "
				+ "       CASE WHEN B.VESSEL1 IS NULL THEN '' ELSE B.VESSEL1 END VESSEL  ,  "
				+ "       CASE WHEN B.VAN_END_DT IS NULL THEN A.VAN_END_DT ELSE B.VAN_END_DT END VAN_END_DT ,  "
				+ "       CASE WHEN B.CB_FLG IS NULL THEN 'Not Maintained' WHEN B.CB_FLG = 'N' THEN 'Not sent to CB' WHEN B.CB_FLG = 'Y' THEN 'Sent to CB' END  BOOK_STAT ,  "
				+ "       A.CONT_GRP_CD , "
				+ "       CASE WHEN UPD_DT IS NULL THEN '' ELSE UPD_DT END UPD_DT ,  "
				+ "       A.GROUP_ID, B.CB_FLG CB_FLG, A.CONT_DST_CD "
				+ " FROM   "
				+ "    ( SELECT   NRSM.EFF_TO_DT, ALL_VPR.PKG_MTH, (ALL_VPR.ETD_1 - interval PARA.PARA_VAL day) VAN_END_DT ,   "
				+ "               ALL_VPR.CONT_GRP_CD    ,   "
				+ "               ALL_VPR.SHP_NM_1       ,  "
				+ "               ALL_VPR.ETD_1          ,    "
				+ "               ALL_VPR.ETA_1          , "
				+ "               ALL_VPR.CONT_DST_CD,  "
				+ "               NRSM.GROUP_ID   ,  "
				+ "               SUM( CASE ALL_VPR.CONT_SIZE WHEN '40' THEN 1 ELSE 0 END ) OVER (  "
				+ "                     PARTITION BY ALL_VPR.ETD_1, ALL_VPR.SHP_NM_1, NRSM.GROUP_ID  ) AS CONT_40,  "
				+ "               SUM( CASE ALL_VPR.CONT_SIZE WHEN '20' THEN 1 ELSE 0 END ) OVER (  "
				+ "                     PARTITION BY ALL_VPR.ETD_1, ALL_VPR.SHP_NM_1, NRSM.GROUP_ID  ) AS CONT_20   "
				+ "       FROM     "
				+ "               ( "
				+ "               SELECT NVMC.PKG_MTH, NVMC.CONT_DST_CD, NVMC.ETD_1, NVMC.CONT_GRP_CD, NVMC.SHP_NM_1, NVMC.CONT_SIZE, IFNULL(NVMC.ETA_2,NVMC.ETA_1) ETA_1  "
				+ "               FROM   TB_R_VPR_MTH_CONTAINER NVMC "
				+ "               WHERE  PKG_MTH = :" + VANNING_MONTH);
		appendWhereIfHas(sqlFromWhere, " AND NVMC.CONT_DST_CD = :" + DESTINATION_CODE, input.getDestinationCode());
		sqlFromWhere.append(" AND NVMC.CONT_TYP = 'F' "
				+ " AND    NOT EXISTS ( SELECT 1 "
				+ " FROM   TB_R_ETD_CONTAINER NEDC "
				+ " WHERE  NVMC.PKG_MTH     = NEDC.PKG_MTH "
				+ " AND    NVMC.CONT_DST_CD = NEDC.CONT_DST_CD "
				+ " AND    NVMC.ETD_1       = NEDC.OLD_ETD_1 "
				+ " AND    NEDC.ETD_1      <> NEDC.OLD_ETD_1 "
				+ " AND    NVMC.CONT_GRP_CD = NEDC.CONT_GRP_CD "
				+ " AND    NVMC.SHP_NM_1    = NEDC.SHP_NM_1 "
				+ " ) "
				+ " UNION  ALL "
				+ " SELECT NVMKC.PKG_MTH, NVMKC.CONT_DST_CD, NVMKC.ETD_1, NVMKC.CONT_GRP_CD, NVMKC.SHP_NM_1, NVMKC.CONT_SIZE, IFNULL(NVMKC.ETA_2,NVMKC.ETA_1) ETA_1 "
				+ " FROM   TB_R_VPR_MTH_KEIHEN_CONTAINER NVMKC "
				+ " WHERE  NVMKC.PKG_MTH = :" + VANNING_MONTH);
		appendWhereIfHas(sqlFromWhere, " AND NVMKC.CONT_DST_CD = :" + DESTINATION_CODE, input.getDestinationCode());
		sqlFromWhere.append(" AND NVMKC.CONT_TYP   = 'F' "
				+ " AND NOT EXISTS ( SELECT 1 "
				+ " FROM   TB_R_ETD_CONTAINER NEDC "
				+ " WHERE  NVMKC.PKG_MTH     = NEDC.PKG_MTH "
				+ " AND    NVMKC.CONT_DST_CD = NEDC.CONT_DST_CD "
				+ " AND    NVMKC.ETD_1       = NEDC.OLD_ETD_1 "
				+ " AND    NEDC.ETD_1      <> NEDC.OLD_ETD_1 "
				+ " AND    NVMKC.CONT_GRP_CD = NEDC.CONT_GRP_CD "
				+ " AND    NVMKC.SHP_NM_1    = NEDC.SHP_NM_1 "
				+ " ) "
				+ " AND    NOT EXISTS ( SELECT 1 "
				+ " FROM   TB_R_VPR_MTH_CONTAINER NVMC "
				+ " WHERE  NVMC.PKG_MTH = NVMKC.PKG_MTH "
				+ " AND    NVMC.CONT_DST_CD = NVMKC.CONT_DST_CD "
				+ " AND    NVMC.ETD_1       = NVMKC.ETD_1 "
				+ " AND    NVMC.CONT_GRP_CD = NVMKC.CONT_GRP_CD "
				+ " AND    NVMC.SHP_NM_1    = NVMKC.SHP_NM_1 "
				+ " AND    NVMC.CONT_TYP    = 'F' "
				+ " ) "
				+ " UNION  ALL "
				+ " SELECT NVDC.PKG_MTH, NVDC.CONT_DST_CD, IFNULL(NEDC.ETD_1,NVDC.ETD_1) ETD_1, NVDC.CONT_GRP_CD, NVDC.SHP_NM_1, NVDC.CONT_SIZE, IFNULL(NVDC.ETA_2,NVDC.ETA_1) ETA_1 "
				+ " FROM   TB_R_DLY_VPR_CONTAINER NVDC LEFT OUTER JOIN TB_R_ETD_CONTAINER NEDC "
				+ " ON  NVDC.PKG_MTH      = NEDC.PKG_MTH "
				+ " AND    NVDC.CONT_DST_CD  = NEDC.CONT_DST_CD "
				+ " AND    NVDC.CONT_SNO     = NEDC.CONT_SNO "
				+ " WHERE  NVDC.PKG_MTH = :" + VANNING_MONTH);
		appendWhereIfHas(sqlFromWhere, " AND NVDC.CONT_DST_CD = :" + DESTINATION_CODE, input.getDestinationCode());
		sqlFromWhere.append(" AND    NVDC.CONT_TYP   = 'F' "
				+ " AND    NOT EXISTS ( SELECT 1 "
				+ " FROM   TB_R_VPR_MTH_CONTAINER NVMC "
				+ " WHERE  NVMC.PKG_MTH=NVDC.PKG_MTH "
				+ " AND    NVMC.CONT_DST_CD = NVDC.CONT_DST_CD   "
				+ " AND    NVMC.ETD_1       = IFNULL(NEDC.ETD_1,NVDC.ETD_1)  "
				+ " AND    NVMC.CONT_GRP_CD = NVDC.CONT_GRP_CD   "
				+ " AND    NVMC.SHP_NM_1    = NVDC.SHP_NM_1      "
				+ " AND    NVMC.CONT_TYP    = 'F' "
				+ " ) "
				+ " AND    NOT EXISTS ( SELECT 1 "
				+ " FROM   TB_R_VPR_MTH_KEIHEN_CONTAINER NVMKC "
				+ " WHERE  NVMKC.PKG_MTH = NVDC.PKG_MTH "
				+ " AND    NVMKC.CONT_DST_CD = NVDC.CONT_DST_CD "
				+ " AND    NVMKC.ETD_1       = IFNULL(NEDC.ETD_1,NVDC.ETD_1) "
				+ " AND    NVMKC.CONT_GRP_CD = NVDC.CONT_GRP_CD "
				+ " AND    NVMKC.SHP_NM_1    = NVDC.SHP_NM_1 "
				+ " AND    NVMKC.CONT_TYP    = 'F' "
				+ " )) ALL_VPR, "
				+ " TB_M_MTH_RENBAN_SETUP NRSM, TB_M_PARAMETER PARA "
				+ " WHERE   ALL_VPR.CONT_GRP_CD = NRSM.CONT_GRP_CD "
				+ " AND     ALL_VPR.CONT_DST_CD = NRSM.CONT_DST_CD "
				+ " AND     ALL_VPR.ETD_1      >= NRSM.EFF_FROM_DT "
				+ " AND     ALL_VPR.ETD_1      <= NRSM.EFF_TO_DT "
				+ " AND     ALL_VPR.PKG_MTH = :" + VANNING_MONTH + " AND PARA.PARA_CD = 'INSP_LEAD_DAYS' ");
		appendWhereIfHas(sqlFromWhere, " AND ALL_VPR.CONT_DST_CD = :" + DESTINATION_CODE, input.getDestinationCode());
		appendWhereIfHas(sqlFromWhere, " AND ALL_VPR.ETD_1 >= STR_TO_DATE(:" + ETD_FROM + ",'%d/%m/%Y') ", input.getEtdFrom());
		appendWhereIfHas(sqlFromWhere, " AND ALL_VPR.ETD_1 <= STR_TO_DATE(:" + ETD_TO + ",'%d/%m/%Y') ", input.getEtdTo());
		appendWhereIfHas(sqlFromWhere, " AND ALL_VPR.SHP_NM_1 = :" + SHIPPING_COMPANY, input.getShippingCompanyCode());
		sqlFromWhere.append(" ) A  LEFT OUTER JOIN       " 
						+ "   (   SELECT NRBM.CB_CD , NRBM.CONT_VAN_MTH, NRBM.CONT_DST_CD , "
						+ " NRBM.ETD1        , " 
						+ " NRBM.VESSEL1     , "
						+ " NRBM.SHP_NM_1    , " 
						+ " NRBM.GROUP_ID    , "
						+ " NRBM.ETA         , " 
						+ " NRBM.BOOK_NO     , "
						+ " NRBM.CB_FLG      , " 
						+ " NRBD.CONT_GRP_CD , "
						+ " NRBM.VAN_END_DT  , "
						+ " DATE_FORMAT(NRBM.UPD_DT, '%Y%m%d%H%i%s') UPD_DT, "
						+ " NRBM.CONT_40, NRBM.CONT_20 "
						+ " FROM   TB_R_MTH_RENBAN_BOOKING_H NRBM , TB_R_MTH_RENBAN_BOOKING_D NRBD "
						+ " WHERE  NRBM.GROUP_ID     = NRBD.GROUP_ID "
						+ " AND NRBM.SHP_NM_1     = NRBD.SHP_NM_1 "
						+ " AND NRBM.ETD1         = NRBD.ETD1 "
						+ " AND NRBM.CONT_VAN_MTH = NRBD.CONT_VAN_MTH "
						+ " AND NRBM.CONT_DST_CD  = NRBD.CONT_DST_CD "
						+ " AND NRBM.CONT_VAN_MTH = :" + VANNING_MONTH);
		appendWhereIfHas(sqlFromWhere, " AND NRBM.CONT_DST_CD = :" + DESTINATION_CODE, input.getDestinationCode());
		appendWhereIfHas(sqlFromWhere, " AND NRBM.ETD1 >= STR_TO_DATE(:" + ETD_FROM + ",'%d/%m/%Y') ", input.getEtdFrom());
		appendWhereIfHas(sqlFromWhere, " AND NRBM.ETD1 <= STR_TO_DATE(:" + ETD_TO + ",'%d/%m/%Y') ", input.getEtdTo());
		sqlFromWhere.append(" ) B ON ( B.SHP_NM_1 = A.SHP_NM_1 "
			+ " AND B.GROUP_ID  = A.GROUP_ID "
			+ " AND B.ETD1 = A.ETD_1 "
			+ " AND B.CONT_VAN_MTH = A.PKG_MTH "
			+ " AND B.CONT_DST_CD = A.CONT_DST_CD) ) TAB_2 ) TAB_1 " 
			+ " WHERE TAB_1.SR_NO = 1 ");
		
		String sqlOrder = " ORDER BY TAB_1.CONT_DST_CD ASC, TAB_1.ETD1 ASC, TAB_1.ETA ASC, TAB_1.GROUP_ID ASC ";
		
		String sqlLimit = "";
		if(pagination != null) {
			sqlLimit = " LIMIT "+ pagination.getPageSize() +" OFFSET "+ pagination.getOffset();
		}
		
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(sqlSelect).append(sqlGroupCont()).append(sqlFromWhere).append(sqlOrder).append(sqlLimit);
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue(VANNING_MONTH, input.getVanningMonth().replace(ConstantUtil.SLASH, ConstantUtil.BLANK));
		addParamIfHas(param, DESTINATION_CODE, input.getDestinationCode());
		addParamIfHas(param, ETD_FROM, input.getEtdFrom());
		addParamIfHas(param, ETD_TO, input.getEtdTo());
		addParamIfHas(param, SHIPPING_COMPANY, input.getShippingCompanyCode());
		
		List<VesselBookingMasterSearch> queryResult = namedParameterJdbcTemplate.query(sqlQuery.toString(), param, (rs, rowNum) -> {
			VesselBookingMasterSearch objResult = new VesselBookingMasterSearch();
			objResult.setDestinationCode(rs.getString("CONT_DST_CD"));
			objResult.setEtd1(rs.getString("ETD"));
			objResult.setFinalEta(rs.getString("ETA"));
			objResult.setNoOfContainer20ft(rs.getString("CONT_20"));
			objResult.setNoOfContainer40ft(rs.getString("CONT_40"));
			objResult.setShippingCompany(rs.getString("SHP_NM"));
			objResult.setCustomBrokerCode(rs.getString("CB_CD"));
			objResult.setCustomBrokerName(rs.getString("CB_NM"));
			objResult.setBookingNo(rs.getString("BOOK_NO"));
			objResult.setVessel1(rs.getString("VESSEL"));
			objResult.setBookingStatus(rs.getString("BOOK_STAT"));
			objResult.setGroupId(rs.getString("GROUP_ID"));
			objResult.setVanningMonth(rs.getString("PKG_MTH"));
			objResult.setVanEndDate(rs.getString("VAN_END_DT"));
			objResult.setOldCustomBrokerCode(rs.getString("OLD_CB_CD"));
			objResult.setOldBookingNo(rs.getString("OLD_BOOK_NO"));
			objResult.setOldVessel1(rs.getString("OLD_VESSEL"));
			objResult.setEtdPassMonth(rs.getBoolean("ETD_IS_PASSED"));
			objResult.setCbFlag(rs.getString("CB_FLG"));
			objResult.setRenbanCode(rs.getString("RENBAN_CODE"));
			return objResult;
		});
		
		if(pagination != null) {
			return new PageImpl<>(queryResult, pagination, count(sqlFromWhere.toString(), param));
		} else {
			return new PageImpl<>(queryResult);
		}
	}
	
	public List<MixedVesselBooking> findShipCompETD(MixedVesselBooking input) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DISTINCT SHP_NM_1, "
				+ " DATE_FORMAT(ETD_1, '%d/%m/%Y') ETD_1 "
				+ " , CONT_DST_CD "
				+ " FROM  TB_R_DLY_VPR_CONTAINER "
				+ " WHERE PKG_MTH = :" + VANNING_MONTH);
		appendWhereIfHas(sql, " AND CONT_DST_CD IN (:" + DESTINATION_CODE +") ", input.getDestinationCodes());
		appendWhereIfHas(sql, " AND ETD_1 >= STR_TO_DATE(:" + ETD_FROM + ", '%d/%m/%Y') ", input.getEtdFrom());
		appendWhereIfHas(sql, " AND ETD_1 <= STR_TO_DATE(:" + ETD_TO + ", '%d/%m/%Y') ", input.getEtdTo());
		appendWhereIfHas(sql, " AND SHP_NM_1 = :"+ SHIPPING_COMPANY, input.getShippingCompanyCode());
		sql.append(" UNION "
				+ " SELECT DISTINCT SHP_NM_1, DATE_FORMAT(ETD_1,'%d/%m/%Y') ETD_1 "
				+ " , CONT_DST_CD "
				+ " FROM  TB_R_VPR_MTH_KEIHEN_CONTAINER "
				+ " WHERE PKG_MTH    = :" + VANNING_MONTH
				+ " AND   CONT_TYP   = 'F' "
				+ " AND   KEIHEN_REV_NO = :" + KEIHEN_REV_NO);
		appendWhereIfHas(sql, " AND CONT_DST_CD IN (:" + DESTINATION_CODE+") ", input.getDestinationCodes());
		appendWhereIfHas(sql, " AND ETD_1 >= STR_TO_DATE(:" + ETD_FROM + ", '%d/%m/%Y') ", input.getEtdFrom());
		appendWhereIfHas(sql, " AND ETD_1 <= STR_TO_DATE(:" + ETD_TO + ", '%d/%m/%Y') ", input.getEtdTo());
		appendWhereIfHas(sql, " AND SHP_NM_1 = :" + SHIPPING_COMPANY, input.getShippingCompanyCode());
		
		String revNo = findKeihenRev(input.getVanningMonth());
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue(VANNING_MONTH, input.getVanningMonth().replace(ConstantUtil.SLASH, ConstantUtil.BLANK))
				.addValue(KEIHEN_REV_NO, revNo);
		addParamIfHas(param, DESTINATION_CODE, input.getDestinationCodes());
		addParamIfHas(param, ETD_FROM, input.getEtdFrom());
		addParamIfHas(param, ETD_TO, input.getEtdTo());
		addParamIfHas(param, SHIPPING_COMPANY, input.getShippingCompanyCode());
		
		return namedParameterJdbcTemplate.query(sql.toString(), param, (rs, rowNum) -> {
			MixedVesselBooking mixedIn = new MixedVesselBooking();
			mixedIn.setVanningMonth(input.getVanningMonth());
			mixedIn.setDestinationCode(rs.getString("CONT_DST_CD"));
			mixedIn.setEtd(rs.getString("ETD_1"));
			mixedIn.setShippingCompanyCode(rs.getString("SHP_NM_1"));
			mixedIn.setKeihenRevNo(revNo);
			
			return mixedIn;
		});
	}
	
	public String findKeihenRev(String pkgMonth) {
		String sql = " SELECT MAX(KEIHEN_REV_NO) MAX_KEIHEN_REV_NO "
				+ "    FROM TB_R_VPR_MTH_KEIHEN_CONTAINER "
				+ "    WHERE PKG_MTH = :" + VANNING_MONTH;
		
		return namedParameterJdbcTemplate.queryForObject(sql
				, new MapSqlParameterSource().addValue(VANNING_MONTH, pkgMonth.replace(ConstantUtil.SLASH, ConstantUtil.BLANK)) , String.class);
	}
	
	public List<MixedVesselBooking> getAllTypeGrpCnt(MixedVesselBooking input) {
		String sql = " SELECT GROUP_ID, IFNULL(CONT_40, 0) CONT_40, "
				+ "    IFNULL(CONT_20, 0) CONT_20 "
				+ " , 'RENBAN' LIST_TYPE "
				+ " FROM   TB_R_MTH_RENBAN_BOOKING_H "
				+ " WHERE  CONT_VAN_MTH = :" + VANNING_MONTH
				+ " AND CONT_DST_CD = :" + DESTINATION_CODE
				+ " AND ETD1 = STR_TO_DATE(:" + ETD +", '%d/%m/%Y') "
				+ " AND SHP_NM_1 = :" + SHIPPING_COMPANY
				+ " UNION "
				+ " SELECT B.GROUP_ID, "
				+ "        SUM(CASE A.CONT_SIZE  WHEN '40' THEN  1  ELSE 0 END) CONT_40, "
				+ "        SUM(CASE A.CONT_SIZE  WHEN '20' THEN  1  ELSE 0 END) CONT_20 "
				+ " , 'DAILY' LIST_TYPE "
				+ " FROM   TB_R_DLY_VPR_CONTAINER A, TB_M_MTH_RENBAN_SETUP B"
				+ " WHERE  A.CONT_DST_CD = B.CONT_DST_CD "
				+ " AND    A.CONT_GRP_CD = B.CONT_GRP_CD "
				+ " AND    A.ETD_1      >= B.EFF_FROM_DT "
				+ " AND    A.ETD_1      <= B.EFF_TO_DT "
				+ " AND A.CONT_DST_CD = :" + DESTINATION_CODE
				+ " AND A.PKG_MTH     = :" + VANNING_MONTH
				+ " AND A.ETD_1       = STR_TO_DATE(:" + ETD + ", '%d/%m/%Y') "
				+ " AND A.SHP_NM_1    = :" + SHIPPING_COMPANY
				+ " AND A.CONT_TYP    = 'F' "
				+ " GROUP BY A.ETD_1, A.SHP_NM_1, B.GROUP_ID"
				+ " UNION "
				+ " SELECT B.GROUP_ID, "
				+ "        SUM(CASE A.CONT_SIZE  WHEN '40' THEN  1  ELSE 0 END) CONT_40, "
				+ "        SUM(CASE A.CONT_SIZE  WHEN '20' THEN  1  ELSE 0 END) CONT_20 "
				+ " , 'KEIHEN' LIST_TYPE "
				+ " FROM   TB_R_VPR_MTH_KEIHEN_CONTAINER A, TB_M_MTH_RENBAN_SETUP B "
				+ " WHERE  A.CONT_DST_CD = B.CONT_DST_CD "
				+ " AND    A.CONT_GRP_CD = B.CONT_GRP_CD "
				+ " AND    A.ETD_1      >= B.EFF_FROM_DT "
				+ " AND    A.ETD_1      <= B.EFF_TO_DT   "
				+ " AND    A.CONT_DST_CD = :" + DESTINATION_CODE
				+ " AND    A.PKG_MTH     = :" + VANNING_MONTH
				+ " AND    A.ETD_1       = STR_TO_DATE(:"+ ETD + ", '%d/%m/%Y') "
				+ " AND    A.SHP_NM_1    = :" + SHIPPING_COMPANY
				+ " AND    A.CONT_TYP    = 'F' "
				+ " AND    A.KEIHEN_REV_NO    = :" + KEIHEN_REV_NO
				+ " GROUP BY A.ETD_1, A.SHP_NM_1, B.GROUP_ID"
				+ " UNION "
				+ " SELECT B.GROUP_ID, "
				+ "        SUM(CASE A.CONT_SIZE  WHEN '40' THEN  1  ELSE 0 END) CONT_40, "
				+ "        SUM(CASE A.CONT_SIZE  WHEN '20' THEN  1  ELSE 0 END) CONT_20 "
				+ " , 'MONTHLY' LIST_TYPE "
				+ " FROM   TB_R_VPR_MTH_CONTAINER A, TB_M_MTH_RENBAN_SETUP B "
				+ " WHERE  A.CONT_DST_CD = B.CONT_DST_CD "
				+ " AND    A.CONT_GRP_CD = B.CONT_GRP_CD "
				+ " AND    A.ETD_1      >= B.EFF_FROM_DT "
				+ " AND    A.ETD_1      <= B.EFF_TO_DT   "
				+ " AND    A.CONT_DST_CD = :" + DESTINATION_CODE
				+ " AND A.PKG_MTH = :" + VANNING_MONTH
				+ " AND A.ETD_1       = STR_TO_DATE(:"+ ETD + ", '%d/%m/%Y') "
				+ " AND A.SHP_NM_1    = :" + SHIPPING_COMPANY
				+ " AND A.CONT_TYP    = 'F' "
				+ " GROUP BY A.ETD_1, A.SHP_NM_1, B.GROUP_ID";
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue(VANNING_MONTH, input.getVanningMonth().replace(ConstantUtil.SLASH, ConstantUtil.BLANK))
				.addValue(ETD, input.getEtd())
				.addValue(SHIPPING_COMPANY, input.getShippingCompanyCode())
				.addValue(KEIHEN_REV_NO, input.getKeihenRevNo())
				.addValue(DESTINATION_CODE, input.getDestinationCode());
		
		return namedParameterJdbcTemplate.query(sql, param, (rs, rowNum) -> {
			MixedVesselBooking res = new MixedVesselBooking();
			res.setGroupId(rs.getString("GROUP_ID"));
			res.setCont20(rs.getInt("CONT_20"));
			res.setCont40(rs.getInt("CONT_40"));
			res.setListType(rs.getString("LIST_TYPE"));
			return res;
		});
	}
	
	public List<MixedVesselBooking> findNewGroupCDS(MixedVesselBooking input){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT NVMC.PKG_MTH, NVMC.CONT_GRP_CD , "
		+ " NVMC.SHP_NM_1 , DATE_FORMAT(NVMC.ETD_1, '%d/%m/%Y') ETD , "
		+ " DATE_FORMAT(NVMC.ETA_1, '%d/%m/%Y') ETA, NVMC.CONT_DST_CD, "
		+ " NRSM.GROUP_ID FROM ( "
		+ " SELECT NVDC.PKG_MTH PKG_MTH, NVDC.CONT_DST_CD , "
		+ " IFNULL(NEDC.ETD_1,NVDC.ETD_1) ETD_1, NVDC.CONT_GRP_CD CONT_GRP_CD, "
		+ " NVDC.SHP_NM_1 SHP_NM_1, IFNULL(NVDC.ETA_2,NVDC.ETA_1) ETA_1 "
		+ " FROM TB_R_DLY_VPR_CONTAINER NVDC LEFT OUTER JOIN TB_R_ETD_CONTAINER NEDC "
		+ " ON NVDC.PKG_MTH      = NEDC.PKG_MTH "
		+ " AND NVDC.CONT_DST_CD  = NEDC.CONT_DST_CD "
		+ " AND NVDC.CONT_SNO     = NEDC.CONT_SNO "
		+ " WHERE NVDC.PKG_MTH = :" + VANNING_MONTH);
		appendWhereIfHas(sql, " AND NVDC.CONT_DST_CD  = :" + DESTINATION_CODE, input.getDestinationCode());
		sql.append(" AND NVDC.CONT_TYP = 'F' "
		+ " AND    NOT EXISTS ( SELECT 1 "
		+ " FROM   TB_R_VPR_MTH_CONTAINER NVMC "
		+ " WHERE  NVMC.PKG_MTH     = NVDC.PKG_MTH "
		+ " AND    NVMC.CONT_DST_CD = NVDC.CONT_DST_CD "
		+ " AND    NVMC.ETD_1       = IFNULL(NEDC.ETD_1,NVDC.ETD_1) "
		+ " AND    NVMC.CONT_GRP_CD = NVDC.CONT_GRP_CD "
		+ " AND    NVMC.SHP_NM_1    = NVDC.SHP_NM_1 "
		+ " AND    NVMC.CONT_TYP    = 'F' "
		+ " ) "
		+ " AND    NOT EXISTS ( SELECT 1 "
		+ " FROM   TB_R_VPR_MTH_KEIHEN_CONTAINER NVMKC "
		+ " WHERE  NVMKC.PKG_MTH =NVDC.PKG_MTH "
		+ " AND    NVMKC.CONT_DST_CD = NVDC.CONT_DST_CD "
		+ " AND    NVMKC.ETD_1       = IFNULL(NEDC.ETD_1,NVDC.ETD_1) "
		+ " AND    NVMKC.CONT_GRP_CD = NVDC.CONT_GRP_CD "
		+ " AND    NVMKC.SHP_NM_1    = NVDC.SHP_NM_1 "
		+ " AND    NVMKC.CONT_TYP    = 'F' "
		+ " ) "
		+ " UNION ALL "
		+ " SELECT NVMKC.PKG_MTH, NVMKC.CONT_DST_CD, IFNULL(NVMKC.ETD_1,NVMKC.ETD_2) ETD_1, NVMKC.CONT_GRP_CD, NVMKC.SHP_NM_1,IFNULL(NVMKC.ETA_2,NVMKC.ETA_1) ETA_1 "
		+ " FROM TB_R_VPR_MTH_KEIHEN_CONTAINER NVMKC "
		+ "  WHERE NVMKC.PKG_MTH    = :" + VANNING_MONTH);
		appendWhereIfHas(sql, " AND NVMKC.CONT_DST_CD = :" + DESTINATION_CODE, input.getDestinationCode());
		sql.append("    AND NVMKC.CONT_TYP   = 'F' "
		+ "    AND NOT EXISTS ( SELECT 1 "
		+ "                     FROM TB_R_ETD_CONTAINER NEDC "
		+ "                     WHERE NVMKC.PKG_MTH     = NEDC.PKG_MTH "
		+ "                     AND NVMKC.CONT_DST_CD = NEDC.CONT_DST_CD "
		+ "                     AND NVMKC.ETD_1       = NEDC.OLD_ETD_1 "
		+ "                     AND NEDC.ETD_1      <> NEDC.OLD_ETD_1 "
		+ "                     AND NVMKC.CONT_GRP_CD = NEDC.CONT_GRP_CD "
		+ "                     AND NVMKC.SHP_NM_1    = NEDC.SHP_NM_1 "
		+ "                       ) "
		+ "    AND NOT EXISTS ( SELECT 1 "
		+ "                       FROM   TB_R_VPR_MTH_CONTAINER NVMC "
		+ "                       WHERE  NVMC.PKG_MTH = NVMKC.PKG_MTH "
		+ "                       AND    NVMC.CONT_DST_CD = NVMKC.CONT_DST_CD "
		+ "                       AND    NVMC.ETD_1       = NVMKC.ETD_1 "
		+ "                       AND    NVMC.CONT_GRP_CD = NVMKC.CONT_GRP_CD "
		+ "                       AND    NVMC.SHP_NM_1    = NVMKC.SHP_NM_1 "
		+ "                       AND    NVMC.CONT_TYP    = 'F' "
		+ "                           ) "
		+ " ) NVMC, "
		+ " TB_M_MTH_RENBAN_SETUP NRSM "
		+ " WHERE NVMC.CONT_GRP_CD = NRSM.CONT_GRP_CD "
		+ " AND   NVMC.CONT_DST_CD = NRSM.CONT_DST_CD "
		+ " AND   NVMC.ETD_1      >= NRSM.EFF_FROM_DT "
		+ " AND   NVMC.ETD_1      <= NRSM.EFF_TO_DT   "
		+ " AND   NVMC.PKG_MTH     = :" + VANNING_MONTH);
		appendWhereIfHas(sql, " AND NVMC.CONT_DST_CD = :" + DESTINATION_CODE, input.getDestinationCode());
		sql.append(" AND NOT EXISTS (SELECT 1 "
		+ " FROM TB_R_MTH_RENBAN_BOOKING_H NRBM, TB_R_MTH_RENBAN_BOOKING_D NRBD"
		+ " WHERE NRBM.GROUP_ID   = NRBD.GROUP_ID "
		+ " AND NRBM.SHP_NM_1     = NRBD.SHP_NM_1 "
		+ " AND NRBM.ETD1         = NRBD.ETD1 "
		+ " AND NRBM.CONT_VAN_MTH = NRBD.CONT_VAN_MTH "
		+ " AND NRBM.CONT_DST_CD  = NRBD.CONT_DST_CD "
		+ " AND NRBM.CONT_VAN_MTH = NVMC.PKG_MTH "
		+ " AND NRBM.CONT_DST_CD  = NVMC.CONT_DST_CD "
		+ " AND NRBM.ETD1         = NVMC.ETD_1 "
		+ " AND NRBM.SHP_NM_1     = NVMC.SHP_NM_1 "
		+ " AND NRBM.GROUP_ID     = NRSM.GROUP_ID "
		+ " AND NRBD.CONT_GRP_CD  = NVMC.CONT_GRP_CD) "
		+ " GROUP BY NVMC.PKG_MTH, NVMC.CONT_GRP_CD , NVMC.SHP_NM_1 , NVMC.ETD_1 , NVMC.ETA_1 , NVMC.CONT_DST_CD , NRSM.GROUP_ID "
		+ " ORDER BY NVMC.PKG_MTH, NVMC.ETD_1, NVMC.CONT_DST_CD, NRSM.GROUP_ID");
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue(VANNING_MONTH, input.getVanningMonth().replace(ConstantUtil.SLASH, ConstantUtil.BLANK));
		addParamIfHas(param, DESTINATION_CODE, input.getDestinationCode());
		
		return namedParameterJdbcTemplate.query(sql.toString(), param, (rs, rowNum) -> {
			MixedVesselBooking mixedIn = new MixedVesselBooking();
			mixedIn.setEtd(rs.getString("ETD"));
			mixedIn.setGroupId(rs.getString("GROUP_ID"));
			mixedIn.setContGroupCode(rs.getString("CONT_GRP_CD"));
			mixedIn.setVanningMonth(rs.getString("PKG_MTH"));
			mixedIn.setShippingCompanyCode(rs.getString("SHP_NM_1"));
			
			return mixedIn;
		});
	}

	private int count(String sqlFromWhere, MapSqlParameterSource param) {
		StringBuilder sqlCount = new StringBuilder();
		sqlCount.append("SELECT COUNT(*) ");
		sqlCount.append(sqlFromWhere);
		Integer cnt = namedParameterJdbcTemplate.queryForObject(sqlCount.toString(), param, Integer.class);
		return cnt == null ? 0 : cnt;
	}
	
	private void appendWhereIfHas(StringBuilder mainSql, String appendWhere, Object value) {
		if (ObjectUtils.isNotEmpty(value))
			mainSql.append(appendWhere);
	}
	
	private void addParamIfHas(MapSqlParameterSource param, String name, Object value) {
		if (ObjectUtils.isNotEmpty(value))
			param.addValue(name, value);
	}
	
	private String sqlGroupCont() {
		return ", ( select GROUP_CONCAT(TAB.CONT_GRP_CD ORDER BY TAB.CONT_GRP_CD ASC) RENBAN_CODE from ( select "
				+ " distinct DATE_FORMAT(NVMC.ETD_1, '%d/%m/%Y') ETD, REN_MST.GROUP_ID, "
				+ " NVMC.CONT_GRP_CD, NVMC.CONT_DST_CD from "
				+ " TB_M_MTH_RENBAN_SETUP REN_MST ,  (  select  distinct NVMC0.PKG_MTH, "
				+ " NVMC0.CONT_DST_CD, IFNULL(NEDC.ETD_1, NVMC0.ETD_1) ETD_1, "
				+ " NVMC0.CONT_GRP_CD from ( select distinct PKG_MTH, "
				+ " CONT_DST_CD, ETD_1, CONT_GRP_CD from TB_R_VPR_MTH_CONTAINER A "
				+ " where A.CONT_DST_CD = TAB_1.CONT_DST_CD "
				+ " and A.PKG_MTH = TAB_1.PKG_MTH and A.CONT_TYP = 'F'  union all "
				+ " select  distinct PKG_MTH,  CONT_DST_CD,  ETD_1, "
				+ " CONT_GRP_CD  from  TB_R_VPR_MTH_KEIHEN_CONTAINER B  where "
				+ " B.CONT_DST_CD = TAB_1.CONT_DST_CD  and B.PKG_MTH = TAB_1.PKG_MTH "
				+ " and B.CONT_TYP = 'F'  and B.KEIHEN_REV_NO = '1' union all  select "
				+ " distinct C.PKG_MTH, C.CONT_DST_CD,  IFNULL(D.ETD_1, C.ETD_1) ETD_1, "
				+ " C.CONT_GRP_CD from TB_R_DLY_VPR_CONTAINER C "
				+ " left outer join TB_R_ETD_CONTAINER D on  C.PKG_MTH = D.PKG_MTH "
				+ " and C.CONT_DST_CD = D.CONT_DST_CD  and C.CONT_SNO = D.CONT_SNO "
				+ " where C.CONT_DST_CD = TAB_1.CONT_DST_CD "
				+ " and C.PKG_MTH = TAB_1.PKG_MTH  and C.CONT_TYP = 'F' ) NVMC0 "
				+ " left outer join TB_R_ETD_CONTAINER NEDC on "
				+ " NVMC0.PKG_MTH = NEDC.PKG_MTH  and NVMC0.CONT_DST_CD = NEDC.CONT_DST_CD "
				+ " and NVMC0.CONT_GRP_CD = NEDC.CONT_GRP_CD "
				+ " and NVMC0.ETD_1 = NEDC.OLD_ETD_1 ) NVMC  where "
				+ " REN_MST.CONT_DST_CD = NVMC.CONT_DST_CD "
				+ " and REN_MST.EFF_FROM_DT <= NVMC.ETD_1 "
				+ " and REN_MST.EFF_TO_DT >= NVMC.ETD_1 "
				+ " and NVMC.CONT_DST_CD = TAB_1.CONT_DST_CD "
				+ " and REN_MST.GROUP_ID = TAB_1.GROUP_ID  and NVMC.ETD_1 = TAB_1.ETD1 "
				+ " and REN_MST.CONT_GRP_CD = NVMC.CONT_GRP_CD "
				+ " and NVMC.PKG_MTH = TAB_1.PKG_MTH  ) TAB group by TAB.ETD, "
				+ " TAB.GROUP_ID) RENBAN_CODE ";
	}
}
