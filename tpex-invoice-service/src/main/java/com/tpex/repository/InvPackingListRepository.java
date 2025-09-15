package com.tpex.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.RddDownLocDtlEntity;

@Repository
public interface InvPackingListRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {

	
	@Query(value = "SELECT PARA_VAL CD FROM TB_M_PARAMETER WHERE PARA_CD = 'CMP_CD'", nativeQuery = true)  //TODO: remove this query, make it input param
	String getCompanyCode();
	
	@Query(value = "SELECT TMAPTH_INV_FLG FROM TB_R_INV_INVOICE_H WHERE INV_NO = :invNumber", nativeQuery = true)
	String getTmapthInvFlg(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT CASE WHEN INV_DT >= (SELECT str_to_date(PARA_VAL,'%d%m%Y') FROM TB_M_PARAMETER WHERE PARA_CD = 'CMP_NM_CUTOFF') THEN 'TMAPTH_CMP' ELSE 'TMAPTH_CMPO' END COMP_CD FROM  TB_R_INV_INVOICE_H WHERE INV_NO = :invNumber", nativeQuery = true)
			String getCompCode(@Param("invNumber") String invNumber);
	
	
	@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 FROM TB_M_CNSG CMP, TB_M_PARAMETER OPR WHERE  CMP.CMP_CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY = OPR.PARA_VAL AND OPR.PARA_CD = :compCode", nativeQuery = true) //TODO: WHAT IS PKG_OEM_UTL.FN_GET_COMPANY_CD(p_i_v_invoice_no); ?????
	Tuple getInvCompDetailWhenFlgY(@Param("companyCode") String companyCode, @Param("compCode") String compCode); //TODO: replace hard coded values
	
	@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 FROM TB_M_CNSG CMP WHERE CMP.CMP_CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY IN ('OTHER','TMT')", nativeQuery = true)
	Tuple getInvCompDetailWhenFlgN(@Param("companyCode") String companyCode); //TODO: replace hard coded values

	@Query(value = "SELECT TO_CHAR(IND.INV_DT,'DD/MM/YYYY') INV_DT, INP.INP_PART_NO PART_NO, INP.INP_UNIT_PER_BOX UNIT_BOX, INP.INP_NET_WT PART_WT, INP.INP_CF_CD CF_CD, "
			+ "IND.FINAL_DST DST_CD, INP.INP_MOD_NO SHIP_MARK, IND.MARK4 SHIP_MARK4, INP.INP_SERIES SERIES, INP.INP_PART_NM PRT_NAME, SUM(INP.INP_UNIT_PER_BOX) SUM_TOT_UNIT "
			+ "FROM INS_INV_PARTS_DTLS INP, TB_R_INV_INVOICE_H IND "
			+ "WHERE INP.INP_INV_NO  = :invNumber AND INP.INP_INV_NO = IND.INV_NO "
			+ "GROUP BY INP.INP_PART_NO, INP.INP_UNIT_PER_BOX, INP.INP_NET_WT, INP.INP_CF_CD, IND.FINAL_DST, INP.INP_MOD_NO, IND.MARK4, INP.INP_SERIES, INP.INP_PART_NM, TO_CHAR(IND.INV_DT,'DD/MM/YYYY') "
			+ "ORDER BY INP.INP_MOD_NO", nativeQuery = true)
	Object getPartDetailData1(@Param("invNumber") String invNumber); //TODO: replace hard coded values


	/*@Query(value = "SELECT\r\n" + 
	"            TO_CHAR(IND.INV_DT,'DD/MM/YYYY')INV_DT,INP.INP_PART_NO PART_NO,INP.INP_UNIT_PER_BOX UNIT_BOX,INP.INP_NET_WT PART_WT,INP.INP_CF_CD CF_CD, IND.FINAL_DST DST_CD,INP.INP_MOD_NO SHIP_MARK,IND.MARK4 SHIP_MARK4,INP.INP_SERIES SERIES,INP.INP_PART_NM PRT_NAME,SUM(INP.INP_UNIT_PER_BOX) SUM_TOT_UNIT\r\n" + 
	"   FROM\r\n" + 
	"            INS_INV_PARTS_DTLS          INP,\r\n" + 
	"            TB_R_INV_INVOICE_H                IND\r\n" + 
	"   WHERE\r\n" + 
	"            INP.INP_INV_NO  = :invNumber\r\n" + 
	"   AND      INP.INP_INV_NO  =  IND.INV_NO\r\n" + 
	"   GROUP BY\r\n" + 
	"            INP.INP_PART_NO,\r\n" + 
	"            INP.INP_UNIT_PER_BOX ,\r\n" + 
	"            INP.INP_NET_WT,\r\n" + 
	"            INP.INP_CF_CD,\r\n" + 
	"            IND.FINAL_DST,\r\n" + 
	"            INP.INP_MOD_NO,\r\n" + 
	"            IND.MARK4,\r\n" + 
	"            INP.INP_SERIES,\r\n" + 
	"            INP.INP_PART_NM,\r\n" + 
	"            TO_CHAR(IND.INV_DT,'DD/MM/YYYY')\r\n" + 
	"   ORDER BY\r\n" + 
	"            INP.INP_MOD_NO", nativeQuery = true)
	List<Tuple> getPartDetailData(@Param("invNumber") String invNumber); //KR22100530, KR22102669
	*/
	
	
	@Query(value = "SELECT\r\n" + 
			"            date_format(str_to_date(IND.INV_DT, '%Y-%m-%d'), '%d-%m-%Y')     INV_DT,\r\n" + 
			"            INP.INP_PART_NO                             PART_NO,\r\n" + 
			"            INP.INP_UNIT_PER_BOX                        UNIT_BOX,\r\n" + 
			"            INP.INP_NET_WT                              PART_WT,\r\n" + 
			"            INP.INP_CF_CD                               CF_CD ,\r\n" + 
			"            IND.FINAL_DST                           DST_CD,\r\n" + 
			"            INP.INP_MOD_NO                              SHIP_MARK,\r\n" + 
			"            IND.MARK4                               SHIP_MARK4,\r\n" + 
			"            INP.INP_SERIES                              SERIES,\r\n" + 
			"            INP.INP_PART_NM                             PRT_NAME,\r\n" + 
			"            SUM(INP.INP_UNIT_PER_BOX)                   SUM_TOT_UNIT\r\n" + 
			"   FROM\r\n" + 
			"            INS_INV_PARTS_DTLS          INP,\r\n" + 
			"            TB_R_INV_INVOICE_H                IND\r\n" + 
			"   WHERE\r\n" + 
			"            INP.INP_INV_NO  = :invNumber\r\n" + 
			"   AND      INP.INP_INV_NO  =  IND.INV_NO\r\n" + 
			"   GROUP BY\r\n" + 
			"            INP.INP_PART_NO,\r\n" + 
			"            INP.INP_UNIT_PER_BOX ,\r\n" + 
			"            INP.INP_NET_WT,\r\n" + 
			"            INP.INP_CF_CD,\r\n" + 
			"            IND.FINAL_DST,\r\n" + 
			"            INP.INP_MOD_NO,\r\n" + 
			"            IND.MARK4,\r\n" + 
			"            INP.INP_SERIES,\r\n" + 
			"            INP.INP_PART_NM,\r\n" + 
			"            date_format(str_to_date(IND.INV_DT, '%Y-%m-%d'),'%d-%m-%Y')          \r\n" + 
			"			ORDER BY\r\n" + 
			"            INP.INP_MOD_NO", nativeQuery = true)
			List<Tuple> getPartDetailData(@Param("invNumber") String invNumber); //KR22100530, KR22102669
	
	
	
	@Query(value = "SELECT\r\n" + 
	"                   SUM(INM.INM_GROSS_WT),\r\n" + 
	"                   SUM(INM.INM_TOT_M3)\r\n" + 
	"              FROM\r\n" + 
	"                   INS_INV_MODULE_DTLS INM\r\n" + 
	"              WHERE\r\n" + 
	"                   INM.INM_MOD_NO       = :shipMark\r\n" + 
	"              AND  INM.INM_INV_NO       = :invNumber\r\n" + 
	"              AND INM.INM_CF_CD         = :cfCd", nativeQuery = true)
	Tuple getGrossWeightAndMeasurement(@Param("shipMark") String shipMark, @Param("invNumber") String invNumber, @Param("cfCd") String cfCd);
	
	
	@Query(value = "SELECT\r\n" + 
	"                         COUNT(1)\r\n" + 
	"              FROM\r\n" + 
	"                   INS_INV_MODULE_DTLS INM\r\n" + 
	"              WHERE\r\n" + 
	"                   INM.INM_MOD_NO       = :shipMark\r\n" + 
	"              AND  INM.INM_INV_NO       = :invNumber\r\n" + 
	"              AND  INM.INM_CF_CD        = :cfCd", nativeQuery = true)
	Tuple getNoMod(@Param("shipMark") String shipMark, @Param("invNumber") String invNumber, @Param("cfCd") String cfCd);
	
	//@Transactional
	 // @Procedure(procedureName = "PINS002", )
	 // List<Tuple> getProcData(@Param("invCursor") ResultSet invCursor,@Param("invNumber") String invNumber,@Param("userId") String userId);

	
	
	/*
	 * @Query(value =
	 * "SELECT INM_GROSS_WT,INM_TOT_M3,INM_MOD_NO,INM_INV_NO,INM_CF_CD FROM INS_INV_MODULE_DTLS WHERE INM_INV_NO = 'KR22100530'"
	 * , nativeQuery = true) List<Tuple> getData(@Param("invNumber") String
	 * invNumber);
	 */
	
	@Query(value = "SELECT  CONCAT(INM_CF_CD , '-' , INM_MOD_NO) AS CFCD_MOD_NO, sum(INM_GROSS_WT),sum(INM_TOT_M3), COUNT(1) NO_OF_MOD FROM INS_INV_MODULE_DTLS WHERE INM_INV_NO = :invNumber " + 
			"group by INM_CF_CD, INM_MOD_NO", nativeQuery = true)
	List<Tuple> getData(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT COUNT(1) FROM INS_INV_MODULE_DTLS", nativeQuery = true)
	Long getModData();
	
	//********************************************************
	
/*	@Query(value = "SELECT IFNULL(PARA_VAL, 'N') FROM TB_M_PARAMETER WHERE PARA_CD ='CNTRY_ORG'", nativeQuery = true)
	String getCountryOfOrigin();
	
	
	@Query(value = "SELECT FD.FD_DST_NM FROM TB_R_INV_INVOICE_H IND, OEM_FNL_DST_MST FD WHERE IND.INV_NO = 'KR22100530' AND IND.FINAL_DST = FD.FD_DST_CD", nativeQuery = true)
	String getFinalCountryName(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT date_format(str_to_date(IND.INV_DT, '%Y-%m-%d'), '%d-%m-%Y')			INV_DT,\r\n" + 
	"           IND.MARK1                                                                            MARK1,\r\n" + 
	"           IND.MARK2                                                                            MARK2,\r\n" + 
	"           IND.MARK3                                                                            MARK3,\r\n" + 
	"           IND.MARK4                                                                            MARK4,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE CONCAT('LOT NO. : ' , SUBSTR(IND.MARK4,1,75)) END   MARK4_1,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,76,85)  END          MARK4_2, \r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,161,85) END                       MARK4_3,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,246,85) END                       MARK4_4,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,331,85) END                       MARK4_5,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,416,85) END                       MARK4_6,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,501,85) END                       MARK4_7,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,586,85) END                       MARK4_8,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,671,85) END                       MARK4_9,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,756,85) END                       MARK4_10,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,841,85) END                       MARK4_11,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,926,74) END                       MARK4_12,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE CONCAT('CASE NO. : ' , SUBSTR(IND.MARK5,1,75)) END    MARK5_1,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,76,85)  END                       MARK5_2,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,161,85) END                       MARK5_3,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,246,85) END                       MARK5_4,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,331,85) END                       MARK5_5,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,416,85) END                       MARK5_6,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,501,85) END                       MARK5_7,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,586,85) END                       MARK5_8,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,671,85) END                       MARK5_9,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,756,85) END                       MARK5_10,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,841,85) END                       MARK5_11,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,926,74) END                       MARK5_12,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE CONCAT('Container Renban : ' , SUBSTR(IND.MARK6,1,66)) END MARK6_1,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,67,85)  END                       MARK6_2,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,152,85) END                       MARK6_3,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,237,85) END                       MARK6_4,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,322,85) END                       MARK6_5,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,407,85) END                       MARK6_6,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,492,85) END                       MARK6_7,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,577,85) END                       MARK6_8,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,662,85) END                       MARK6_9,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,747,85) END                       MARK6_10,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,832,85) END                       MARK6_11,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,917,83) END                       MARK6_12,\r\n" + 
	"           IND.MARK7                                                                            MARK7,\r\n" + 
	"           IND.MARK8  \r\n" + 
	"    FROM   TB_R_INV_INVOICE_H IND\r\n" + 
	"    WHERE\r\n" + 
	"           IND.INV_NO        =  'KR22100530';\r\n" , nativeQuery = true)
	Tuple getInvoiceHeaderPageDetail(@Param("invNumber") String invNumber);
	*/
}
