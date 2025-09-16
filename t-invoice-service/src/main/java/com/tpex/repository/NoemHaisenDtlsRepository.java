package com.tpex.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.NoemHaisenDtlsEntity;
import com.tpex.entity.NoemHaisenDtlsIdEntity;

@Repository
@SuppressWarnings({"squid:S2479","squid:S107"})
public interface NoemHaisenDtlsRepository extends JpaRepository<NoemHaisenDtlsEntity, NoemHaisenDtlsIdEntity> {

	@Query(value = "SELECT DISTINCT a.HAISEN_YEAR_MTH,a.HAISEN_NO,a.ETD,a.ETA,a.BUYER,a.VSSL_NM_OCEAN,a.VOY_NO,a.NO_OF_20FT,a.NO_OF_40FT,a.CONT_EFF,a.SHIP_CO_NM,a.DEP_PORT,a.DST_PORT \r\n"
			+ "		FROM TB_R_HAISEN_D a INNER JOIN TB_R_INV_INVOICE_H b ON (a.HAISEN_NO = b.HAISEN_NO AND \r\n"
			+ "		a.HAISEN_YEAR_MTH = b.HAISEN_YR_MTH) where\r\n"
			+ "		(a.ETD>=:etdFrom) AND (a.ETA <= :etdTo) AND (:buyer is null or a.BUYER = :buyer) AND b.ORD_TYP IN(:regular,:cpo,:spo)\r\n"
			+ "		ORDER BY a.HAISEN_YEAR_MTH ASC, a.HAISEN_NO ASC", nativeQuery = true)
	List<Object[]> getHaisenDetails(@Param("etdFrom") LocalDate etdFrom, @Param("etdTo") LocalDate etdTo,
			@Param("buyer") String buyer, @Param("regular") String regular, @Param("cpo") String cpo,
			@Param("spo") String spo);

	@Query(value = "SELECT DISTINCT a.HAISEN_YEAR_MTH,a.HAISEN_NO,b.VESSEL_NAME_FEEDER,b.VOYAGE_NO_FEEDER\r\n"
			+ "			FROM TB_R_HAISEN_D a INNER JOIN TB_R_INV_INVOICE_H b ON (a.HAISEN_NO = b.HAISEN_NO AND a.HAISEN_YEAR_MTH = b.HAISEN_YR_MTH) where\r\n"
			+ "			(a.HAISEN_NO=:haisenNo) AND (a.HAISEN_YEAR_MTH = :haisenYearMonth) AND (:buyer is null or a.BUYER = :buyer) AND b.ORD_TYP IN(:regular,:cpo,:spo) ORDER BY a.HAISEN_YEAR_MTH ASC, a.HAISEN_NO ASC;\r\n"
			+ "", nativeQuery = true)
	List<Object[]> getfeederDetailsForHaisenNo(String haisenNo, String haisenYearMonth, String buyer, String regular,
			String cpo, String spo);

	@Query(value = "SELECT count(*) AS BUYER FROM   TB_R_HAISEN_D WHERE  BUYER =:buyer", nativeQuery = true)
	int getDetailsByBuyer(@Param("buyer") String buyer);

	/**
	 * query for get invoice details based on haisen no and haisenYear
	 * 
	 * @param haisenNo
	 * @param haisenYear
	 * @return
	 */
	@Query(value = "\r\n" + "SELECT \r\n" + "a.HAISEN_NO,\r\n" + "a.ETD,\r\n" + "a.ETA,\r\n" + "a.VSSL_NM_OCEAN,\r\n"
			+ "a.VOY_NO,\r\n" + "b.INV_NO,\r\n" + "b.INV_DT,\r\n" + "b.INV_AMT,\r\n" + "b.MEASUREMENT,\r\n"
			+ "b.VESSEL_NAME_FEEDER,\r\n" + "b.VOYAGE_NO_FEEDER,\r\n" + "b.BUYER,\r\n" + "b.SHIPPING_COMP,\r\n"
			+ "a.DEP_PORT,\r\n" + "a.DST_PORT\r\n"
			+ "FROM TB_R_HAISEN_D a INNER JOIN TB_R_INV_INVOICE_H b ON (a.HAISEN_NO = b.HAISEN_NO AND a.HAISEN_YEAR_MTH = b.HAISEN_YR_MTH) \r\n"
			+ "where a.HAISEN_NO=:haisenNo AND a.HAISEN_YEAR_MTH=:haisenYear AND b.CANCEL_FLG <> 'Y'", nativeQuery = true)
	List<Object> getInvDetails(@Param("haisenNo") String haisenNo, @Param("haisenYear") String haisenYear);

	/*
	 * @Tpex-144
	 * 
	 * @author akshatha.m.e
	 */
	@Query(value = "SELECT ETD " + "FROM   TB_R_HAISEN_D " + "WHERE  HAISEN_NO  = :haisenNo "
			+ "AND    HAISEN_YEAR_MTH       = :haisenYrMnth " + "LIMIT 1", nativeQuery = true)
	String getOldEtd(@Param("haisenNo") String haisenNo, @Param("haisenYrMnth") String haisenYrMnth);

	@Modifying
	@Query(value = " UPDATE TB_R_HAISEN_D" + " SET NO_OF_20FT = :noOf20FtCont, " + "     NO_OF_40FT = :noOf40FtCont, "
			+ "     CONT_EFF = :containerEffeciency, " + "     LCL_VOL = :lclVol, " + "     UPD_BY = :userId, "
			+ "     UPD_DT = :currentDate," + "     ETA =  :eta" + " WHERE HAISEN_YEAR_MTH = :haisenYearMonth "
			+ "    AND HAISEN_NO = :haisenNo", nativeQuery = true)
	void updateHaisenDtls(@Param("haisenNo") String haisenNo, @Param("haisenYearMonth") String haisenYearMonth,
			@Param("noOf20FtCont") Integer noOf20FtCont, @Param("noOf40FtCont") Integer noOf40FtCont,
			@Param("containerEffeciency") BigDecimal containerEffeciency, @Param("lclVol") Integer lclVol,
			@Param("userId") String userId, @Param("currentDate") Date currentDate, @Param("eta") Date eta);

	@Modifying
	@Query(value = " UPDATE TB_R_HAISEN_D" + " SET ETA = :eta, " + "     DEP_PORT = :portOfLoading, "
			+ "     DST_PORT = :portOfDischarge, " + "     VSSL_NM_OCEAN = :oceanVessel, "
			+ "     VOY_NO = :oceanVoyage, " + "     UPD_BY = :userId," + "     UPD_DT = :updDt"
			+ " WHERE HAISEN_YEAR_MTH = :newHaisenYrMnth " + "    AND HAISEN_NO = :newHaisenNo", nativeQuery = true)
	void updateHaisenforInvoiceChange(@Param("newHaisenNo") String newHaisenNo,
			@Param("newHaisenYrMnth") String newHaisenYrMnth, @Param("eta") Date eta,
			@Param("portOfLoading") String portOfLoading, @Param("portOfDischarge") String portOfDischarge,
			@Param("oceanVessel") String oceanVessel, @Param("oceanVoyage") String oceanVoyage,
			@Param("userId") String userId, @Param("updDt") Date updDt);

}
