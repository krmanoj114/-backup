package com.tpex.repository;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InsInvDtlsEntity;
@SuppressWarnings("squid:S107")
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

	Optional<InsInvDtlsEntity> findByIndInvNoAndCompanyCode(String invNo, String companyCode);
	
	@Query(value = "select INVP.INV_NO AS INV_NO, INVP.MOD_NO AS MOD_NO, INVP.LOT_NO AS LOT_NO, INVP.PART_NO AS PART_NO, INVP.BOX_NO AS BOX_NO, "
			+ " case "
			+ "   when INVDTLS.INV_PATTERN = 'P' "
			+ "    then ifnull((select PART_PRC from tb_m_part_price  "
			+ "         where CF_CD = INVP.CF_CD  "
			+ "            and DST_CD = INVDTLS.DEST_CD "
			+ "            and PART_NO = INVP.PART_NO "
			+ "            and CURR_CD = INVDTLS.CURRENCY "
			+ "            and EFF_FR_MTH <= INVDTLS.PACKING_MONTH "
			+ "            and (EFF_TO_MTH IS NULL OR EFF_TO_MTH >= INVDTLS.PACKING_MONTH)), 0) "
			+ "    else ifnull((select PART_PRC from tb_m_lot_part_price  "
			+ "         where CF_CD = INVP.CF_CD  "
			+ "            and DST_CD = INVDTLS.DEST_CD "
			+ "            and PART_NO = INVP.PART_NO "
			+ "            and LOT_CD = substring(INVP.LOT_NO, 1, 2) "
			+ "            and CURR_CD = INVDTLS.CURRENCY "
			+ "            and EFF_FR_MTH <= INVDTLS.PACKING_MONTH "
			+ "            and (EFF_TO_MTH IS NULL OR EFF_TO_MTH >= INVDTLS.PACKING_MONTH)), 0) "
			+ " end AS PART_PRICE "
			+ " from  "
			+ " (select distinct "
			+ "    INVH.INV_NO AS INV_NO, "
			+ "    INVH.FINAL_DST AS DEST_CD,  "
			+ "    INVH.LOT_PTTRN AS INV_PATTERN,  "
			+ "    INVH.PAY_CRNCY AS CURRENCY,  "
			+ "    case "
			+ "    when INVM.PCK_MTH = '******' "
			+ "    then INVH.ETD "
			+ "    else INVM.PCK_MTH "
			+ "    end as PACKING_MONTH "
			+ "    from tb_r_inv_invoice_h as INVH  "
			+ "    inner join tb_r_inv_module_d as INVM on INVM.INV_NO = INVH.INV_NO "
			+ "    where INVH.INV_NO = :invNo "
			+ "    and INVH.CMP_CD = :cmpCd "
			+ " ) AS INVDTLS "
			+ " inner join tb_r_inv_part_d as INVP on INVP.INV_NO = INVDTLS.INV_NO;", nativeQuery = true)
	List<Map<String, Object>> getInvPartsForPartPriceUpdate(@Param("invNo") String invNo, @Param("cmpCd") String cmpCd);
	
	@Query(value = "select INVP.INV_NO AS INV_NO, INVP.MOD_NO AS MOD_NO, INVP.LOT_NO AS LOT_NO, INVP.PART_NO AS PART_NO, INVP.BOX_NO AS BOX_NO, "
			+ " case "
			+ "   when INVDTLS.INV_PATTERN = 'P' "
			+ "    then (select PART_NM from tb_m_part_price  "
			+ "         where CF_CD = INVP.CF_CD  "
			+ "            and DST_CD = INVDTLS.DEST_CD "
			+ "            and PART_NO = INVP.PART_NO "
			+ "            and CURR_CD = INVDTLS.CURRENCY "
			+ "            and EFF_FR_MTH <= INVDTLS.PACKING_MONTH "
			+ "            and (EFF_TO_MTH IS NULL OR EFF_TO_MTH >= INVDTLS.PACKING_MONTH)) "
			+ "    else (select PART_NM from tb_m_lot_part_price  "
			+ "         where CF_CD = INVP.CF_CD  "
			+ "            and DST_CD = INVDTLS.DEST_CD "
			+ "            and PART_NO = INVP.PART_NO "
			+ "            and LOT_CD = substring(INVP.LOT_NO, 1, 2) "
			+ "            and CURR_CD = INVDTLS.CURRENCY "
			+ "            and EFF_FR_MTH <= INVDTLS.PACKING_MONTH "
			+ "            and (EFF_TO_MTH IS NULL OR EFF_TO_MTH >= INVDTLS.PACKING_MONTH)) "
			+ " end AS PART_NAME "
			+ " from  "
			+ " (select distinct "
			+ "    INVH.INV_NO AS INV_NO, "
			+ "    INVH.FINAL_DST AS DEST_CD,  "
			+ "    INVH.LOT_PTTRN AS INV_PATTERN,  "
			+ "    INVH.PAY_CRNCY AS CURRENCY,  "
			+ "    case "
			+ "    when INVM.PCK_MTH = '******' "
			+ "    then INVH.ETD "
			+ "    else INVM.PCK_MTH "
			+ "    end as PACKING_MONTH "
			+ "    from tb_r_inv_invoice_h as INVH  "
			+ "    inner join tb_r_inv_module_d as INVM on INVM.INV_NO = INVH.INV_NO "
			+ "    where INVH.INV_NO = :invNo "
			+ "    and INVH.CMP_CD = :cmpCd "
			+ " ) AS INVDTLS "
			+ " inner join tb_r_inv_part_d as INVP on INVP.INV_NO = INVDTLS.INV_NO;", nativeQuery = true)
	List<Map<String, Object>> getInvPartsForPartNameUpdate(@Param("invNo") String invNo, @Param("cmpCd") String cmpCd);
 
	@Procedure(procedureName = "ReCalculateInvoicePrivilage", outputParameterName = "result")
	String reCalculateInvoicePrivilage(@Param("invoiceNumber") String invoiceNumber, @Param("companyCode") String companyCode, @Param("userId") String userId);
}
