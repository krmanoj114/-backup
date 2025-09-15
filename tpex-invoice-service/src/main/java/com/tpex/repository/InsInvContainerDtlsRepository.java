package com.tpex.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InsInvContainerDtlsEntity;
import com.tpex.entity.InsInvContainerDtlsIdEntity;


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
			"WHERE  IND.INV_NO        = IICD.INV_NO " +
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
			 "FROM TB_R_INV_CONTAINER_D IICD, TB_R_INV_INVOICE_H IND" +
			 "WHERE IICD.INV_NO = IND.INV_NO" +
			 "AND (ETD = :etd)" +
			 "AND IND.ORD_TYP = 'R'" +
			 "AND IND.CANCEL_FLG = 'N'" +
			 "AND (:bookingNum IS  NULL OR IICD.BOOK_NO =:bookingNum)" +
			 "AND (:countryCd IS  NULL OR IICD.CONT_DST =:countryCd)" +
			 "ORDER BY BOOK_NO;",nativeQuery = true)
	List<Tuple> getListOfBookingNum(@Param("etd") String etd,@Param("bookingNum") String bookingNum,@Param("countryCd") String countryCd);

	@Query(value="SELECT GROUP_CONCAT(CASE WHEN ROWNUM_BUYER ='1' THEN BUYER ELSE null END " +
			 "ORDER BY BOOK_NO,HAISEN_NO,HAISEN_YR_MTH" +
			 "SEPARATOR ',') AS BUYER," +
			 "GROUP_CONCAT(CASE WHEN ROWNUM_FINAL_DST ='1' THEN FINAL_DST ELSE null END " +
			 "ORDER BY BOOK_NO,HAISEN_NO,HAISEN_YR_MTH" +
			 "SEPARATOR ',') AS FINAL_DST," +
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
			 "VOYAGE_NO_OCEAN" +
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
			 "ROW_NUMBER()  OVER (PARTITION BY IICD.BOOK_NO,IND.HAISEN_NO, IND.HAISEN_YR_MTH,VAN_PLNT_CD ORDER BY VAN_PLNT_CD )  ROWNUM_VAN_PLNT_CD" +
			 "FROM TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_INVOICE_H IND," +
			 "TB_R_INV_MODULE_D IIMD" +
			 "WHERE IICD.INV_NO = IND.INV_NO" +
			 "AND IND.INV_NO = IIMD.INV_NO" +
			 "AND IICD.CONT_SNO = IIMD.CONT_SNO" +
			 "AND IICD.CONT_DST = IIMD.CONT_DST" +
			 "AND   IND.CANCEL_FLG = 'N'" +
			 "AND IICD.BOOK_NO = :bookingNum" +
			 "AND   IND.HAISEN_NO = :haisenNo" +
			 "AND   IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 ")a" +
			 "GROUP BY " +
			 "SHIPPING_COMP," +
			 "VESSEL_NAME_OCEAN," +
			 "VOYAGE_NO_OCEAN",nativeQuery = true)
	Tuple getDataForBookingNum(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT COUNT(DISTINCT IICD.INV_NO)" +
			 "FROM TB_R_INV_CONTAINER_D IICD,TB_R_INV_INVOICE_H IND" +
			 "WHERE     IICD.INV_NO = IND.INV_NO" +
			 "AND IICD.BOOK_NO = :bookingNum" +
			 "AND IND.HAISEN_NO = :haisenNo" +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 "AND IND.CANCEL_FLG = 'N' ;",nativeQuery = true)
	Integer getInvoiceDataCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="select  INV_NO,MOD_NO,INV_AMT,SERIES,PCK_MTH,GROUP_CONCAT(PRIV_NM SEPARATOR '+') from (select DISTINCT" +
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
			 "TB_R_INV_PART_D IIPD" +
			 "WHERE IICD.INV_NO = IND.INV_NO" +
			 "AND IND.INV_NO = IIMD.INV_NO" +
			 "AND IICD.CONT_SNO = IIMD.CONT_SNO" +
			 "AND IICD.CONT_DST = IIMD.CONT_DST" +
			 "AND IICD.BOOK_NO = :bookingNum " +
			 "AND IND.HAISEN_NO = :haisenNo" +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 "AND IND.CANCEL_FLG = 'N'" +
			 "AND IIPD.INV_NO = IIMD.INV_NO" +
			 "AND IIPD.INV_DT = IIMD.INV_DT" +
			 "AND IIPD.MOD_NO = IIMD.MOD_NO" +
			 "AND IIPD.LOT_NO = IIMD.LOT_NO ) AS a," +
			 "TB_M_PRIVILEGE_TYPE" +
			 "where " +
			 "a.AICO_FLG = PRIV_CD" +
			 "GROUP BY INV_NO," +
			 "MOD_NO," +
			 "INV_AMT," +
			 "SERIES," +
			 "PCK_MTH", nativeQuery = true)
	List<Tuple> getInvoiceData(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT COUNT(1) R_RACK_FLG" +
			 "FROM TB_R_MTH_VPR_MODULE NVDM," +
			 "TB_R_INV_VPR_MODULE NVIM," +
			 "TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_INVOICE_H IND" +
			 "WHERE  NVIM.INV_NO = IICD.INV_NO" +
			 "AND NVIM.CONT_DST_CD = IICD.CONT_DST" +
			 "AND NVIM.CONT_SNO = IICD.CONT_SNO" +
			 "AND NVDM.CONT_DST_CD = NVIM.CONT_DST_CD" +
			 "AND NVDM.CONT_SNO = NVIM.CONT_SNO" +
			 "AND NVDM.MOD_DST_CD = NVIM.MOD_DST_CD" +
			 "AND NVDM.LOT_MOD_NO = NVIM.LOT_MOD_NO" +
			 "AND NVDM.CASE_NO = NVIM.CASE_NO" +
			 "AND NVDM.VAN_MTH = NVIM.VAN_MTH" +
			 "AND NVDM.RRACK_FLG = 'Y'" +
			 "AND NVIM.INV_NO IS NOT NULL" +
			 "AND IICD.INV_NO = IND.INV_NO" +
			 "AND IICD.BOOK_NO = :bookingNum" +
			 "AND IND.HAISEN_NO = :haisenNo" +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 "AND IND.CANCEL_FLG = 'N' ;",nativeQuery = true)
	Integer getRRackDataCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT GROUP_CONCAT(CONCAT(BOX_MAT_CD, ':', sum) separator ',') AS BOX_MAT_DATA " +
			 "from (" +
			 "SELECT BOX_MAT_CD,SUM(BOX_COUNT) AS sum" +
			 "FROM (" +
			 "ELECT COALESCE(NVIP.BOX_MAT_CD, :val ) AS BOX_MAT_CD, COUNT(DISTINCT COALESCE(NVIP.BOX_NO, NVIP.BOX_SEQ_NO)) AS BOX_COUNT" +
			 "FROM TB_R_INV_VPR_MODULE NVIM, TB_R_INV_VPR_PART NVIP, TB_R_INV_CONTAINER_D IICD,TB_R_INV_INVOICE_H IND" +
			 "WHERE NVIM.INV_NO = IICD.INV_NO" +
			 "AND NVIM.CONT_DST_CD = IICD.CONT_DST" +
			 "AND NVIM.CONT_SNO = IICD.CONT_SNO" +
			 "AND NVIM.CONT_DST_CD = NVIP.CONT_DST_CD " +
			 "AND NVIM.CONT_SNO = NVIP.CONT_SNO " +
			 "AND NVIM.MOD_DST_CD = NVIP.MOD_DST_CD " +
			 "AND NVIM.LOT_MOD_NO = NVIP.LOT_MOD_NO " +
			 "AND NVIM.CASE_NO = NVIP.CASE_NO " +
			 "AND NVIM.VAN_MTH = NVIP.VAN_MTH " +
			 "AND IICD.INV_NO = IND.INV_NO" +
			 "AND IICD.BOOK_NO = :bookingNum" +
			 "AND IND.HAISEN_NO = :haisenNo" +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 "AND IND.CANCEL_FLG = 'N' " +
			 "GROUP BY IICD.BOOK_NO, NVIM.CONT_SNO, NVIM.LOT_MOD_NO, NVIM.CASE_NO, COALESCE(NVIP.BOX_MAT_CD,:val) " +
			 " ) AS a Group by BOX_MAT_CD) b GROUP BY BOX_MAT_CD;", nativeQuery = true)
	List getRBoxDataCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth
			,@Param("val") String val);

	@Query(value="SELECT SUM(COUNT_40), SUM(COUNT_20)" +
			 "FROM ( SELECT COUNT(CASE" +
			 "WHEN CONT_SIZE = '40' THEN 1" +
			 "ELSE NULL" +
			 "END)" +
			 					"COUNT_40," +
			 					"COUNT(CASE" +
			 							"WHEN CONT_SIZE = '20' THEN 1" +
			 							"ELSE NULL" +
			 							"END)" +
			 					"COUNT_20" +
			 					"FROM (" +
			 							"(SELECT DISTINCT IICD.CONT_SNO,IICD.CONT_SIZE" +
			 									"FROM TB_R_INV_INVOICE_H IND," +
			 									"TB_R_INV_CONTAINER_D IICD," +
			 									"TB_R_INV_MODULE_D IIMD" +
			 									"WHERE IND.INV_NO = IICD.INV_NO" +
			 									"AND IND.INV_NO = IIMD.INV_NO" +
			 									"AND IND.INV_DT = IICD.INV_DT" +
			 									"AND IND.ORD_TYP = 'R'" +
			 									"AND IIMD.DG_FLG = '0'" +
			 									"AND IICD.CONT_SNO = IIMD.CONT_SNO" +
			 									"AND IICD.CONT_DST = IIMD.CONT_DST" +
			 									"AND IICD.INV_NO = IIMD.INV_NO" +
			 									"AND IICD.CONT_SIZE IN ('20', '40')" +
			 									"AND IICD.INV_NO = IND.INV_NO" +
			 									"AND IICD.BOOK_NO  = :bookingNum" +
			 									"AND IND.HAISEN_NO = :haisenNo" +
			 									"AND IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 									"AND IND.CANCEL_FLG = 'N'" +
			 									"AND (IICD.CONT_SNO,IICD.CONT_SIZE) NOT IN " +
			 									"(SELECT DISTINCT IICD.CONT_SNO,IICD.CONT_SIZE" +
			 											"FROM TB_R_INV_INVOICE_H IND," +
			 											"TB_R_INV_CONTAINER_D IICD," +
			 											"TB_R_INV_MODULE_D IIMD" +
			 											"WHERE IND.INV_NO = IICD.INV_NO" +
			 											"AND IND.INV_NO = IIMD.INV_NO" +
			 											"AND IND.INV_DT = IICD.INV_DT" +
			 											"AND IND.ORD_TYP = 'R'" +
			 											"AND IIMD.DG_FLG = '1'" +
			 											"AND IICD.CONT_SNO = IIMD.CONT_SNO" +
			 											"AND IICD.CONT_DST = IIMD.CONT_DST" +
			 											"AND IICD.INV_NO = IIMD.INV_NO" +
			 											"AND IICD.CONT_SIZE IN ('20', '40')" +
			 											"AND IICD.INV_NO = IND.INV_NO" +
			 											"AND IICD.BOOK_NO  = :bookingNum" +
			 										    "AND IND.HAISEN_NO = :haisenNo" +
			 											"AND IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 											"AND IND.CANCEL_FLG = 'N')" +
			 									")" +
			 							")a GROUP BY CONT_SIZE )b", nativeQuery = true)
	Tuple getNonDgCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT SUM(COUNT_40), SUM(COUNT_20)" +
			 "FROM (SELECT COUNT(CASE" +
			 "WHEN CONT_SIZE = '40' THEN 1" +
			 "ELSE NULL" +
			 "END)" +
			 "COUNT_40," +
			 "COUNT(CASE" +
			 "WHEN CONT_SIZE = '20' THEN 1" +
			 "ELSE NULL" +
			 "END)" +
			 "COUNT_20" +
			 "FROM " +
			 "(SELECT DISTINCT IICD.CONT_SNO, IICD.CONT_SIZE " +
			 "FROM TB_R_INV_INVOICE_H IND," +
			 "TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_MODULE_D IIMD" +
			 "WHERE IND.INV_NO = IICD.INV_NO" +
			 "AND IND.INV_NO = IIMD.INV_NO" +
			 "AND IND.INV_DT = IICD.INV_DT" +
			 "AND IND.ORD_TYP = 'R'" +
			 "AND IIMD.DG_FLG = '1'" +
			 "AND IICD.CONT_SNO = IIMD.CONT_SNO" +
			 "AND IICD.CONT_DST = IIMD.CONT_DST" +
			 "AND IICD.INV_NO = IIMD.INV_NO" +
			 "AND CONT_SIZE IN ('20', '40')" +
			 "AND IICD.BOOK_NO  = :bookingNum" +
			 "AND IND.HAISEN_NO = :haisenNo" +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 "AND IND.CANCEL_FLG = 'N' " +
			 ") a" +
			 "GROUP BY CONT_SIZE ) b", nativeQuery = true)
	Tuple getDgCount(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

	@Query(value="SELECT GROUP_CONCAT(SERIES" +
			 "ORDER BY SERIES" +
			 "SEPARATOR ' / ')" +
			 "SERIES" +
			 "FROM (" +
			 "SELECT DISTINCT IIPD.SERIES" +
			 "FROM TB_R_INV_CONTAINER_D IICD," +
			 "TB_R_INV_INVOICE_H IND," +
			 "TB_R_INV_MODULE_D IIMD," +
			 "TB_R_INV_PART_D IIPD" +
			 "WHERE IICD.INV_NO = IND.INV_NO" +
			 "AND IND.INV_NO = IIMD.INV_NO" +
			 "AND IICD.CONT_SNO = IIMD.CONT_SNO" +
			 "AND IICD.CONT_DST = IIMD.CONT_DST" +
			 "AND IIPD.INV_NO = IIMD.INV_NO" +
			 "AND IIPD.INV_DT = IIMD.INV_DT" +
			 "AND IIPD.MOD_NO = IIMD.MOD_NO" +
			 "AND IIPD.LOT_NO = IIMD.LOT_NO" +
			 "AND IICD.BOOK_NO = :bookingNum" +
			 "AND IND.HAISEN_NO = :haisenNo" +
			 "AND IND.HAISEN_YR_MTH = :haisenYrMnth" +
			 "AND IND.CANCEL_FLG = 'N'   ) a", nativeQuery = true)
	Object getDataForSeries(@Param("bookingNum") String bookingNum,@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);

}
