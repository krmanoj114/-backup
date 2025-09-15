package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.RddDownLocDtlEntity;

@Repository
public interface DgDeclarationReportResponseRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {

	@Query(value = "SELECT TRIM(IICD_CONT_SNO)as IICD_CONT_SNO,\r\n"
			+ "			               dense_rank() over (partition by IICD_CONT_SNO order by INM_MOD_NO) \r\n"
			+ "			               + dense_rank() over (partition by IICD_CONT_SNO order by INM_MOD_NO desc)-1 TOT_CASES_RB,\r\n"
			+ "			                INM_MOD_NO,                round(TOT_NW_CASE,3) TOT_NW_CASE,\r\n"
			+ "			                TOT_GW_CASE,               round(TOT_M3_CASE,3) TOT_M3_CASE,\r\n"
			+ "			                INP_PART_NO,               PRT_NAME,               UNIT_BOX,\r\n"
			+ "			                SUM_TOT_UNIT,                round(NET_WT,3),\r\n"
			+ "			                round(TOT_NET_CASE, 3) TOT_NET_CASE,\r\n"
			+ "			                round(BOX_GROSS_CASE, 3) BOX_GROSS_CASE,\r\n"
			+ "			                round(M3_CASE,3) M3_CASE,\r\n"
			+ "			                (SELECT REGEXP_REPLACE(group_concat( distinct temp.INV_NO,','),',,',',')\r\n"
			+ "			                   FROM TB_R_INV_MODULE_D temp\r\n"
			+ "			                  WHERE temp.CONT_SNO = IICD_CONT_SNO)                 INV_NO_RENBAN,\r\n"
			+ "			                  FORMAT(SUM(TOT_NET_CASE) OVER (PARTITION  BY IICD_CONT_SNO),3) NW,\r\n"
			+ "			                  round(SUM(BOX_GROSS_CASE) OVER (PARTITION  BY IICD_CONT_SNO),3) GW,\r\n"
			+ "			                  round(SUM(M3_CASE) OVER (PARTITION  BY IICD_CONT_SNO),3) M3_REN,\r\n"
			+ "			                  BOOK_NO,          CONT_DST\r\n"
			+ "			            FROM ( SELECT INM_CONT_SNO,IICD_CONT_SNO,                          INM_MOD_NO,\r\n"
			+ "			                          INP_PART_NO,                          PRT_NAME,\r\n"
			+ "			                          UNIT_BOX,                          SUM_TOT_UNIT,\r\n"
			+ "			                          NET_WT,                          TOT_NET_CASE,\r\n"
			+ "			                          BOX_GROSS_CASE,\r\n"
			+ "			                          round(M3_CASE, 3) M3_CASE,\r\n"
			+ "			                          SUM(TOT_NET_CASE) OVER (PARTITION BY IICD_CONT_SNO, INM_MOD_NO) TOT_NW_CASE,\r\n"
			+ "			                          SUM(BOX_GROSS_CASE) OVER (PARTITION BY IICD_CONT_SNO, INM_MOD_NO) TOT_GW_CASE,\r\n"
			+ "			                          SUM(M3_CASE) OVER (PARTITION BY IICD_CONT_SNO, INM_MOD_NO) TOT_M3_CASE,\r\n"
			+ "			                          INM_INV_NO,                        BOOK_NO,\r\n"
			+ "			                          CONT_DST\r\n"
			+ "			                     FROM (SELECT INM.CONT_SNO as INM_CONT_SNO,IICD.CONT_SNO IICD_CONT_SNO,\r\n"
			+ "			                                    CASE WHEN INM.LOT_NO='***' THEN INM.MOD_NO ELSE CONCAT(INM.MOD_NO , '-' , INM.LOT_NO) END INM_MOD_NO,\r\n"
			+ "			                                    SUBSTR(INP.PART_NO, 1, 10) INP_PART_NO,\r\n"
			+ "			                                    \r\n"
			+ "			                                    INP.PART_NM PRT_NAME,\r\n"
			+ "			                                    Round(INP.UNIT_PER_BOX,9)\r\n"
			+ "												UNIT_BOX,\r\n"
			+ "			                                    Round(SUM(INP.UNIT_PER_BOX),9) SUM_TOT_UNIT,\r\n"
			+ "			                                    Round(INP.NET_WT, 9) NET_WT,\r\n"
			+ "			                                    (IFNULL(INP.NET_WT, 0)\r\n"
			+ "			                                     * IFNULL(SUM(INP.UNIT_PER_BOX), '0'))\r\n"
			+ "			                                       TOT_NET_CASE,\r\n"
			+ "			                                    SUM(INP.BOX_GROSS_WT) BOX_GROSS_CASE,\r\n"
			+ "			                                    SUM(INP.BOX_M3) M3_CASE,\r\n"
			+ "			                                    INM.INV_NO as INM_INV_NO,\r\n"
			+ "			                                    IICD.BOOK_NO,\r\n"
			+ "			                                    IICD.CONT_DST\r\n"
			+ "			                               FROM TB_R_INV_INVOICE_H IND,\r\n"
			+ "												TB_R_INV_CONTAINER_D IICD,\r\n"
			+ "												TB_R_INV_MODULE_D INM,\r\n"
			+ "												TB_R_INV_PART_D INP  \r\n"
			+ "			                              WHERE     IICD.INV_NO    = IND.INV_NO\r\n"
			+ "			                                    AND IICD.CONT_SNO  = INM.CONT_SNO\r\n"
			+ "			                                    AND IICD.CONT_DST  = INM.CONT_DST\r\n"
			+ "			                                    AND IND.INV_NO = INM.INV_NO\r\n"
			+ "			                                    AND IND.INV_NO = INP.INV_NO\r\n"
			+ "			                                    AND INM.INV_NO = INP.INV_NO\r\n"
			+ "			                                    AND INM.INV_NO = INP.INV_NO\r\n"
			+ "			                                    AND INM.MOD_NO = INP.MOD_NO\r\n"
			+ "			                                    AND INM.LOT_NO = INP.LOT_NO\r\n"
			+ "			                                    AND IND.ORD_TYP = 'R'\r\n"
			+ "			                                    AND IND.ETD >=STR_TO_DATE(:etd,'%d/%m/%Y')\r\n"
			+ "			                                    AND IND.ETD <=STR_TO_DATE(:eta,'%d/%m/%Y')\r\n"
			+ "			                                    AND IND.CANCEL_FLG = 'N'\r\n"
			+ "			                                    AND (:bookingNo IS NULL OR IICD.BOOK_NO = :bookingNo )\r\n"
			+ "			                                    AND (:contryCd IS NULL OR IICD.CONT_DST = :contryCd)\r\n"
			+ "			                                    AND INP.DG_FLG = '1'\r\n"
			+ "			                           GROUP BY INM.CONT_SNO,\r\n"
			+ "									            IICD.CONT_SNO,\r\n"
			+ "			                                    INM.MOD_NO,\r\n"
			+ "			                                    INM.LOT_NO,\r\n"
			+ "			                                    INP.PART_NO,\r\n"
			+ "			                                    INP.PART_NM,\r\n"
			+ "			                                    INP.UNIT_PER_BOX,\r\n"
			+ "			                                    INP.NET_WT,\r\n"
			+ "			                                    INM.INV_NO,\r\n"
			+ "			                                    IICD.BOOK_NO,\r\n"
			+ "			                                    IICD.CONT_DST\r\n"
			+ "			                           ORDER BY IICD.CONT_SNO,\r\n"
			+ "			                                    INM.MOD_NO,\r\n"
			+ "			                                    INP.PART_NO,\r\n"
			+ "			                                    PRT_NAME,                                    UNIT_BOX,\r\n"
			+ "			                                    NET_WT) as p                 GROUP BY IICD_CONT_SNO,\r\n"
			+ "			                          INM_MOD_NO,                          INP_PART_NO,\r\n"
			+ "			                          PRT_NAME,                          UNIT_BOX,\r\n"
			+ "			                          SUM_TOT_UNIT,                         NET_WT,\r\n"
			+ "			                          TOT_NET_CASE,                         BOX_GROSS_CASE,\r\n"
			+ "			                          M3_CASE,                          INM_INV_NO,\r\n"
			+ "			                          BOOK_NO,\r\n"
			+ "			                          CONT_DST) AS T order by T.IICD_CONT_SNO,T.INM_MOD_NO,T.INP_PART_NO", nativeQuery = true)
	List<Object[]> getRins104Data(@Param("etd") String etd, @Param("eta") String eta,
			@Param("bookingNo") String bookingNo, @Param("contryCd") String contryCd);

	@Query(value = "CALL PINS002DG(:invoiceNo,:orderType,:usertId,'','',:cmpCd is null or :cmpCd)", nativeQuery = true)
	void getInvoiceTwoDgData(@Param("invoiceNo") String invoiceNo, @Param("orderType") String orderType,
			@Param("usertId") String usertId, @Param("cmpCd") String cmpCd);

	@Query(value = "CALL PINS002DG('',:orderType,:usertId,:etd,:destination,:cmpCd is null or :cmpCd)", nativeQuery = true)
	void getInvoiceTwoDgData(@Param("orderType") String orderType,@Param("usertId") String usertId,@Param("etd") String etd, @Param("destination") String destination,
			 @Param("cmpCd") String cmpCd);

	@Query(value = "SELECT				   INS_CNSG_NAME,				   INS_CNSG_ADD1,\r\n"
			+ "						   INS_CNSG_ADD2,				   INS_CNSG_ADD3,\r\n"
			+ "						   INS_CNSG_ADD4,				   INS_INV_NO,\r\n"
			+ "						   INS_INV_DT,				   INS_PART_NO,\r\n"
			+ "						   INS_UNIT_PER_BOX,				   INS_SUM_TOT_UNIT,\r\n"
			+ "						   INS_ICO_FLG,				   INS_PART_PRICE,\r\n"
			+ "						   INS_PART_NAME,				   INS_PART_WT,\r\n"
			+ "						   INS_GROSS_WT,\r\n"
			+ "						   CASE WHEN INS_MEASUREMENT IS NULL THEN '0' ELSE INS_MEASUREMENT END INS_MEASUREMENT,\r\n"
			+ "						   INS_SHIPMARK_4,				   INS_SHIPMARK_5,\r\n"
			+ "						   CASE WHEN INS_TPT_CD= '1'THEN INS_CONT_SNO ELSE INS_SHIPMARK_4 END SHIP_MARK_GP,\r\n"
			+ "						   \r\n"
			+ "                           CASE WHEN SUBSTR(INS_SHIPMARK_4, 1, 3) ='123' THEN \r\n"
			+ "		                   CONCAT(IFNULL(SUBSTR(INS_SHIPMARK_4, 1, 3), '') , '-' ,\r\n"
			+ "		                   IFNULL(SUBSTR(INS_SHIPMARK_4, 4, 1), '') , '-' ,\r\n"
			+ "		                   IFNULL(SUBSTR(INS_SHIPMARK_4, 5, 1), '') , '-' ,\r\n"
			+ "		                   IFNULL(SUBSTR(INS_SHIPMARK_4, 6), '')) ELSE INS_SHIPMARK_4 END     CASE_MOD,\r\n"
			+ "						   INS_CF_CD,				   INS_SRS_NAME,\r\n"
			+ "						   INS_NO_OF_CASES,				   INS_NO_OF_BOXES,\r\n"
			+ "						   INS_CONT_SNO,				   INS_ISO_CONT_NO,\r\n"
			+ "						   INS_TPT_CD			FROM				   TB_S_INS_PACK_LIST\r\n"
			+ "					ORDER BY INS_INV_NO, INS_SRS_NAME, CASE WHEN INS_TPT_CD = '1' THEN INS_CONT_SNO ELSE INS_SHIPMARK_4 END, INS_PART_NO", nativeQuery = true)
	List<Object[]> getDataFromTemp1(@Param("cmpCd") String cmpCd);
	
}
