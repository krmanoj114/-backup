package com.tpex.repository;

import java.sql.Date;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InsInvDtlsEntity;

@Repository
public interface InsInvDetailsRepository extends JpaRepository<InsInvDtlsEntity, String> {

	/* @Tpex-144
	 * @author akshatha.m.e
	 */

	@Query(value=" SELECT * FROM   TB_R_INV_INVOICE_H"+
			" WHERE (HAISEN_NO =:oldHaisenNo AND HAISEN_YR_MTH =:oldHaisenYearMonth) " +
			" OR    (HAISEN_NO =:newHaisenNo AND HAISEN_YR_MTH =:newHaisenYrMnth)",nativeQuery=true)
	List<InsInvDtlsEntity> getInvTnso(@Param("oldHaisenNo") String oldHaisenNo,@Param("oldHaisenYearMonth") String oldHaisenYearMonth,@Param("newHaisenNo") String newHaisenNo,@Param("newHaisenYrMnth") String newHaisenYrMnth);

	List<InsInvDtlsEntity> findByIndHaisenNoAndIndHaisenYearMonth(String haisenNo,String haisenYrMnth);

	@Query(value = "SELECT  INV_NO " +
			" FROM TB_R_INV_INVOICE_H"  +
			" WHERE   HAISEN_NO =:haisenNo"  +
			" AND  HAISEN_YR_MTH = :haisenYrMnth", nativeQuery = true)
	List<String> getInvNo(@Param("haisenNo") String haisenNo,@Param("haisenYrMnth") String haisenYrMnth);
	
	@Query(value = "select DISTINCT cust,cust_nm,notify,notify_nm,CUST_ADDR1,CUST_ADDR2,CUST_ADDR3,CUST_ADDR4,NOTIFY_ADDR1,NOTIFY_ADDR2,NOTIFY_ADDR3,NOTIFY_ADDR4 from TB_R_INV_INVOICE_H where inv_no=:envNo ", nativeQuery = true)
	Tuple getCustCode(@Param("envNo") String envNo);

	@Query(value = "select DISTINCT cust,cust_nm,notify_nm,CUST_ADDR1,CUST_ADDR2,CUST_ADDR3,CUST_ADDR4,NOTIFY_ADDR1,NOTIFY_ADDR2,NOTIFY_ADDR3,NOTIFY_ADDR4 from TB_R_INV_INVOICE_H ", nativeQuery = true)
	List<Object[]> getCustInformation();

	/* @Tpex-144
	 * @author akshatha.m.e
	 */

	@Query(value=" SELECT HAISEN_NO, HAISEN_YR_MTH,ETA,DEST_PORT_CD,DEP_PORT_CD,VESSEL_NAME_OCEAN,VOYAGE_NO_OCEAN "   +
			" FROM   TB_R_INV_INVOICE_H"   +
			" WHERE  INV_NO = :invNo" ,nativeQuery = true)
	Object getHaisenNo(@Param("invNo") String invNo);

	@Modifying
	@Query(value = " UPDATE TB_R_INV_INVOICE_H SET ETA = :eta, VESSEL_NAME_OCEAN = :vslNameOcean, HAISEN_NO = :haisenNo , HAISEN_YR_MTH = :haisenYrMnth , VOYAGE_NO_OCEAN = :voyNo, DEP_PORT_CD = :portOfLoading, DEST_PORT_CD = :portOfDischarge, UPD_BY = :userId, UPD_DT = :updDt, TNSO_FLG = 'N'  WHERE  INV_NO = :invNo " ,nativeQuery = true)
	void updateInvoiceTable(@Param("invNo") String haisenNo,@Param("haisenNo") String invNo,@Param("haisenYrMnth") String haisenYrMnth,@Param("eta") Date eta,@Param("vslNameOcean") String vslNameOcean,@Param("voyNo") String voyNo,
			@Param("portOfLoading") String portOfLoading,@Param("portOfDischarge") String portOfDischarge,@Param("userId") String userId,@Param("updDt") Date updDt);


	@Query(value = "select DISTINCT cust,cust_nm,CUST_ADDR1,CUST_ADDR2,CUST_ADDR3,CUST_ADDR4 from TB_R_INV_INVOICE_H where cust IS NOT NULL and CUST_ADDR1 IS NOT NULL", nativeQuery = true)
	List<Object[]> getCustData();
	
	@Query(value = "select DISTINCT notify,notify_nm,notify_addr1,notify_addr2,notify_addr3,notify_addr4 from TB_R_INV_INVOICE_H where notify IS NOT NULL and NOTIFY_ADDR1 IS NOT NULL", nativeQuery = true)
	List<Object[]> getNotifyData();
	
	@Query(value = "SELECT BUYER,BUYER_NM,BUYER_ADDR1,BUYER_ADDR2,BUYER_ADDR3,BUYER_ADDR4 FROM  TB_R_INV_INVOICE_H WHERE INV_NO=:envNo ", nativeQuery = true)
	Tuple getBuyerCode(@Param("envNo") String envNo);

}
