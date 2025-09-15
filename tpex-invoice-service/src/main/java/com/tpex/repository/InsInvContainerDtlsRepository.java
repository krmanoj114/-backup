package com.tpex.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InsInvContainerDtlsEntity;
import com.tpex.entity.InsInvContainerDtlsIdEntity;

@SuppressWarnings("squid:S3740")
@Repository
public interface InsInvContainerDtlsRepository extends JpaRepository<InsInvContainerDtlsEntity, InsInvContainerDtlsIdEntity>{

	@Query(value=" SELECT " + 
			"      COALESCE(SUM(if(A.CONT_SIZE='20',1,0)),0) AS NO_OF_20FT," +
			"      COALESCE(SUM(if(A.CONT_SIZE='40',1,0)),0) AS NO_OF_40FT," +
			"      COALESCE(AVG(A.CONT_EFF),0) AS TOT_CONT_EFF                 " +
			" FROM (SELECT IICD.CONT_SNO,IICD.CONT_SIZE,IICD.CONT_EFF," +
			"              ROW_NUMBER() OVER ( PARTITION BY CONT_SNO  ORDER BY CONT_SNO) AS SR_NO" +
			"       FROM   TB_R_INV_CONTAINER_D IICD, TB_R_INV_INVOICE_H IND" +
			"       WHERE  IICD.INV_NO     = IND.INV_NO" +
			"       AND    IND.HAISEN_NO    = :haisenNo" +
			"       AND    IND.HAISEN_YR_MTH= :haisenYrMnth" +
			"       AND    IND.CANCEL_FLG   = 'N' ) A" +
			" WHERE  A.SR_NO = 1", nativeQuery = true)
	Object getContainerDetails(@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value= " SELECT COALESCE(SUM(S.MEASUREMENT),0) " +
			" FROM ( " +
			"       SELECT DISTINCT IND.INV_NO, IND.MEASUREMENT " +
			"       FROM   TB_R_INV_CONTAINER_D IICD, TB_R_INV_INVOICE_H IND " +
			"       WHERE  IICD.INV_NO     = IND.INV_NO " +
			"       AND    IND.HAISEN_NO    = :haisenNo " +
			"       AND    IND.HAISEN_YR_MTH= :haisenYrMnth" +
			"       AND    IND.CANCEL_FLG   = 'N' " +
			"       AND    IICD.CONT_TYP = 'L' ) S ",nativeQuery = true)
	Integer getlclVolume(@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT DISTINCT IICD.CONT_DST,DATE_FORMAT(IICD.PLN_VAN_DT, '%Y%m')"
			+ ", IND.SHIPPING_COMP " +
			"FROM   TB_R_INV_INVOICE_H IND, TB_R_INV_CONTAINER_D IICD " +
			" WHERE  IND.INV_NO        = IICD.INV_NO " +
			"AND    IND.INV_NO LIKE :invNoPrefix || '%' " +  
			"AND    IICD.CONT_TYP = 'F' " +
			"AND    IND.HAISEN_NO     = :newHaiSenNo " +
			"AND    IND.HAISEN_YR_MTH = :newHaisenYrMnth " +
			"ORDER BY 1,2 ",nativeQuery = true)
	List<Object> getNewHaisenContainerDtls(@Param("invNoPrefix") String invNoPrefix,@Param("newHaiSenNo") String newHaiSenNo,@Param("newHaisenYrMnth") String newHaisenYrMnth);


	@Query(value="SELECT  " +
			 "DISTINCT IICD.BOOK_NO BOOK_NO," +
			 "IND.HAISEN_NO HAISEN_NO," +
			 "IND.HAISEN_YR_MTH" +
			 " FROM TB_R_INV_CONTAINER_D IICD, TB_R_INV_INVOICE_H IND" +
			 " WHERE IICD.INV_NO = IND.INV_NO" +
			 " AND IND.ETD =STR_TO_DATE(:etd,'%d/%m/%Y')" +
			 " AND IND.ORD_TYP = 'R'" +
			 "AND IND.CANCEL_FLG = 'N'" +
			 "AND (:bookingNum IS  NULL OR IICD.BOOK_NO =:bookingNum)" +
			 "AND (:countryCd IS  NULL OR IICD.CONT_DST =:countryCd)" +
			 "ORDER BY BOOK_NO;",nativeQuery = true)
	List<Tuple> getListOfBookingNum(@Param("etd") String etd,@Param("bookingNum") String bookingNum,@Param("countryCd") String countryCd);

	@Query(value="SELECT GROUP_CONCAT(CASE WHEN ROWNUM_BUYER ='1' THEN BUYER ELSE null END " +
			 "ORDER BY BOOK_NO,HAISEN_NO,HAISEN_YR_MTH " +
			 "SEPARATOR ',') AS BUYER, " +
			 "GROUP_CONCAT(CASE WHEN ROWNUM_FINAL_DST ='1' THEN FINAL_DST ELSE null END " +
			 "ORDER BY BOOK_NO,HAISEN_NO,HAISEN_YR_MTH " +
			 "SEPARATOR ',') AS FINAL_DST, " +
			 "GROUP_CONCAT(CASE WHEN ROWNUM_VAN_PLNT_CD ='1' THEN(SELECT PLANT_NAME " +
			 "FROM TB_M_PLANT " +
			 "WHERE PLANT_CD =  VAN_PLNT_CD)  ELSE null END " +
			 "ORDER BY BOOK_NO,HAISEN_NO,HAISEN_YR_MTH " +
			 "SEPARATOR ',') AS VAN_PLNT_CD," +
			 "REPLACE(DATE_FORMAT(STR_TO_DATE(MAX(  " +
			 "CASE PCK_MTH  WHEN '******' " +
			 "THEN DATE_FORMAT(CONCAT(PLN_PKG_DT,'01'),'%Y%m%d') " +
			 "ELSE CONCAT(PCK_MTH,'01')" +
			 "END),'%Y%m%d'),'%b-%y'),'-','''')  PCK_MTH ," +
			 "DATE_FORMAT(MAX(ETD       ),'%d/%b/%y')  ETD          ," +
			 "DATE_FORMAT(MAX(ETA       ),'%d/%b/%y')  ETA          ," +
			 "DATE_FORMAT(MAX(VANNING_DT),'%d/%b/%y') VANNING_DT," +
			 "SHIPPING_COMP," +
			 "VESSEL_NAME_OCEAN," +
			 "VOYAGE_NO_OCEAN " +
			 "FROM (   SELECT " +
			 "IICD.BOOK_NO," +
			 "IND.HAISEN_NO, " +
			 "IND.HAISEN_YR_MTH, " +
			 "BUYER, " +
			 "FINAL_DST, " +
			 "PCK_MTH ," +
			 "IIMD.PLN_PKG_DT," +
			 "ETD ," +
			 "IND.ETA ETA ," +
			 "VANNING_DT ," +
			 "IND.SHIPPING_COMP ," +
			 "IND.VESSEL_NAME_OCEAN," +
			 "IND.VOYAGE_NO_OCEAN ," +
			 "VAN_PLNT_CD," +
			 "ROW_NUMBER()  OVER (PARTITION BY IICD.BOOK_NO,IND.HAISEN_NO, IND.HAISEN_YR_MTH ,BUYER ORDER BY BUYER )  ROWNUM_BUYER," +
			 "ROW_NUMBER()  OVER (PARTITION BY IICD.BOOK_NO,IND.HAISEN_NO, IND.HAISEN_YR_MTH,FINAL_DST ORDER BY FINAL_DST )  ROWNUM_FINAL_DST," +
			 "ROW_NUMBER()  OVER (PARTITION BY IICD.BOOK_NO,IND.HAISEN_NO, IND.HAISEN_YR_MTH,VAN_PLNT_CD ORDER BY VAN_PLNT_CD )  ROWNUM_VAN_PLNT_CD " +
			 "FROM TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_INVOICE_H IND," +
			 "TB_R_INV_MODULE_D IIMD " +
			 "WHERE IICD.INV_NO = IND.INV_NO " +
			 "AND IND.INV_NO = IIMD.INV_NO " +
			 "AND IICD.CONT_SNO = IIMD.CONT_SNO " +
			 "AND IICD.CONT_DST = IIMD.CONT_DST " +
			 "AND   IND.CANCEL_FLG = 'N' " +
			 "AND IICD.BOOK_NO = :bookingNum " +
			 "AND   IND.HAISEN_NO = :haisenNo " +
			 "AND   IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 ")a " +
			 "GROUP BY " +
			 "SHIPPING_COMP," +
			 "VESSEL_NAME_OCEAN," +
			 "VOYAGE_NO_OCEAN",nativeQuery = true)
	Tuple getDataForBookingNum(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT COUNT(DISTINCT IICD.INV_NO) " +
			 "FROM TB_R_INV_CONTAINER_D IICD,TB_R_INV_INVOICE_H IND " +
			 "WHERE     IICD.INV_NO = IND.INV_NO " +
			 "AND IICD.BOOK_NO = :bookingNum " +
			 "AND IND.HAISEN_NO = :haisenNo " +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth " +
			 "AND IND.CANCEL_FLG = 'N' ;",nativeQuery = true)
	Integer getInvoiceDataCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="select  INV_NO,MOD_NO,INV_AMT,SERIES,PCK_MTH,GROUP_CONCAT(PRIV_NM SEPARATOR '+') from (select DISTINCT " +
			 "IICD.INV_NO," +
			 "IIPD.AICO_FLG," +
			 "DENSE_RANK() OVER (PARTITION BY IICD.INV_NO,IIPD.LOT_NO ORDER BY IIPD.MOD_NO ASC ) +" +
			 "DENSE_RANK() OVER (PARTITION BY IICD.INV_NO,IIPD.LOT_NO ORDER BY IIPD.MOD_NO DESC) - 1 AS MOD_NO," +
			 "IND.INV_AMT ," +
			 "IIPD.SERIES," +
			 "IIPD.PCK_MTH    " +
			 "FROM TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_INVOICE_H IND," +
			 "TB_R_INV_MODULE_D IIMD," +
			 "TB_R_INV_PART_D IIPD " +
			 "WHERE IICD.INV_NO = IND.INV_NO " +
			 "AND IND.INV_NO = IIMD.INV_NO " +
			 "AND IICD.CONT_SNO = IIMD.CONT_SNO " +
			 "AND IICD.CONT_DST = IIMD.CONT_DST " +
			 "AND IICD.BOOK_NO = :bookingNum " +
			 "AND IND.HAISEN_NO = :haisenNo " +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth " +
			 "AND IND.CANCEL_FLG = 'N'" +
			 "AND IIPD.INV_NO = IIMD.INV_NO " +
			 "AND IIPD.INV_DT = IIMD.INV_DT " +
			 "AND IIPD.MOD_NO = IIMD.MOD_NO " +
			 "AND IIPD.LOT_NO = IIMD.LOT_NO ) AS a," +
			 "TB_M_PRIVILEGE_TYPE " +
			 "where " +
			 "a.AICO_FLG = PRIV_CD " +
			 "GROUP BY INV_NO," +
			 "MOD_NO," +
			 "INV_AMT," +
			 "SERIES," +
			 "PCK_MTH", nativeQuery = true)
	List<Tuple> getInvoiceData(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT COUNT(1) R_RACK_FLG " +
			 "FROM TB_R_MTH_VPR_MODULE NVDM," +
			 "TB_R_INV_VPR_MODULE NVIM," +
			 "TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_INVOICE_H IND " +
			 "WHERE  NVIM.INV_NO = IICD.INV_NO " +
			 "AND NVIM.CONT_DST_CD = IICD.CONT_DST " +
			 "AND NVIM.CONT_SNO = IICD.CONT_SNO " +
			 "AND NVDM.CONT_DST_CD = NVIM.CONT_DST_CD " +
			 "AND NVDM.CONT_SNO = NVIM.CONT_SNO " +
			 "AND NVDM.MOD_DST_CD = NVIM.MOD_DST_CD " +
			 "AND NVDM.LOT_MOD_NO = NVIM.LOT_MOD_NO " +
			 "AND NVDM.CASE_NO = NVIM.CASE_NO " +
			 "AND NVDM.VAN_MTH = NVIM.VAN_MTH " +
			 "AND NVDM.RRACK_FLG = 'Y' " +
			 "AND NVIM.INV_NO IS NOT NULL " +
			 "AND IICD.INV_NO = IND.INV_NO " +
			 "AND IICD.BOOK_NO = :bookingNum " +
			 "AND IND.HAISEN_NO = :haisenNo " +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth " +
			 "AND IND.CANCEL_FLG = 'N' ;",nativeQuery = true)
	Integer getRRackDataCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT GROUP_CONCAT(CONCAT(BOX_MAT_CD, ':', sum) separator ',') AS BOX_MAT_DATA " +
			 "from (" +
			 "SELECT BOX_MAT_CD,SUM(BOX_COUNT) AS sum " +
			 "FROM (" +
			 "SELECT COALESCE(NVIP.BOX_MAT_CD, :val ) AS BOX_MAT_CD, COUNT(DISTINCT COALESCE(NVIP.BOX_NO, NVIP.BOX_SEQ_NO)) AS BOX_COUNT " +
			 "FROM TB_R_INV_VPR_MODULE NVIM, TB_R_INV_VPR_PART NVIP, TB_R_INV_CONTAINER_D IICD,TB_R_INV_INVOICE_H IND  " +
			 "WHERE NVIM.INV_NO = IICD.INV_NO " +
			 "AND NVIM.CONT_DST_CD = IICD.CONT_DST " +
			 "AND NVIM.CONT_SNO = IICD.CONT_SNO " +
			 "AND NVIM.CONT_DST_CD = NVIP.CONT_DST_CD " +
			 "AND NVIM.CONT_SNO = NVIP.CONT_SNO " +
			 "AND NVIM.MOD_DST_CD = NVIP.MOD_DST_CD " +
			 "AND NVIM.LOT_MOD_NO = NVIP.LOT_MOD_NO " +
			 "AND NVIM.CASE_NO = NVIP.CASE_NO " +
			 "AND NVIM.VAN_MTH = NVIP.VAN_MTH " +
			 "AND IICD.INV_NO = IND.INV_NO " +
			 "AND IICD.BOOK_NO = :bookingNum " +
			 "AND IND.HAISEN_NO = :haisenNo " +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth " +
			 "AND IND.CANCEL_FLG = 'N' " +
			 "GROUP BY IICD.BOOK_NO, NVIM.CONT_SNO, NVIM.LOT_MOD_NO, NVIM.CASE_NO, COALESCE(NVIP.BOX_MAT_CD,:val) " +
			 " ) AS a Group by BOX_MAT_CD) b GROUP BY BOX_MAT_CD;", nativeQuery = true)
	List<String> getRBoxDataCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth
			,@Param("val") String val);

	@Query(value="SELECT SUM(COUNT_40), SUM(COUNT_20) " +
			 "FROM ( SELECT COUNT(CASE " +
			 "WHEN CONT_SIZE = '40' THEN 1 " +
			 "ELSE NULL " +
			 "END)" +
			 					"COUNT_40," +
			 					"COUNT(CASE " +
			 							"WHEN CONT_SIZE = '20' THEN 1 " +
			 							"ELSE NULL " +
			 							"END) " +
			 					"COUNT_20 " +
			 					"FROM (" +
			 							"(SELECT DISTINCT IICD.CONT_SNO,IICD.CONT_SIZE " +
			 									"FROM TB_R_INV_INVOICE_H IND," +
			 									"TB_R_INV_CONTAINER_D IICD," +
			 									"TB_R_INV_MODULE_D IIMD " +
			 									"WHERE IND.INV_NO = IICD.INV_NO " +
			 									"AND IND.INV_NO = IIMD.INV_NO " +
			 									"AND IND.INV_DT = IICD.INV_DT " +
			 									"AND IND.ORD_TYP = 'R' " +
			 									"AND IIMD.DG_FLG = '0' " +
			 									"AND IICD.CONT_SNO = IIMD.CONT_SNO " +
			 									"AND IICD.CONT_DST = IIMD.CONT_DST " +
			 									"AND IICD.INV_NO = IIMD.INV_NO " +
			 									"AND IICD.CONT_SIZE IN ('20', '40') " +
			 									"AND IICD.INV_NO = IND.INV_NO " +
			 									"AND IICD.BOOK_NO  = :bookingNum " +
			 									"AND IND.HAISEN_NO = :haisenNo " +
			 									"AND IND.HAISEN_YR_MTH = :haisenYrMnth " +
			 									"AND IND.CANCEL_FLG = 'N' " +
			 									"AND (IICD.CONT_SNO,IICD.CONT_SIZE) NOT IN " +
			 									"(SELECT DISTINCT IICD.CONT_SNO,IICD.CONT_SIZE " +
			 											"FROM TB_R_INV_INVOICE_H IND," +
			 											"TB_R_INV_CONTAINER_D IICD," +
			 											"TB_R_INV_MODULE_D IIMD " +
			 											"WHERE IND.INV_NO = IICD.INV_NO " +
			 											"AND IND.INV_NO = IIMD.INV_NO " +
			 											"AND IND.INV_DT = IICD.INV_DT " +
			 											"AND IND.ORD_TYP = 'R' " +
			 											"AND IIMD.DG_FLG = '1' " +
			 											"AND IICD.CONT_SNO = IIMD.CONT_SNO " +
			 											"AND IICD.CONT_DST = IIMD.CONT_DST " +
			 											"AND IICD.INV_NO = IIMD.INV_NO " +
			 											"AND IICD.CONT_SIZE IN ('20', '40') " +
			 											"AND IICD.INV_NO = IND.INV_NO " +
			 											"AND IICD.BOOK_NO  = :bookingNum " +
			 										    "AND IND.HAISEN_NO = :haisenNo " +
			 											"AND IND.HAISEN_YR_MTH = :haisenYrMnth " +
			 											"AND IND.CANCEL_FLG = 'N') " +
			 									")" +
			 							")a GROUP BY CONT_SIZE )b", nativeQuery = true)
	Tuple getNonDgCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT SUM(COUNT_40), SUM(COUNT_20) " +
			 "FROM (SELECT COUNT(CASE " +
			 "WHEN CONT_SIZE = '40' THEN 1 " +
			 "ELSE NULL " +
			 "END)" +
			 "COUNT_40," +
			 "COUNT(CASE " +
			 "WHEN CONT_SIZE = '20' THEN 1 " +
			 "ELSE NULL " +
			 "END) " +
			 "COUNT_20 " +
			 "FROM " +
			 "(SELECT DISTINCT IICD.CONT_SNO, IICD.CONT_SIZE " +
			 "FROM TB_R_INV_INVOICE_H IND," +
			 "TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_MODULE_D IIMD " +
			 "WHERE IND.INV_NO = IICD.INV_NO " +
			 "AND IND.INV_NO = IIMD.INV_NO " +
			 "AND IND.INV_DT = IICD.INV_DT " +
			 "AND IND.ORD_TYP = 'R' " +
			 "AND IIMD.DG_FLG = '1' " +
			 "AND IICD.CONT_SNO = IIMD.CONT_SNO " +
			 "AND IICD.CONT_DST = IIMD.CONT_DST " +
			 "AND IICD.INV_NO = IIMD.INV_NO " +
			 "AND CONT_SIZE IN ('20', '40') " +
			 "AND IICD.BOOK_NO  = :bookingNum " +
			 "AND IND.HAISEN_NO = :haisenNo " +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth " +
			 "AND IND.CANCEL_FLG = 'N' " +
			 ") a " +
			 "GROUP BY CONT_SIZE ) b", nativeQuery = true)
	Tuple getDgCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT GROUP_CONCAT(SERIES " +
			 "ORDER BY SERIES " +
			 "SEPARATOR ' / ') " +
			 "SERIES " +
			 "FROM (" +
			 "SELECT DISTINCT IIPD.SERIES " +
			 "FROM TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_INVOICE_H IND," +
			 "TB_R_INV_MODULE_D IIMD," +
			 "TB_R_INV_PART_D IIPD " +
			 "WHERE IICD.INV_NO = IND.INV_NO " +
			 "AND IND.INV_NO = IIMD.INV_NO " +
			 "AND IICD.CONT_SNO = IIMD.CONT_SNO " +
			 "AND IICD.CONT_DST = IIMD.CONT_DST " +
			 "AND IIPD.INV_NO = IIMD.INV_NO " +
			 "AND IIPD.INV_DT = IIMD.INV_DT " +
			 "AND IIPD.MOD_NO = IIMD.MOD_NO " +
			 "AND IIPD.LOT_NO = IIMD.LOT_NO " +
			 "AND IICD.BOOK_NO = :bookingNum " +
			 "AND IND.HAISEN_NO = :haisenNo " +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth " +
			 "AND IND.CANCEL_FLG = 'N'   ) a", nativeQuery = true)
	Object getDataForSeries(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);
	
	@Query(value="SELECT\n"
            + "        t1.IMP_CD  IMP_CD,\n"
            + "        t1.EXP_CD  EXP_CD,\n"
            + "        t1.MSP_ORD_TYP MSP_ORD_TYP,\n"
            + "        t1.PCK_MTH  PCK_MTH,\n"
            + "        t1.CF_CD    CF_CD,\n"
            + "        t1.REXP_CD  REXP_CD,\n"
            + "        t1.INV_TYP  INV_TYP,\n"
            + "        t1.CPO_SPO_NO   CPO_NO,\n"
            + "        t1.IICD_VAN_MTH VAN_MTH,\n"
            + "        t1.VAN_PLNT_CD VAN_PLNT,\n"
            + "        t1.IICD_VAN_DT VAN_DT,\n"
            + "        t1.IND_ETD ETD,\n"
            + "        t1.IND_ETA ETA,\n"
            + "        t1.HAISEN_NO HAISEN_NO,\n"
            + "        t1.INV_NO INV_NO,\n"
            + "        t1.ind_INV_DT INV_DT,\n"
            + "        t1.INV_AMT AMT_FOB_INV,\n"
            + "        CASE \n"
            + "            WHEN ORD_TYP = 'R' THEN 'Regular'                         \n"
            + "            WHEN ORD_TYP = 'C' THEN 'CPO'                         \n"
            + "            WHEN ORD_TYP = 'S' THEN 'SPO' \n"
            + "        END ORD_TYP,\n"
            + "        CASE \n"
            + "            WHEN INV_CAT = 'L' THEN 'Lot' \n"
            + "            Else 'PxP' \n"
            + "        END PKG_PATTERN,\n"
            + "        t1.CONT_SNO RANBEN_NO,\n"
            + "        t1.CONT_SIZE CONT_SIZE,\n"
            + "        t1.ISO_CONT_NO ISO_CONT_NO,\n"
            + "        t1.SEAL_NO SEAL_NO,\n"
            + "        COALESCE(NO_OF_MOD ,\n"
            + "        CASE \n"
            + "            WHEN CANCEL_FLG = 'Y' THEN '' \n"
            + "            ELSE 0 \n"
            + "        END) QTY_MOD,\n"
            + "        COALESCE(NO_OF_MOD_RRACK,\n"
            + "        CASE \n"
            + "            WHEN CANCEL_FLG = 'Y' THEN '' \n"
            + "            ELSE 0 \n"
            + "        END) QTY_MOD_ROUND_USE,\n"
            + "        t1.PART_BOX_MAT   ,\n"
            + "        COALESCE(SUBSTRING(MOD_INFO,\n"
            + "        1,\n"
            + "        LOCATE('#',\n"
            + "        MOD_INFO)-1),\n"
            + "        CASE \n"
            + "            WHEN CANCEL_FLG = 'Y' THEN '' \n"
            + "            ELSE 0 \n"
            + "        END) NET_WT,\n"
            + "        COALESCE(SUBSTRING(MOD_INFO,\n"
            + "        LOCATE('#',\n"
            + "        MOD_INFO,\n"
            + "        1)+1,\n"
            + "        LOCATE('#',\n"
            + "        MOD_INFO,\n"
            + "        2)- LOCATE( '#',\n"
            + "        MOD_INFO,\n"
            + "        1)-1),\n"
            + "        CASE \n"
            + "            WHEN CANCEL_FLG = 'Y' THEN  '' \n"
            + "            ELSE 0 \n"
            + "        END) GROSS_WT,\n"
            + "        COALESCE(SUBSTRING(MOD_INFO,\n"
            + "        LOCATE( '#',\n"
            + "        MOD_INFO,\n"
            + "        10)+1,\n"
            + "        4)) M3,\n"
            + "        COALESCE(IICD_CONT_FOB,\n"
            + "        CASE \n"
            + "            WHEN CANCEL_FLG = 'Y' THEN '' \n"
            + "            ELSE 0 \n"
            + "        END) CONT_FOB,\n"
            + "        CASE \n"
            + "            WHEN CANCEL_FLG = 'Y' THEN 'Cancelled' \n"
            + "            ELSE '' \n"
            + "        END CANCEL_FLG ,\n"
            + "        t1.DG_CONTAINER,\n"
            + "        t1.BOOK_NO                 \n"
            + "    FROM\n"
            + "        (    SELECT\n"
            + "            t.IMP_CD,\n"
            + "            t.EXP_CD,\n"
            + "            t.MSP_ORD_TYP,\n"
            + "            t.PCK_MTH,\n"
            + "            t.CF_CD,\n"
            + "            t.REXP_CD,\n"
            + "            t.INV_TYP,\n"
            + "            t.CPO_SPO_NO,\n"
            + "            t.IICD_VAN_MTH,\n"
            + "            t.VAN_PLNT_CD,\n"
            + "            t.IICD_VAN_DT,\n"
            + "            t.IND_ETD,\n"
            + "            t.IND_ETA,\n"
            + "            t.HAISEN_NO,\n"
            + "            t.INV_NO,\n"
            + "            t.ind_INV_DT,\n"
            + "            t.INV_AMT,\n"
            + "            t.ORD_TYP,\n"
            + "            t.INV_CAT,\n"
            + "            t.CONT_SNO,\n"
            + "            t.CONT_SIZE,\n"
            + "            t.ISO_CONT_NO,\n"
            + "            t.SEAL_NO,\n"
            + "            t.NO_OF_MOD,\n"
            + "            t.NO_OF_MOD_RRACK,\n"
            + "            t.PART_BOX_MAT,\n"
            + "            t.MOD_INFO,\n"
            + "            t.IICD_CONT_FOB,\n"
            + "            t.CANCEL_FLG ,\n"
            + "            t.DG_CONTAINER,\n"
            + "            t.BOOK_NO                    \n"
            + "        FROM\n"
            + "            (         SELECT\n"
            + "                DISTINCT                            INP.IMP_CD,\n"
            + "                INP.EXP_CD,\n"
            + "                INM.MSP_ORD_TYP,\n"
            + "                INP.PCK_MTH,\n"
            + "                INM.CF_CD,\n"
            + "                INM.REXP_CD,\n"
            + "                IND.INV_TYP,\n"
            + "                CASE \n"
            + "                    WHEN IND.ORD_TYP = 'R'THEN NULL \n"
            + "                    ELSE SUBSTRING(INP.ORD_NO,\n"
            + "                    -6) \n"
            + "                END CPO_SPO_NO,\n"
            + "                DATE_FORMAT(iFNULL(IICD.ACT_VAN_DT,\n"
            + "                IICD.PLN_VAN_DT),\n"
            + "                '%Y-%m') IICD_VAN_MTH,\n"
            + "                IICD.VAN_PLNT_CD,\n"
            + "                DATE_FORMAT(COALESCE(IICD.PLN_VAN_DT,\n"
            + "                IICD.ACT_VAN_DT),\n"
            + "                '%d-%m-%Y') IICD_VAN_DT,\n"
            + "                DATE_FORMAT(IND.ETD,\n"
            + "                '%d-%m-%Y') IND_ETD,\n"
            + "                DATE_FORMAT(IND.ETA,\n"
            + "                '%d-%m-%Y') IND_ETA,\n"
            + "                IND.HAISEN_NO,\n"
            + "                IND.INV_NO,\n"
            + "                DATE_FORMAT(IND.INV_DT,\n"
            + "                '%d-%m-%Y') IND_INV_DT,\n"
            + "                IND.INV_AMT,\n"
            + "                IND.ORD_TYP,\n"
            + "                IND.INV_CAT,\n"
            + "                IICD.CONT_SNO,\n"
            + "                IICD.CONT_SIZE,\n"
            + "                IICD.ISO_CONT_NO,\n"
            + "                IICD.SEAL_NO,\n"
            + "                IICD.NO_OF_MOD,\n"
            + "                IICD.NO_OF_MOD_RRACK,\n"
            + "                CASE                                          \n"
            + "                    WHEN IND.ORD_TYP = 'R'                                          THEN                      (                        SELECT\n"
            + "                        GROUP_CONCAT(CONCAT( T1.BOX_MAT_CD,\n"
            + "                        ':' ,\n"
            + "                        t1.box_count ) \n"
            + "                    ORDER BY\n"
            + "                        T1.BOX_MAT_CD SEPARATOR ',' )PART_BOX_MAT   \n"
            + "                    FROM\n"
            + "                        (  select\n"
            + "                            t.inv_no,\n"
            + "                            t.cont_dst_cd,\n"
            + "                            t.cont_sno,\n"
            + "                            t.box_mat_cd,\n"
            + "                            sum(t.box_count)box_count  \n"
            + "                        from\n"
            + "                            (SELECT\n"
            + "                                DISTINCT NVIM.INV_NO,\n"
            + "                                NVIM.CONT_DST_CD,\n"
            + "                                NVIM.CONT_SNO,\n"
            + "                                NVIM.LOT_MOD_NO,\n"
            + "                                NVIM.CASE_NO,\n"
            + "                                IFNULL(NVIP.BOX_MAT_CD,\n"
            + "                                FN_GET_DEFAULT_PART_MAT_CD()) BOX_MAT_CD,\n"
            + "                                COUNT(IFNULL(NVIP.BOX_NO,\n"
            + "                                NVIP.BOX_SEQ_NO))                                   OVER ( PARTITION \n"
            + "                            BY\n"
            + "                                NVIM.CONT_SNO,\n"
            + "                                NVIM.LOT_MOD_NO,\n"
            + "                                NVIM.CASE_NO,\n"
            + "                                NVIM.INV_NO,\n"
            + "                                IFNULL(NVIP.BOX_MAT_CD,\n"
            + "                                FN_GET_DEFAULT_PART_MAT_CD()) )                                   BOX_COUNT                                          \n"
            + "                            FROM\n"
            + "                                TB_R_INV_VPR_MODULE NVIM,\n"
            + "                                TB_R_INV_VPR_PART NVIP                          \n"
            + "                            WHERE\n"
            + "                                NVIM.CONT_DST_CD = NVIP.CONT_DST_CD                                                       \n"
            + "                                AND NVIM.CONT_SNO = NVIP.CONT_SNO                                                       \n"
            + "                                AND NVIM.MOD_DST_CD = NVIP.MOD_DST_CD                                                       \n"
            + "                                AND NVIM.LOT_MOD_NO = NVIP.LOT_MOD_NO                                                       \n"
            + "                                AND NVIM.CASE_NO = NVIP.CASE_NO                      \n"
            + "                                AND NVIM.VAN_MTH = NVIP.VAN_MTH )t                               \n"
            + "                        group by\n"
            + "                            t.inv_no,\n"
            + "                            t.cont_dst_cd,\n"
            + "                            t.cont_sno,\n"
            + "                            t.box_mat_cd                               )t1                   \n"
            + "                        WHERE\n"
            + "                            t1.INV_NO = INM.INV_NO                                                       \n"
            + "                            AND t1.CONT_DST_CD = INM.CONT_DST                                                       \n"
            + "                            AND t1.CONT_SNO = INM.CONT_SNO                      \n"
            + "                        GROUP BY\n"
            + "                            t1.inv_no                                                                                   )                                         \n"
            + "                        WHEN IND.ORD_TYP IN ('C',\n"
            + "                        'S')                                          THEN                       (                               select\n"
            + "                            GROUP_CONCAT( CONCAT( T6.BOX_MAT_CD,\n"
            + "                            ':' ,\n"
            + "                            T6.box_count) \n"
            + "                        ORDER BY\n"
            + "                            T6.BOX_MAT_CD SEPARATOR ',' )                         PART_BOX_MAT                           \n"
            + "                        from\n"
            + "                            ( select\n"
            + "                                t5.ACT_CONT_SNO,\n"
            + "                                t5.inv_no,\n"
            + "                                t5.cont_dst_cd,\n"
            + "                                t5.box_mat_cd,\n"
            + "                                t5.box_count                           \n"
            + "                            FROM\n"
            + "                                (select\n"
            + "                                    t4.ACT_CONT_SNO,\n"
            + "                                    t4.inv_no,\n"
            + "                                    t4.cont_dst_cd,\n"
            + "                                    t4.box_mat_cd,\n"
            + "                                    sum(t4.box_count)box_count                \n"
            + "                                FROM\n"
            + "                                    (                SELECT\n"
            + "                                        DISTINCT NDMD.INV_NO  INV_NO ,\n"
            + "                                        NDCD.CONT_DST_CD  CONT_DST_CD,\n"
            + "                                        NDCD.ACT_CONT_SNO ACT_CONT_SNO,\n"
            + "                                        NDPD.TMP_LOT_MOD_NO,\n"
            + "                                        NDPD.LOT_CD,\n"
            + "                                        ifnull(NDPD.BOX_TYPE,\n"
            + "                                        FN_GET_DEFAULT_PART_MAT_CD()) BOX_MAT_CD,\n"
            + "                                        COUNT( ifnull(BOX_ID,\n"
            + "                                        BOX_SEQ_NO) )                                 OVER ( PARTITION \n"
            + "                                    BY\n"
            + "                                        NDMD.TMP_CONT_SNO,\n"
            + "                                        NDPD.TMP_LOT_MOD_NO,\n"
            + "                                        NDPD.LOT_CD,\n"
            + "                                        ifnull(NDPD.BOX_TYPE,\n"
            + "                                        FN_GET_DEFAULT_PART_MAT_CD()),\n"
            + "                                        NDMD.INV_NO )                                        BOX_COUNT                \n"
            + "                                    FROM\n"
            + "                                        TB_R_DLY_MODULE_D NDMD ,\n"
            + "                                        tb_r_dly_part_d NDPD,\n"
            + "                                        TB_R_DLY_CONTAINER_D NDCD                 \n"
            + "                                    WHERE\n"
            + "                                        NDMD.DSI_TI_NO = NDPD.DSI_TI_NO                                                               \n"
            + "                                        AND NDMD.ORDER_NO = NDPD.ORDER_NO                                                               \n"
            + "                                        AND NDMD.LOT_CD = NDPD.LOT_CD                                                               \n"
            + "                                        AND NDMD.LOT_NO = NDPD.LOT_NO                                                               \n"
            + "                                        AND NDMD.MOD_CS_SFX = NDPD.MOD_CS_SFX                                \n"
            + "                                        AND NDMD.TMP_LOT_MOD_NO = NDPD.TMP_LOT_MOD_NO                               \n"
            + "                                        AND NDMD.TMP_CONT_SNO =  NDCD.TMP_CONT_SNO                                \n"
            + "                                        AND NDMD.CONT_VAN_MTH =  NDCD.CONT_VAN_MTH                                \n"
            + "                                        AND NDMD.CONT_DST_CD = NDCD.CONT_DST_CD ) T4                                             \n"
            + "                                group by\n"
            + "                                    t4.ACT_CONT_SNO,\n"
            + "                                    t4.inv_no,\n"
            + "                                    t4.cont_dst_cd,\n"
            + "                                    t4.box_mat_cd ) T5                           \n"
            + "                                WHERE\n"
            + "                                    INV_NO = INM.INV_NO                                                                                  \n"
            + "                                    AND                                  T5.ACT_CONT_SNO IN                                                        (SELECT\n"
            + "                                        DISTINCT NDCD.ACT_CONT_SNO                        \n"
            + "                                    FROM\n"
            + "                                        tb_r_dly_container_d NDCD,\n"
            + "                                        tb_r_dly_module_d NDMD,\n"
            + "                                        tb_r_inv_module_d INMD                                           \n"
            + "                                    WHERE\n"
            + "                                        NDCD.CONT_DST_CD   = NDMD.CONT_DST_CD                                                 \n"
            + "                                        AND     NDCD.CONT_VAN_MTH = NDMD.CONT_VAN_MTH                                                 \n"
            + "                                        AND     NDCD.CONT_DST_CD = NDMD.CONT_DST_CD                                                      \n"
            + "                                        AND  INMD.MOD_NO = CASE \n"
            + "                                            WHEN LOT_CD = '**' THEN ACT_LOT_MOD_NO \n"
            + "                                            ELSE MOD_CS_SFX \n"
            + "                                        END                                                 \n"
            + "                                        AND  INMD.LOT_NO = CASE \n"
            + "                                            WHEN LOT_CD = '**' THEN '***'  \n"
            + "                                            ELSE INMD.LOT_NO \n"
            + "                                        END                           \n"
            + "                                        AND  INMD.INV_NO = NDMD.INV_NO                           \n"
            + "                                        AND  NDMD.INV_NO = INM.INV_NO                                                          \n"
            + "                                        AND  INMD.CONT_SNO = INM.CONT_SNO                                    \n"
            + "                                        AND  CONT_DST = INM.CONT_DST                              ) )t6                                     \n"
            + "                                GROUP BY\n"
            + "                                    BOX_MAT_CD       )                                 \n"
            + "                                ELSE ''                                 \n"
            + "                            END          PART_BOX_MAT,\n"
            + "                            (SELECT\n"
            + "                                CONCAT (SUM(NET_WT) ,\n"
            + "                                '#' ,\n"
            + "                                SUM(GROSS_WT) ,\n"
            + "                                '#' ,\n"
            + "                                SUM(TOT_M3))                            \n"
            + "                            FROM\n"
            + "                                TB_R_INV_MODULE_D B                            \n"
            + "                            WHERE\n"
            + "                                INM.INV_NO = B.INV_NO                            \n"
            + "                                AND   INM.CONT_DST =B.CONT_DST                            \n"
            + "                                AND   INM.CONT_SNO = B.CONT_SNO) MOD_INFO,\n"
            + "                            SUM(INP.UNIT_PER_BOX * INP.PRC) OVER(PARTITION \n"
            + "                        BY\n"
            + "                            IND.INV_NO,\n"
            + "                            INM.CONT_DST,\n"
            + "                            INM.CONT_SNO) IICD_CONT_FOB ,\n"
            + "                            IND.CANCEL_FLG CANCEL_FLG,\n"
            + "                            CASE \n"
            + "                                WHEN  INP.DG_FLG = 1 THEN 'Y' \n"
            + "                                ELSE 'N'  \n"
            + "                            END DG_CONTAINER,\n"
            + "                            BOOK_NO                        \n"
            + "                        FROM\n"
            + "                            TB_R_INV_INVOICE_H IND,\n"
            + "                            TB_R_INV_PART_D INP,\n"
            + "                            TB_R_INV_MODULE_D INM \n"
            + "                        left outer join\n"
            + "                            TB_R_INV_CONTAINER_D IICD \n"
            + "                                on                            INM.INV_NO   = IICD.INV_NO                         \n"
            + "                                AND  INM.CONT_DST = IICD.CONT_DST                         \n"
            + "                                AND  INM.CONT_SNO = IICD.CONT_SNO                          \n"
            + "                        WHERE\n"
            + "                            IND.INV_NO   = INM.INV_NO         \n"
            + "                            AND  INM.INV_NO = INP.INV_NO                             \n"
            + "                            AND INM.MOD_NO  = INP.MOD_NO                        \n"
            + "                            AND INM.LOT_NO  = INP.LOT_NO      \n"
            + "                                                \n"
            + "                            AND IND.ETD =:etd                  \n"
            + "                            AND  INP.EXP_CD IN (:destination)                    \n"
            + "                        )t                    \n"
            + "                    GROUP BY\n"
            + "                        t.IMP_CD,\n"
            + "                        t.EXP_CD,\n"
            + "                        t.MSP_ORD_TYP,\n"
            + "                        t.IICD_VAN_MTH,\n"
            + "                        t.VAN_PLNT_CD,\n"
            + "                        t.IICD_VAN_DT,\n"
            + "                        t.IND_ETD,\n"
            + "                        t.IND_ETA,\n"
            + "                        t.HAISEN_NO,\n"
            + "                        t.INV_NO,\n"
            + "                        t.IND_INV_DT,\n"
            + "                        t.INV_AMT,\n"
            + "                        t.ORD_TYP,\n"
            + "                        t.INV_CAT,\n"
            + "                        t.CONT_SNO,\n"
            + "                        t.CONT_SIZE,\n"
            + "                        t.ISO_CONT_NO,\n"
            + "                        t.SEAL_NO,\n"
            + "                        t.NO_OF_MOD,\n"
            + "                        t.NO_OF_MOD_RRACK,\n"
            + "                        t.PART_BOX_MAT,\n"
            + "                        t.MOD_INFO,\n"
            + "                        t.PCK_MTH,\n"
            + "                        t.CF_CD,\n"
            + "                        t.REXP_CD,\n"
            + "                        t.INV_TYP,\n"
            + "                        t.CPO_SPO_NO,\n"
            + "                        t.IICD_CONT_FOB ,\n"
            + "                        t.SEAL_NO,\n"
            + "                        t.CANCEL_FLG,\n"
            + "                        t.DG_CONTAINER,\n"
            + "                        t.BOOK_NO                 )t1                  \n"
            + "                    ORDER BY\n"
            + "                        INV_NO,\n"
            + "                        ISO_CONT_NO", nativeQuery = true)
    List<String[]> getDataForContinerList(@Param("etd") String etdformat,@Param("destination") List<String> destinationcode);
    
    @Query(value="SELECT CASE  WHEN IND.ORD_TYP = 'R' THEN (SELECT CONCAT(IFNULL(INP.IMP_CD,''),'-' ,IFNULL(IICD.BOOK_NO,'')) \n"
    		+ " FROM TB_R_INV_INVOICE_H IND, \n"
    		+ " TB_R_INV_PART_D INP,\n"
    		+ " TB_R_INV_MODULE_D INM left outer join\n"
    		+ " TB_R_INV_CONTAINER_D IICD on\n"
    		+ " INM.INV_NO   = IICD.INV_NO \n"
    		+ " AND INM.CONT_DST = IICD.CONT_DST \n"
    		+ " AND INM.CONT_SNO = IICD.CONT_SNO \n"
    		+ " WHERE IND.INV_NO = INM.INV_NO \n"
    		+ " AND INM.INV_NO = INP.INV_NO \n"
    		+ " AND INM.MOD_NO  = INP.MOD_NO \n"
    		+ " AND INM.LOT_NO  = INP.LOT_NO \n"    		
    		+ " LIMIT 1)\n"
    		+ " WHEN IND.ORD_TYP IN ('C', 'S') THEN \n"
    		+ " (select concat(IFNULL(INP.IMP_CD,''),'-' ,IFNULL(DATE_FORMAT(IND.ETD,'%d%m%Y'),'')) FROM\n"
    		+ " TB_R_INV_INVOICE_H IND,\n"
    		+ " TB_R_INV_PART_D INP,\n"
    		+ " TB_R_INV_MODULE_D INM left outer join\n"
    		+ " TB_R_INV_CONTAINER_D IICD on\n"
    		+ " INM.INV_NO   = IICD.INV_NO \n"
    		+ " AND  INM.CONT_DST = IICD.CONT_DST \n"
    		+ " AND  INM.CONT_SNO = IICD.CONT_SNO  \n"
    		+ " WHERE \n"
    		+ " IND.INV_NO = INM.INV_NO \n"
    		+ " AND INM.INV_NO = INP.INV_NO \n"
    		+ " AND INM.MOD_NO  = INP.MOD_NO \n"
    		+ " AND INM.LOT_NO  = INP.LOT_NO \n"
    		+ " LIMIT 1 )\n"
    		+ " ELSE '' END ReportName         \n"
    		+ " FROM \n"
    		+ " TB_R_INV_INVOICE_H IND,\n"
    		+ " TB_R_INV_PART_D INP,\n"
    		+ " TB_R_INV_MODULE_D INM left outer join\n"
    		+ " TB_R_INV_CONTAINER_D IICD on\n"
    		+ " INM.INV_NO = IICD.INV_NO \n"
    		+ " AND  INM.CONT_DST = IICD.CONT_DST \n"
    		+ " AND  INM.CONT_SNO = IICD.CONT_SNO  \n"
    		+ " WHERE \n"
    		+ " IND.INV_NO   = INM.INV_NO \n"
    		+ " AND INM.INV_NO = INP.INV_NO \n"
    		+ " AND INM.MOD_NO  = INP.MOD_NO\n"
    		+ " AND INM.LOT_NO  = INP.LOT_NO\n"
    		+ " AND  IND.ETD = :etd \n"
    		+ " AND INP.EXP_CD = (:destination) LIMIT 1" , nativeQuery = true)
	String getReportNameDtls(@Param("etd") String etdFormatted,@Param("destination") List<String> listDestination);

}
