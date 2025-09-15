package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpex.entity.RddDownLocDtlEntity;

import java.util.List;

import javax.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MpmiInvoiceCoverPageRepository extends JpaRepository<RddDownLocDtlEntity, Integer>{
	
	@Query(value = "SELECT  DISTINCT ORD_NO FROM TB_R_INV_PART_D   WHERE  INV_NO  = :invNumber ORDER BY ORD_NO", nativeQuery = true)
	List<String> getOrderNumbers(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT  DISTINCT CO_CD   FROM    TB_R_INV_PART_D     WHERE   INV_NO = :invNumber    ORDER BY CO_CD ", nativeQuery = true)
	List<String> getCountryCode(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT NPTM.PRIV_NM   ICO_DESC, NPTM.SORT_SEQ,  SUM(INP.UNIT_PER_BOX) SUM_TOT_UNIT,  SUM(INP.UNIT_PER_BOX * INP.PRC) AMOUNT  FROM  TB_R_INV_PART_D INP, TB_M_PRIVILEGE_TYPE  NPTM   WHERE   INP.INV_NO = :invNumber  AND INP.AICO_FLG  = NPTM.PRIV_CD  GROUP BY  NPTM.PRIV_NM, NPTM.SORT_SEQ  ORDER BY NPTM.SORT_SEQ;", nativeQuery = true)
	List<Tuple> getTariff(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT MAX(CASE WHEN PARA_CD ='COUNTRY' THEN PARA_VAL ELSE NULL END)  COUNTRY_NM, MAX(CASE WHEN PARA_CD ='CMP_CD' THEN PARA_VAL ELSE NULL END) CMP_CD, MAX(CASE WHEN PARA_CD ='XDOC_CNT' THEN PARA_VAL ELSE NULL END) XDOC_CNT FROM  TB_M_PARAMETER WHERE PARA_CD IN ('COUNTRY','CMP_CD','XDOC_CNT')", nativeQuery = true)
	Tuple getParameterDetails();
	
	 
	@Query(value = "SELECT coalesce(PARA_VAL, 'N') FROM TB_M_PARAMETER WHERE PARA_CD = 'CNTRY_ORG'", nativeQuery = true)
	String  getCountryOfOrigin();
	
	@Query(value="SELECT TMAPTH_INV_FLG FROM TB_R_INV_INVOICE_H WHERE INV_NO= :invNumber",nativeQuery = true)
	String getTmapthInvFlg(@Param("invNumber") String invNumber);
	
	@Query(value="SELECT COUNT(1)\r\n"
			+ "     FROM\r\n"
			+ "         TB_R_INV_INVOICE_H      IND,\r\n"
			+ "         TB_M_PORT      PM1,\r\n"
			+ "         TB_M_PORT      PM2,\r\n"
			+ "         TB_M_PAYMENT_TERM PT,\r\n"
			+ "         TB_M_CURRENCY  CRM\r\n"
			+ "     WHERE\r\n"
			+ "          INV_NO        = :invNumber\r\n"
			+ "     AND  IND.DEST_PORT_CD  =  PM1.CD   \r\n"
			+ "     AND  IND.DEP_PORT_CD   =  PM2.CD  \r\n"
			+ "     AND  IND.PAY_TERM      =  PT.CD\r\n"
			+ "     AND  IND.PAY_CRNCY     =  CRM.CD",nativeQuery = true)
	Integer getCount(@Param("invNumber") String invNumber);
	
	@Query(value="SELECT XDOC,FINAL_DST FROM TB_R_INV_INVOICE_H WHERE INV_NO= :invNumber",nativeQuery = true)
	Tuple getCrossDoc(@Param("invNumber") String invNumber);
	
	@Query(value="SELECT DST_NM FROM TB_M_FINAL_DESTINATION  WHERE DST_CD = :finalDest",nativeQuery = true)
	String getDestName(@Param("finalDest") String finalDest);
	
	@Query(value="SELECT coalesce(LENGTH(MARK4),0),coalesce(LENGTH(MARK5),0), coalesce(LENGTH(MARK6),0) FROM TB_R_INV_INVOICE_H WHERE INV_NO = :invNumber",nativeQuery = true)
	Tuple getshippingMarkdetails(@Param("invNumber") String invNumber);
	
	@Query(value="SELECT  IND.INV_NO INV_NO,\r\n"
			+ "		DATE_FORMAT(IND.INV_DT,'%d/%c/%Y') INV_DT,\r\n"
			+ "		IND.CUST_NM CUST_NM,\r\n"
			+ "		IND.CUST_ADDR1 CUST_ADDR1,\r\n"
			+ "		IND.CUST_ADDR2 CUST_ADDR2,\r\n"
			+ "		IND.CUST_ADDR3 CUST_ADDR3,\r\n"
			+ "		IND.CUST_ADDR4 CUST_ADDR4,\r\n"
			+ "		IND.CUST_ZIP CUST_ZIP,\r\n"
			+ "		DATE_FORMAT(IND.ETD,'%d/%c/%Y') ETD_DT,\r\n"
			+ "		DATE_FORMAT(IND.ETA,'%d/%c/%Y') ETA_DT,\r\n"
			+ "		-- UNDER 'VESSEL DESC'\r\n"
			+ "		IND.VESSEL_NAME_OCEAN VESSEL_NAME_OCEAN,\r\n"
			+ "		IND.VOYAGE_NO_OCEAN VOYAGE_NO_OCEAN,\r\n"
			+ "		IND.VESSEL_NAME_FEEDER VESSEL_NAME_FEEDER,\r\n"
			+ "		IND.VOYAGE_NO_FEEDER VOYAGE_NO_FEEDER,\r\n"
			+ "		PM1.NAME NAME,\r\n"
			+ "		FD.DST_NM                                                                             BUYER_CNTRY,\r\n"
			+ "		PT.DESCRIPTION                                                                               PT_DESC,\r\n"
			+ "		IND.GOODS_DESC1                                                                      GOODS_DESC1,\r\n"
			+ "		IND.GOODS_DESC2                                                                      GOODS_DESC2,\r\n"
			+ "		TRIM(IND.GOODS_DESC3)                                                                GOODS_DESC3,\r\n"
			+ "		IND.TRADE_TRM                                                                        TRADE_TRM,\r\n"
			+ "		PM2.NAME                                                                              NAME2,\r\n"
			+ "		IND.INV_AMT                                                                          INV_AMT,\r\n"
			+ "		IND.INV_QTY                                                                          INV_QTY,\r\n"
			+ "		IND.GROSS_WT                                                                         GROSS_WT,\r\n"
			+ "		IND.NET_WT                                                                           NET_WT,\r\n"
			+ "		IND.MEASUREMENT                                                                      MEASUREMENT,\r\n"
			+ "		IND.NO_OF_CASES                                                                      NO_OF_CASES,\r\n"
			+ "		COALESCE(IND.FREIGHT,0.00)                                                        FREIGHT,\r\n"
			+ "		COALESCE(IND.INSURANCE,0.00)                                                           INSURANCE,\r\n"
			+ "		IND.MARK1                                                                            MARK1,\r\n"
			+ "		IND.MARK2                                                                            MARK2,\r\n"
			+ "		IND.MARK3                                                                            MARK3,\r\n"
			+ "		CASE RTRIM(IND.MARK4) WHEN NULL THEN '' ELSE CONCAT('LOT NO. : ',SUBSTR(IND.MARK4,1,75)) END MARK4_1,\r\n"
			+ "		CASE RTRIM(IND.MARK4) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK4,76,75) END MARK4_2,\r\n"
			+ "		CASE RTRIM(IND.MARK4) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK4,151,75) END MARK4_3,\r\n"
			+ "		CASE RTRIM(IND.MARK4) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK4,226,75) END MARK4_4,\r\n"
			+ "		CASE RTRIM(IND.MARK5) WHEN NULL THEN '' ELSE CONCAT('CASE NO. : ',SUBSTR(IND.MARK5,1,90)) END MARK5_1,\r\n"
			+ "		CASE RTRIM(IND.MARK5) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK5,91,100) END MARK5_2,\r\n"
			+ "		CASE RTRIM(IND.MARK5) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK5,191,100) END MARK5_3,\r\n"
			+ "		CASE RTRIM(IND.MARK5) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK5,291,90) END MARK5_4,\r\n"
			+ "		CASE RTRIM(IND.MARK6) WHEN NULL THEN '' ELSE CONCAT('Container Renban : ',SUBSTR(IND.MARK6,1,66)) END MARK6_1,\r\n"
			+ "		CASE RTRIM(IND.MARK6) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK6,67,84) END MARK6_2,\r\n"
			+ "		CASE RTRIM(IND.MARK6) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK6,151,84) END MARK6_3,\r\n"
			+ "		CASE RTRIM(IND.MARK6) WHEN NULL THEN '' ELSE SUBSTR(IND.MARK6,235,65) END MARK6_4,\r\n"
			+ "		IND.MARK7                                                                            MARK7,\r\n"
			+ "		IND.MARK8                                                                            MARK8,\r\n"
			+ "		IND.BUYER_NM                                                                         BUYER_NM,\r\n"
			+ "		IND.BUYER_ADDR1                                                                      BUYER_ADDR1,\r\n"
			+ "		IND.BUYER_ADDR2                                                                      BUYER_ADDR2,\r\n"
			+ "		IND.BUYER_ADDR3                                                                      BUYER_ADDR3,\r\n"
			+ "		IND.BUYER_ADDR4                                                                      BUYER_ADDR4,\r\n"
			+ "		IND.BUYER_ZIP                                                                        BUYER_ZIP,\r\n"
			+ "		CRM.CURR_PRNT_NM                                                                     CURR_PRNT_NM,\r\n"
			+ "		TRIM(IND.GOODS_DESC4)                                                                GOODS_DESC4,\r\n"
			+ "		TRIM(IND.GOODS_DESC5)                                                                GOODS_DESC5,\r\n"
			+ "		TRIM(IND.GOODS_DESC6)                                                                GOODS_DESC6,\r\n"
			+ "		IND.HAISEN_NO                                                                        HAISEN_NO,\r\n"
			+ "		IND.NOTIFY                                                                           NOTIFY,\r\n"
			+ "		IND.NOTIFY_ADDR1                                                                     NOTIFY_ADDR1,\r\n"
			+ "		IND.NOTIFY_ADDR2                                                                     NOTIFY_ADDR2,\r\n"
			+ "		IND.NOTIFY_ADDR3                                                                     NOTIFY_ADDR3,\r\n"
			+ "		IND.NOTIFY_ADDR4                                                                     NOTIFY_ADDR4,\r\n"
			+ "		(SELECT DST_NM FROM TB_M_FINAL_DESTINATION\r\n"
			+ "				WHERE  SUBSTR(DST_CD,1,3) = NOTIFY_CNTRY\r\n"
			+ "				LIMIT  1)                                                                    NOTIFY_CNTRY,\r\n"
			+ "		IND.NOTIFY_NM                                                                        NOTIFY_NM,\r\n"
			+ "		IND.NOTIFY_ZIP                                                                       NOTIFY_ZIP,\r\n"
			+ "		XDOC                                                                                 XDOC_FLG\r\n"
			+ "		FROM    TB_R_INV_INVOICE_H IND ,\r\n"
			+ "		TB_M_PORT       PM1,\r\n"
			+ "		TB_M_PORT       PM2,\r\n"
			+ "		TB_M_PAYMENT_TERM  PT,\r\n"
			+ "		TB_M_CURRENCY   CRM,\r\n"
			+ "		(SELECT CNTRY_CD, DST_NM\r\n"
			+ "				FROM (SELECT DISTINCT SUBSTR(DST_CD,1,3) AS CNTRY_CD, DST_NM,\r\n"
			+ "						ROW_NUMBER() OVER ( PARTITION BY  SUBSTR(DST_CD,1,3)\r\n"
			+ "								ORDER BY SUBSTR(DST_CD,1,3) ) AS SR_NO\r\n"
			+ "						FROM    TB_M_FINAL_DESTINATION) a\r\n"
			+ "				WHERE   SR_NO = 1) FD\r\n"
			+ "		WHERE\r\n"
			+ "		IND.INV_NO        =  :invNumber\r\n"
			+ "		AND     IND.DEST_PORT_CD  =  PM1.CD          -- FOR PRINTING THE DISCHARGE PORT NAME.\r\n"
			+ "		AND     IND.DEP_PORT_CD   =  PM2.CD          -- FOR PRINTING THE DEPARTURE PORT NAME.\r\n"
			+ "		AND     IND.PAY_TERM      =  PT.CD\r\n"
			+ "		AND     IND.PAY_CRNCY     =  CRM.CD\r\n"
			+ "		AND     IND.CUST_CNTRY    =  FD.CNTRY_CD\r\n"
			+ "",nativeQuery = true)
	Tuple getInvoiceDetails(@Param("invNumber") String invNumber);
	
	
	@Query(value="SELECT\r\n"
			+ "		IND.INV_NO                                                                           INV_NO,\r\n"
			+ "		DATE_FORMAT(IND.INV_DT,'%d/%c/%Y') INV_DT,\r\n"
			+ "		IND.CUST_NM                                                                          CUST_NM,\r\n"
			+ "		IND.CUST_ADDR1                                                                       CUST_ADDR1,\r\n"
			+ "		IND.CUST_ADDR2                                                                       CUST_ADDR2,\r\n"
			+ "		IND.CUST_ADDR3                                                                       CUST_ADDR3,\r\n"
			+ "		IND.CUST_ADDR4                                                                       CUST_ADDR4,\r\n"
			+ "		IND.CUST_ZIP                                                                         CUST_ZIP,\r\n"
			+ "		DATE_FORMAT(IND.ETD,'%d/%c/%Y') ETD_DT,\r\n"
			+ "		DATE_FORMAT(IND.ETA,'%d/%c/%Y') ETA_DT,\r\n"
			+ "		IND.VESSEL_NAME_OCEAN                                                                VESSEL_NAME_OCEAN,\r\n"
			+ "		IND.VOYAGE_NO_OCEAN                                                                  VOYAGE_NO_OCEAN,\r\n"
			+ "		IND.VESSEL_NAME_FEEDER                                                               VESSEL_NAME_FEEDER,\r\n"
			+ "		IND.VOYAGE_NO_FEEDER                                                                 VOYAGE_NO_FEEDER,\r\n"
			+ "		PM1.NAME                                                                              NAME,\r\n"
			+ "		FD.DST_NM                                                                             BUYER_CNTRY,\r\n"
			+ "		PT.DESCRIPTION PT_DESC,\r\n"
			+ "		IND.GOODS_DESC1                                                                      GOODS_DESC1,\r\n"
			+ "		IND.GOODS_DESC2                                                                      GOODS_DESC2,\r\n"
			+ "		TRIM(IND.GOODS_DESC3)                                                                GOODS_DESC3,\r\n"
			+ "		IND.TRADE_TRM                                                                        TRADE_TRM,\r\n"
			+ "		PM2.NAME                                                                              NAME2,\r\n"
			+ "		IND.INV_AMT                                                                          INV_AMT,\r\n"
			+ "		IND.INV_QTY                                                                          INV_QTY,\r\n"
			+ "		IND.GROSS_WT                                                                         GROSS_WT,\r\n"
			+ "		IND.NET_WT                                                                           NET_WT,\r\n"
			+ "		IND.MEASUREMENT                                                                      MEASUREMENT,\r\n"
			+ "		IND.NO_OF_CASES                                                                      NO_OF_CASES,\r\n"
			+ "		COALESCE(IND.FREIGHT,0.00)                                                                FREIGHT,\r\n"
			+ "		COALESCE(IND.INSURANCE,0.00)                                                              INSURANCE,\r\n"
			+ "		''                                                                                                                   MARK1,\r\n"
			+ "		''                                                                                       MARK2,\r\n"
			+ "		''                                                                                       MARK3,\r\n"
			+ "		''                                                                                                                   MARK4_1,\r\n"
			+ "		''                                                                                       MARK4_2,\r\n"
			+ "		''                                                                                       MARK4_3,\r\n"
			+ "		''                                                                                       MARK4_4,\r\n"
			+ "		''                                                                                                                                                                           MARK5_1,\r\n"
			+ "		''                                                                                       MARK5_2,\r\n"
			+ "		''                                                                                       MARK5_3,\r\n"
			+ "		''                                                                                       MARK5_4,\r\n"
			+ "		''                                                                                       MARK6_1,\r\n"
			+ "		''                                                                                       MARK6_2,\r\n"
			+ "		''                                                                                       MARK6_3,\r\n"
			+ "		''                                                                                       MARK6_4,\r\n"
			+ "		''                                                                                       MARK7,\r\n"
			+ "		''                                                                                       MARK8,\r\n"
			+ "		IND.BUYER_NM                                                                         BUYER_NM,\r\n"
			+ "		IND.BUYER_ADDR1                                                                      BUYER_ADDR1,\r\n"
			+ "		IND.BUYER_ADDR2                                                                      BUYER_ADDR2,\r\n"
			+ "		IND.BUYER_ADDR3                                                                      BUYER_ADDR3,\r\n"
			+ "		IND.BUYER_ADDR4                                                                      BUYER_ADDR4,\r\n"
			+ "		IND.BUYER_ZIP                                                                        BUYER_ZIP,\r\n"
			+ "		CRM.CURR_PRNT_NM                                                                     CURR_PRNT_NM,\r\n"
			+ "		TRIM(IND.GOODS_DESC4)                                                                GOODS_DESC4,\r\n"
			+ "		TRIM(IND.GOODS_DESC5)                                                                GOODS_DESC5,\r\n"
			+ "		TRIM(IND.GOODS_DESC6)                                                                GOODS_DESC6,\r\n"
			+ "		IND.HAISEN_NO                                                                        HAISEN_NO,\r\n"
			+ "		IND.NOTIFY                                                                           NOTIFY,\r\n"
			+ "		IND.NOTIFY_ADDR1                                                                     NOTIFY_ADDR1,\r\n"
			+ "		IND.NOTIFY_ADDR2                                                                     NOTIFY_ADDR2,\r\n"
			+ "		IND.NOTIFY_ADDR3                                                                     NOTIFY_ADDR3,\r\n"
			+ "		IND.NOTIFY_ADDR4                                                                     NOTIFY_ADDR4,\r\n"
			+ "		(SELECT DST_NM FROM TB_M_FINAL_DESTINATION\r\n"
			+ "				WHERE  SUBSTR(DST_CD,1,3) = NOTIFY_CNTRY\r\n"
			+ "				LIMIT 1)                                                                      NOTIFY_CNTRY,\r\n"
			+ "		IND.NOTIFY_NM                                                                        NOTIFY_NM,\r\n"
			+ "		IND.NOTIFY_ZIP                                                                       NOTIFY_ZIP,\r\n"
			+ "		XDOC                                                                                 XDOC_FLG\r\n"
			+ "		FROM    TB_R_INV_INVOICE_H IND ,\r\n"
			+ "		TB_M_PORT       PM1,\r\n"
			+ "		TB_M_PORT       PM2,\r\n"
			+ "		TB_M_PAYMENT_TERM  PT,\r\n"
			+ "		TB_M_CURRENCY   CRM,\r\n"
			+ "		(SELECT CNTRY_CD, DST_NM\r\n"
			+ "				FROM (SELECT DISTINCT SUBSTR(DST_CD,1,3) AS CNTRY_CD, DST_NM,\r\n"
			+ "						ROW_NUMBER() OVER ( PARTITION BY  SUBSTR(DST_CD,1,3)\r\n"
			+ "								ORDER BY SUBSTR(DST_CD,1,3) ) AS SR_NO\r\n"
			+ "						FROM    TB_M_FINAL_DESTINATION) a\r\n"
			+ "				WHERE   SR_NO = 1) FD\r\n"
			+ "		WHERE\r\n"
			+ "		IND.INV_NO        =  :invNumber\r\n"
			+ "		AND     IND.DEST_PORT_CD  =  PM1.CD          -- FOR PRINTING THE DISCHARGE PORT NAME.\r\n"
			+ "		AND     IND.DEP_PORT_CD   =  PM2.CD          -- FOR PRINTING THE DEPARTURE PORT NAME.\r\n"
			+ "		AND     IND.PAY_TERM      =  PT.CD\r\n"
			+ "		AND     IND.PAY_CRNCY     =  CRM.CD\r\n"
			+ "		AND     IND.CUST_CNTRY    =  FD.CNTRY_CD\r\n"
			+ "",nativeQuery = true)
	Tuple getInvoiceDetails2(@Param("invNumber") String invNumber);



}
