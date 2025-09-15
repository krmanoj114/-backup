package com.tpex.repository;

import java.sql.Date;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InsInvPartsDetailsEntity;
import com.tpex.entity.InsInvPartsDetailsIdEntity;

@Repository
public interface InsInvPartsDetailsRepository extends JpaRepository<InsInvPartsDetailsEntity, InsInvPartsDetailsIdEntity> {

	@Query(value = "SELECT DISTINCT ORD_NO,CF_CD,SERIES FROM TB_R_INV_PART_D WHERE INV_NO=:envNo", nativeQuery = true)
	List<Tuple>  getCarFamily(@Param("envNo") String envNo);
	// TPEX-189 for Invoice Attached Sheet
			@Query(value = "select  date_format(str_to_date(IND.INV_DT, '%Y-%m-%d'), '%d/%m/%Y') INV_DT, \r\n"
					+ "			  INP.PART_NO                       PART_NO,\r\n"
					+ "			  SUM(INP.UNIT_PER_BOX)             SUM_TOT_UNIT,\r\n"
					+ "			  INP.AICO_FLG                      ICO_FLG,			\r\n"
					+ "			  INP.PRC                           PRICE,\r\n"
					+ "			  INP.PART_NM                       PRT_NAME,			\r\n"
					+ "			  INP.CF_CD                         CF_CD,\r\n"
					+ "			  INP.SERIES                        SERIES,\r\n"
					+ "			  CRM.CURR_PRNT_NM                  CRM_CURR,\r\n"
					+ "			  NPTM.PRIV_NM                      ICO_DESC,\r\n"
					+ "			  INP.CO_CD                           CO_CD,\r\n"
					+ "			\r\n"
					+ "			 CASE WHEN INSTR(ORG_CRITERIA,'%') = 0 THEN  CASE \r\n"
					+ "			     WHEN  REGEXP_LIKE (ORG_CRITERIA,'^-?\\\\d(\\\\.\\\\d)?$')\r\n"
					+ "			     THEN((SELECT PARA_VAL FROM TB_M_PARAMETER WHERE PARA_CD = 'ORG_CRT_PREFIX') || ' ' ||         ORG_CRITERIA || '%')\r\n"
					+ "			     WHEN (ORG_CRITERIA  <> 0)                          \r\n"
					+ "			     THEN  COALESCE(ORG_CRITERIA,(SELECT PARA_VAL FROM TB_M_PARAMETER WHERE PARA_CD =         'ORG_CRT_PREFIX') || ' ' || ORG_CRITERIA,NULL)\r\n"
					+ "			          ELSE ORG_CRITERIA END\r\n"
					+ "			                     END ORG_CRITERIA,\r\n"
					+ "			\r\n"
					+ "			  IND.SC_AUTH_EMP,                          \r\n"
					+ "			  NPTM.SORT_SEQ SORT_SEQ,                   \r\n"
					+ "			  SUBSTR(REGEXP_REPLACE(INP.HS_CD, '[^0-9]', ''),1,6) HS_CD,\r\n"
					+ "			\r\n"
					+ "			  IND.FINAL_DST                     DST_CD,\r\n"
					+ "			  IND.MARK4                         IND_MARK4\r\n"
					+ " \r\n"
					+ "  FROM        TB_R_INV_PART_D INP,\r\n"
					+ "            TB_R_INV_INVOICE_H IND,\r\n"
					+ "            TB_M_CURRENCY CRM,\r\n"
					+ "            TB_M_PRIVILEGE_TYPE NPTM\r\n"
					+ "WHERE\r\n"
					+ "			 INP.INV_NO = :invoiceNo\r\n"
					+ "			   AND      INP.INV_NO    = IND.INV_NO\r\n"
					+ "			   AND      IND.PAY_CRNCY = CRM.CD\r\n"
					+ "			   AND      INP.AICO_FLG  = NPTM.PRIV_CD\r\n"
					+ "GROUP BY\r\n"
					+ "            INP.INV_DT,\r\n"
					+ "            INP.AICO_FLG,\r\n"
					+ "            INP.PART_NO,\r\n"
					+ "            INP.PRC,\r\n"
					+ "            INP.CF_CD ,\r\n"
					+ "            IND.FINAL_DST ,\r\n"
					+ "            IND.MARK4,\r\n"
					+ "            INP.SERIES,\r\n"
					+ "            INP.PART_NM,\r\n"
					+ "            CRM.CURR_PRNT_NM,\r\n"
					+ "            NPTM.PRIV_NM,\r\n"
					+ "            INP.CO_CD,\r\n"
					+ "            INP.ORG_CRITERIA,\r\n"
					+ "            IND.SC_AUTH_EMP,\r\n"
					+ "            NPTM.SORT_SEQ \r\n"
					+ "			    , SUBSTR(REGEXP_REPLACE(INP.HS_CD, '[^0-9]', ''),1,6) \r\n"
					+ "                \r\n"
					+ "   ORDER BY NPTM.SORT_SEQ", nativeQuery = true)
			List<Tuple>  getQuerydataforpins003(@Param("invoiceNo") String invoiceNo);
			
			@Query(value = "SELECT IFNULL(PARA_VAL, 'N') FROM TB_M_PARAMETER WHERE PARA_CD ='CNTRY_ORG'", nativeQuery = true)
		    List<Object> getOprParaVal();	
			
			@Query(value = "SELECT TMAPTH_INV_FLG,SC_REMARK FROM TB_R_INV_INVOICE_H WHERE INV_NO =:invoiceNo", nativeQuery = true)
		    Tuple getRemarks(@Param("invoiceNo") String invoiceNo);
			
			@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 \r\n"
					+ "						FROM TB_M_CNSG CMP, TB_M_PARAMETER OPR WHERE CMP.CD = :cmpCd\r\n"
					+ "						AND CMP.INV_ADD_FLG = 'Y' \r\n"
					+ "						AND CMP.COMPANY = OPR.PARA_VAL \r\n"
					+ "						AND OPR.PARA_CD = FN_GET_COMPANY_CD(:invoiceNo)", nativeQuery = true)
		    Tuple getInvCompDetailsWhenFlgY(@Param("cmpCd") String cmpCd, @Param("invoiceNo") String invoiceNo);
			
			@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 \r\n"
					+ "						FROM TB_M_CNSG CMP, TB_M_PARAMETER OPR WHERE CMP.CD = :cmpCd\r\n"
					+ "						AND CMP.INV_ADD_FLG = 'Y' \r\n"
					+ "						AND CMP.COMPANY = OPR.PARA_VAL \r\n"
					+ "						AND OPR.PARA_CD = FN_GET_COMPANY_CD(:invoiceNo)", nativeQuery = true)
		    Tuple getInvCompDetailWhenFlgN(@Param("cmpCd") String cmpCd, @Param("invoiceNo") String invoiceNo);	
			
			// TPEX-280 for SC Invoice Attached Sheet
			
			@Query(value = "select  date_format(str_to_date(IND.INV_DT, '%Y-%m-%d'), '%d/%m/%Y') INV_DT, \r\n"
					+ "			  INP.PART_NO                       PART_NO,\r\n"
					+ "			  SUM(INP.UNIT_PER_BOX)             SUM_TOT_UNIT,\r\n"
					+ "			  INP.AICO_FLG                      ICO_FLG,			\r\n"
					+ "			  INP.PRC                           PRICE,\r\n"
					+ "			  INP.PART_NM                       PRT_NAME,			\r\n"
					+ "			  INP.CF_CD                         CF_CD,\r\n"
					+ "			  INP.SERIES                        SERIES,\r\n"
					+ "			  CRM.CURR_PRNT_NM                  CRM_CURR,\r\n"
					+ "			  NPTM.PRIV_NM                      ICO_DESC,\r\n"
					+ "			  INP.CO_CD                           CO_CD,\r\n"
					+ "			\r\n"
					+ "			 CASE WHEN INSTR(ORG_CRITERIA,'%') = 0 THEN  CASE \r\n"
					+ "			     WHEN  REGEXP_LIKE (ORG_CRITERIA,'^-?\\\\d(\\\\.\\\\d)?$')\r\n"
					+ "			     THEN((SELECT PARA_VAL FROM TB_M_PARAMETER WHERE PARA_CD = 'ORG_CRT_PREFIX') || ' ' ||         ORG_CRITERIA || '%')\r\n"
					+ "			     WHEN (ORG_CRITERIA  <> 0)                          \r\n"
					+ "			     THEN  COALESCE(ORG_CRITERIA,(SELECT PARA_VAL FROM TB_M_PARAMETER WHERE PARA_CD =         'ORG_CRT_PREFIX') || ' ' || ORG_CRITERIA,NULL)\r\n"
					+ "			          ELSE ORG_CRITERIA END\r\n"
					+ "			                     END ORG_CRITERIA,\r\n"
					+ "			\r\n"
					+ "			  IND.SC_AUTH_EMP,                          \r\n"
					+ "			  NPTM.SORT_SEQ SORT_SEQ,                   \r\n"
					+ "			  SUBSTR(REGEXP_REPLACE(INP.HS_CD, '[^0-9]', ''),1,6) HS_CD,\r\n"
					+ "			\r\n"
					+ "			  IND.FINAL_DST                     DST_CD,\r\n"
					+ "			  IND.MARK4                         IND_MARK4\r\n"
					+ " \r\n"
					+ "  FROM        TB_R_INV_PART_D INP,\r\n"
					+ "            TB_R_INV_INVOICE_H IND,\r\n"
					+ "            TB_M_CURRENCY CRM,\r\n"
					+ "            TB_M_PRIVILEGE_TYPE NPTM\r\n"
					+ "WHERE\r\n"
					+ "			 INP.INV_NO = :invoiceNo\r\n"
					+ "			   AND      INP.INV_NO    = IND.INV_NO\r\n"
					+ "			   AND      IND.PAY_CRNCY = CRM.CD\r\n"
					+ "			   AND      INP.AICO_FLG  = NPTM.PRIV_CD\r\n"
					+ "GROUP BY\r\n"
					+ "            INP.INV_DT,\r\n"
					+ "            INP.AICO_FLG,\r\n"
					+ "            INP.PART_NO,\r\n"
					+ "            INP.PRC,\r\n"
					+ "            INP.CF_CD ,\r\n"
					+ "            IND.FINAL_DST ,\r\n"
					+ "            IND.MARK4,\r\n"
					+ "            INP.SERIES,\r\n"
					+ "            INP.PART_NM,\r\n"
					+ "            CRM.CURR_PRNT_NM,\r\n"
					+ "            NPTM.PRIV_NM,\r\n"
					+ "            INP.CO_CD,\r\n"
					+ "            INP.ORG_CRITERIA,\r\n"
					+ "            IND.SC_AUTH_EMP,\r\n"
					+ "            NPTM.SORT_SEQ \r\n"
					+ "			    , SUBSTR(REGEXP_REPLACE(INP.HS_CD, '[^0-9]', ''),1,6) \r\n"
					+ "                \r\n"
					+ "   ORDER BY NPTM.SORT_SEQ ", nativeQuery = true)
			List<Tuple>  getScQuerydataforpins003(@Param("invoiceNo") String invoiceNo);
			@Query(value = "SELECT TMAPTH_INV_FLG,SC_REMARK FROM TB_R_INV_INVOICE_H WHERE INV_NO =:invoiceNo", nativeQuery = true)
		    Tuple getScRemarks(@Param("invoiceNo") String invoiceNo);
			
			@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 \r\n"
					+ "			FROM TB_M_CNSG CMP, TB_M_PARAMETER OPR WHERE CMP.CD = :cmpCd \r\n"
					+ "			AND CMP.INV_ADD_FLG = 'Y' \r\n"
					+ "			AND CMP.COMPANY = OPR.PARA_VAL \r\n"
					+ "			AND OPR.PARA_CD = FN_GET_COMPANY_CD(:invoiceNo)", nativeQuery = true)
		    Tuple getScInvCompDetailsWhenFlgY(@Param("cmpCd") String cmpCd, @Param("invoiceNo") String invoiceNo);
			
			@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 \r\n"
					+ "						FROM TB_M_CNSG CMP, TB_M_PARAMETER OPR WHERE CMP.CD = :cmpCd\r\n"
					+ "						AND CMP.INV_ADD_FLG = 'N' \r\n"
					+ "						AND CMP.COMPANY = OPR.PARA_VAL \r\n"
					+ "						AND OPR.PARA_CD = FN_GET_COMPANY_CD(:invoiceNo)", nativeQuery = true)
		    Tuple getScInvCompDetailWhenFlgN(@Param("cmpCd") String cmpCd, @Param("invoiceNo") String invoiceNo);
			
			@Query(value = "SELECT COUNT(*) FROM TB_R_INV_PART_D WHERE IMP_CD=:impCode AND CF_CD=:cfCode AND PART_NO=:partNo AND date_format(INV_DT,'%Y%m')>=:effFromDate and date_format(INV_DT,'%Y%m')<=:effToDate", nativeQuery = true)
			int countInvoiceGeneratedForPartPrice(@Param("impCode") String impCode, @Param("cfCode") String cfCode, 
					@Param("partNo") String partNo, @Param("effFromDate") String effFromDate, @Param("effToDate") String effToDate);
	}

