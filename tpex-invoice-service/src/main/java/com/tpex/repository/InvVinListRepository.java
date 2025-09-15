package com.tpex.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tpex.entity.RddDownLocDtlEntity;

public interface InvVinListRepository extends JpaRepository<RddDownLocDtlEntity, Integer>{
	@Query(value = "SELECT TMAPTH_INV_FLG FROM TB_R_INV_INVOICE_H WHERE INV_NO = :invNumber", nativeQuery = true)
	String getTmapthInvFlg(@Param("invNumber") String invNumber);
	

	@Query(value = "SELECT CASE WHEN INV_DT >= (SELECT STR_TO_DATE(PARA_VAL,'%d%m%Y')\r\n"
			+ "                    FROM TB_M_PARAMETER WHERE PARA_CD = 'CMP_NM_CUTOFF') \r\n"
			+ "               THEN 'TMAPTH_CMP' ELSE 'TMAPTH_CMPO' \r\n"
			+ "               END COMP_CD   \r\n"
			+ "    FROM   TB_R_INV_INVOICE_H\r\n"
			+ "    WHERE  INV_NO=:invNumber", nativeQuery = true)
			String getCompCode(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 FROM TB_M_CNSG CMP, TB_M_PARAMETER OPR WHERE  CMP.CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY = OPR.PARA_VAL AND OPR.PARA_CD = :compCode", nativeQuery = true)
	Tuple getInvCompDetailWhenFlgY(@Param("companyCode") String companyCode,@Param("compCode") String compCode); 
	
	@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 FROM TB_M_CNSG CMP WHERE CMP.CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY IN ('OTHER',companyCode)", nativeQuery = true)
	Tuple getInvCompDetailWhenFlgN(@Param("companyCode") String companyCode);
	
	@Query(value = "SELECT\r\n"
			+ "          COUNT(1)\r\n"
			+ "     FROM\r\n"
			+ "             TB_R_INV_INVOICE_H IND,\r\n"
			+ "             TB_R_INV_MODULE_D INM,\r\n"
			+ "             TB_R_INV_PART_D INP,\r\n"
			+ "             TB_R_INV_VPR_VIN IIV\r\n"
			+ "      WHERE \r\n"
			+ "             IND.INV_NO    = :invNumber\r\n"
			+ "      AND    IND.INV_NO    = INM.INV_NO\r\n"
			+ "      AND    IND.INV_NO    = INP.INV_NO\r\n"
			+ "      AND    INM.INV_NO    = INP.INV_NO\r\n"
			+ "      AND    INM.MOD_NO    = INP.MOD_NO\r\n"
			+ "      AND    INM.LOT_NO    = INP.LOT_NO\r\n"
			+ "      AND    INM.LOT_NO    = IIV.LOT_CD\r\n"
			+ "      AND    IND.FINAL_DST = IIV.IMP_CD\r\n"
			+ "      AND    INP.EXP_CD    = IIV.EXP_CD", nativeQuery = true)
	Tuple getCtr(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT\r\n"
			+ "             DISTINCT INM.INV_NO,\r\n"
			+ "             DATE_FORMAT(INM.INV_DT,'%d/%m/%Y') INV_DT,\r\n"
			+ "             INM.LOT_NO,\r\n"
			+ "             CONCAT(IFNULL(IIV.VIN_WMI, '') , IFNULL(TRIM(IIV.VIN_VDS), '')\r\n"
			+ "                  , IFNULL(CASE CHAR_LENGTH(TRIM(IIV.VIN_VDS)) WHEN 6 THEN VIN_MOD_YR ELSE CONCAT(IFNULL(IIV.VIN_CHK_DGT, ''), IFNULL(VIN_MOD_YR, '')) END, '')\r\n"
			+ "                                  , IFNULL(IIV.VIN_FRM_NO, '')) AS INV_VIN_NO\r\n"
			+ "             \r\n"
			+ "      FROM\r\n"
			+ "             TB_R_INV_INVOICE_H IND,\r\n"
			+ "             TB_R_INV_MODULE_D INM,\r\n"
			+ "             TB_R_INV_PART_D INP,\r\n"
			+ "             TB_R_INV_VPR_VIN IIV\r\n"
			+ "      WHERE \r\n"
			+ "             IND.INV_NO    = :invNumber\r\n"
			+ "      AND    IND.INV_NO    = INM.INV_NO\r\n"
			+ "      AND    IND.INV_NO    = INP.INV_NO\r\n"
			+ "      AND    INM.INV_NO    = INP.INV_NO\r\n"
			+ "      AND    INM.MOD_NO    = INP.MOD_NO\r\n"
			+ "      AND    INM.LOT_NO    = INP.LOT_NO\r\n"
			+ "      AND    INM.LOT_NO    = IIV.LOT_CD\r\n"
			+ "      AND    IND.FINAL_DST = IIV.IMP_CD\r\n"
			+ "      AND    INP.EXP_CD    = IIV.EXP_CD\r\n"
			+ "      ORDER BY INM.LOT_NO, INV_VIN_NO;", nativeQuery = true)
	List<Tuple> getPartDetailData(@Param("invNumber") String invNumber);
	
	
}
