package com.tpex.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.RddDownLocDtlEntity;

@SuppressWarnings("squid:S2479")
@Repository
public interface InvPackingListRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {

	@Query(value = "SELECT PARA_VAL CD FROM TB_M_PARAMETER WHERE PARA_CD = 'CMP_CD'", nativeQuery = true)
	String getCompanyCode();

	@Query(value = "SELECT TMAPTH_INV_FLG FROM TB_R_INV_INVOICE_H WHERE INV_NO = :invNumber", nativeQuery = true)
	String getTmapthInvFlg(@Param("invNumber") String invNumber);

	@Query(value = "SELECT CASE WHEN INV_DT >= (SELECT str_to_date(PARA_VAL,'%d%m%Y') FROM TB_M_PARAMETER WHERE PARA_CD = 'CMP_NM_CUTOFF') THEN 'TMAPTH_CMP' ELSE 'TMAPTH_CMPO' END COMP_CD FROM  TB_R_INV_INVOICE_H WHERE INV_NO = :invNumber", nativeQuery = true)
	String getCompCode(@Param("invNumber") String invNumber);

	@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 FROM TB_M_CNSG CMP, TB_M_PARAMETER OPR WHERE  CMP.CMP_CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY = OPR.PARA_VAL AND OPR.PARA_CD = :compCode", nativeQuery = true)
	Tuple getInvCompDetailWhenFlgY(@Param("companyCode") String companyCode, @Param("compCode") String compCode);

	@Query(value = "SELECT CMP.NAME, CMP.ADD_1, CMP.ADD_2, CMP.ADD_3, CMP.ADD_4 FROM TB_M_CNSG CMP WHERE CMP.CMP_CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY IN ('OTHER','TMT')", nativeQuery = true)
	Tuple getInvCompDetailWhenFlgN(@Param("companyCode") String companyCode);

	@Query(value = "SELECT TO_CHAR(IND.INV_DT,'DD/MM/YYYY') INV_DT, INP.INP_PART_NO PART_NO, INP.INP_UNIT_PER_BOX UNIT_BOX, INP.INP_NET_WT PART_WT, INP.INP_CF_CD CF_CD, "
			+ "IND.FINAL_DST DST_CD, INP.INP_MOD_NO SHIP_MARK, IND.MARK4 SHIP_MARK4, INP.INP_SERIES SERIES, INP.INP_PART_NM PRT_NAME, SUM(INP.INP_UNIT_PER_BOX) SUM_TOT_UNIT "
			+ "FROM INS_INV_PARTS_DTLS INP, TB_R_INV_INVOICE_H IND "
			+ "WHERE INP.INP_INV_NO  = :invNumber AND INP.INP_INV_NO = IND.INV_NO "
			+ "GROUP BY INP.INP_PART_NO, INP.INP_UNIT_PER_BOX, INP.INP_NET_WT, INP.INP_CF_CD, IND.FINAL_DST, INP.INP_MOD_NO, IND.MARK4, INP.INP_SERIES, INP.INP_PART_NM, TO_CHAR(IND.INV_DT,'DD/MM/YYYY') "
			+ "ORDER BY INP.INP_MOD_NO", nativeQuery = true)
	Object getPartDetailData1(@Param("invNumber") String invNumber);

	@Query(value = "SELECT\r\n"
			+ "            date_format(str_to_date(IND.INV_DT, '%Y-%m-%d'), '%d/%m/%Y')     INV_DT,\r\n"
			+ "            INP.PART_NO                             PART_NO,\r\n"
			+ "            INP.UNIT_PER_BOX                        UNIT_BOX,\r\n"
			+ "            INP.NET_WT                              PART_WT,\r\n"
			+ "            INP.CF_CD                               CF_CD ,\r\n"
			+ "            IND.FINAL_DST                           DST_CD,\r\n"
			+ "            INP.MOD_NO                              SHIP_MARK,\r\n"
			+ "            IND.MARK4                               SHIP_MARK4,\r\n"
			+ "            INP.SERIES                              SERIES,\r\n"
			+ "            INP.PART_NM                             PRT_NAME,\r\n"
			+ "            SUM(INP.UNIT_PER_BOX)                   SUM_TOT_UNIT\r\n" + "   FROM\r\n"
			+ "            TB_R_INV_PART_D          INP,\r\n" + "            TB_R_INV_INVOICE_H                IND\r\n"
			+ "   WHERE\r\n" + "            INP.INV_NO  = :invNumber\r\n" + "   AND      INP.INV_NO  =  IND.INV_NO\r\n"
			+ "   GROUP BY\r\n" + "            INP.PART_NO,\r\n" + "            INP.UNIT_PER_BOX ,\r\n"
			+ "            INP.NET_WT,\r\n" + "            INP.CF_CD,\r\n" + "            IND.FINAL_DST,\r\n"
			+ "            INP.MOD_NO,\r\n" + "            IND.MARK4,\r\n" + "            INP.SERIES,\r\n"
			+ "            INP.PART_NM,\r\n"
			+ "            date_format(str_to_date(IND.INV_DT, '%Y-%m-%d'),'%d/%m/%Y')          \r\n"
			+ "			ORDER BY\r\n" + "            INP.MOD_NO", nativeQuery = true)
	List<Tuple> getPartDetailData(@Param("invNumber") String invNumber); // KR22100530, KR22102669

	@Query(value = "SELECT\r\n" + "                   SUM(INM.GROSS_WT),\r\n" + "                   SUM(INM.TOT_M3)\r\n"
			+ "              FROM\r\n" + "                   TB_R_INV_MODULE_D INM\r\n" + "              WHERE\r\n"
			+ "                   INM.MOD_NO       = :shipMark\r\n"
			+ "              AND  INM.INV_NO       = :invNumber\r\n"
			+ "              AND INM.CF_CD         = :cfCd", nativeQuery = true)
	Tuple getGrossWeightAndMeasurement(@Param("shipMark") String shipMark, @Param("invNumber") String invNumber,
			@Param("cfCd") String cfCd);

	@Query(value = "SELECT\r\n" + "                         COUNT(1)\r\n" + "              FROM\r\n"
			+ "                   TB_R_INV_MODULE_D INM\r\n" + "              WHERE\r\n"
			+ "                   INM.MOD_NO       = :shipMark\r\n"
			+ "              AND  INM.INV_NO       = :invNumber\r\n"
			+ "              AND  INM.CF_CD        = :cfCd", nativeQuery = true)
	Tuple getNoMod(@Param("shipMark") String shipMark, @Param("invNumber") String invNumber,
			@Param("cfCd") String cfCd);

	@Query(value = "SELECT  CONCAT(CF_CD , '-' , MOD_NO) AS CFCD_MOD_NO, sum(GROSS_WT),sum(TOT_M3), COUNT(1) NO_OF_MOD FROM TB_R_INV_MODULE_D WHERE INV_NO = :invNumber "
			+ "group by CF_CD, MOD_NO", nativeQuery = true)
	List<Tuple> getData(@Param("invNumber") String invNumber);

	@Query(value = "SELECT COUNT(1) FROM TB_R_INV_MODULE_D", nativeQuery = true)
	Long getModData();

}
