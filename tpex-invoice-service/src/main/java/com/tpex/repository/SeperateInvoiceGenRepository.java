package com.tpex.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.RddDownLocDtlEntity;

@Repository

public interface SeperateInvoiceGenRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {


	@Query(value = "SELECT CASE WHEN INV_DT >= (SELECT str_to_date(PARA_VAL,'%d%m%Y') FROM TB_M_PARAMETER WHERE PARA_CD = 'CMP_NM_CUTOFF') THEN 'TMAPTH_CMP' ELSE 'TMAPTH_CMPO' END COMP_CD FROM  TB_R_INV_INVOICE_H WHERE INV_NO = :invNumber", nativeQuery = true)
	String getCompCode(@Param("invNumber") String invNumber);

	@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 FROM TB_M_CNSG CMP, TB_M_PARAMETER OPR WHERE  CMP.CMP_CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY = OPR.PARA_VAL AND OPR.PARA_CD = :compCode", nativeQuery = true)
	List<Object[]> getInvCompDetailWhenFlgY(@Param("companyCode") String companyCode,
			@Param("compCode") String compCode);

	@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 FROM TB_M_CNSG CMP WHERE CMP.CMP_CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY IN ('OTHER','TMT')", nativeQuery = true)
	List<Object[]> getInvCompDetailWhenFlgN(@Param("companyCode") String companyCode);

	@Query(value = "SELECT\r\n"
			+ "         INP.INV_NO as INS_INV_NO,\r\n"
			+ "         DATE_FORMAT(INP.INV_DT, '%d/%m/%Y') INV_DT,\r\n"
			+ "         INP.PART_NO AS PART_NO,\r\n"
			+ "         INP.UNIT_PER_BOX,\r\n"
			+ "         SUM(INP.UNIT_PER_BOX) AS SUM_TOT_UNIT,\r\n"
			+ "         INP.COE_AICO_FLG AS ICO_FLG,\r\n"
			+ "         INP.PRC AS PRICE,\r\n"
			+ "         INP.PART_NM AS PRT_NAME,\r\n"
			+ "         TMP.WEIGHT AS PART_WT,\r\n"
			+ "         IND.GROSS_WT ,\r\n"
			+ "         IND.MEASUREMENT,\r\n"
			+ "         IND.MARK4 AS INS_SHIPMARK_4,\r\n"
			+ "         IND.MARK5 AS INS_SHIPMARK_5,\r\n"
			+ "         INP.CF_CD AS CF_CD,\r\n"
			+ "         INP.SERIES AS SERIES,\r\n"
			+ "         CRM.CURR_PRNT_NM AS CRM_CURR,\r\n"
			+ "         NPTM.PRIV_NM AS ICO_DESC,\r\n"
			+ "         INP.CO_CD AS CO_CD\r\n"
			+ "      FROM\r\n"
			+ "         TB_R_INV_PART_D INP,\r\n"
			+ "          TB_R_INV_INVOICE_H IND ,\r\n"
			+ "         TB_M_CURRENCY CRM ,\r\n"
			+ "         TB_M_PRIVILEGE_TYPE NPTM,\r\n"
			+ "         TB_M_PART TMP \r\n"
			+ "      WHERE\r\n"
			+ "         INP.INV_NO = :invoiceNo\r\n"
			+ "         AND INP.COE_AICO_FLG = :invoiceType\r\n"
			+ "         AND INP.INV_NO = IND.INV_NO\r\n"
			+ "         AND IND.PAY_CRNCY = CRM.CD\r\n"
			+ "         AND INP.AICO_FLG = NPTM.PRIV_CD\r\n"
			+ "         AND IND.cmp_cd=:companyCode\r\n"
			+ "         AND CRM.cmp_cd=:companyCode\r\n"
			+ "         AND NPTM.cmp_cd=:companyCode\r\n"
			+ "         AND TMP.PART_NO=INP.PART_NO\r\n"
			+ "      GROUP BY\r\n"
			+ "         INP.INV_NO,\r\n"
			+ "         INP.UNIT_PER_BOX,\r\n"
			+ "         INP.INV_DT,\r\n"
			+ "         INP.COE_AICO_FLG,\r\n"
			+ "         INP.PART_NO,\r\n"
			+ "         INP.PRC,\r\n"
			+ "         INP.CF_CD,\r\n"
			+ "         IND.FINAL_DST,\r\n"
			+ "         IND.MARK4,\r\n"
			+ "         INP.SERIES,\r\n"
			+ "         INP.PART_NM,\r\n"
			+ "         CRM.CURR_PRNT_NM,\r\n"
			+ "         NPTM.PRIV_NM,\r\n"
			+ "         INP.CO_CD,\r\n"
			+ "        IND.GROSS_WT,\r\n"
			+ "        IND.MARK5,\r\n"
			+ "        IND.MEASUREMENT,\r\n"
			+ "TMP.WEIGHT", nativeQuery = true)
	List<Object[]> getDataForSeperateInvoiceGeneration(@Param("invoiceNo") String invoiceNo,
			@Param("invoiceType") String invoiceType, @Param("companyCode") String companyCode);


	@Query(value = "SELECT IFNULL(PARA_VAL, 'N') FROM TB_M_PARAMETER WHERE PARA_CD ='CNTRY_ORG'", nativeQuery = true)
	String getCountryOfOrigin();

}
