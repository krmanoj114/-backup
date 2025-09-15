package com.tpex.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.RddDownLocDtlEntity;

@Repository
@SuppressWarnings("squid:S2479")
public interface CertificateOriginReportRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {
	
	@Query(value = "SELECT TMAPTH_INV_FLG\r\n"
			+ "    FROM   TB_R_INV_INVOICE_H\r\n"
			+ "    WHERE  INV_NO=:invNumber", nativeQuery = true)
	String getTmapthInvFlg(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT CASE WHEN INV_DT >= (SELECT STR_TO_DATE(PARA_VAL,'%d%m%Y')\r\n"
			+ "FROM TB_M_PARAMETER WHERE PARA_CD = 'CMP_NM_CUTOFF') \r\n"
			+ "THEN 'TMAPTH_CMP' ELSE 'TMAPTH_CMPO' \r\n"
			+ "END COMP_CD   \r\n"
			+ "FROM   TB_R_INV_INVOICE_H\r\n"
			+ "WHERE  INV_NO=:invNumber", nativeQuery = true)
	String getCompCode(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT\r\n"
			+ "          CMP.NAME name,\r\n"
			+ "          CMP.ADD_1 address1,\r\n"
			+ "          CMP.ADD_2 address2,\r\n"
			+ "          CMP.ADD_3 address3,\r\n"
			+ "          CMP.ADD_4 address4\r\n"
			+ "              FROM\r\n"
			+ "          TB_M_CNSG  CMP,\r\n"
			+ "          TB_M_PARAMETER OPR\r\n"
			+ "    WHERE  CMP.CD         = :companyCode \r\n"
			+ "      AND    CMP.INV_ADD_FLG    = 'Y'\r\n"
			+ "   AND    CMP.COMPANY    = OPR.PARA_VAL\r\n"
			+ "   AND    OPR.PARA_CD    = :compCode", nativeQuery = true)
	Tuple getInvCompDetailWhenFlgY(@Param("companyCode") String companyCode,@Param("compCode") String compCode); 
	
	@Query(value = "SELECT CMP.NAME name, CMP.ADD_1 address1, CMP.ADD_2 address2, CMP.ADD_3 address3, CMP.ADD_4 address4 FROM TB_M_CNSG CMP WHERE CMP.CD = :companyCode AND CMP.INV_ADD_FLG = 'Y' AND CMP.COMPANY IN ('OTHER',companyCode)", nativeQuery = true)
	Tuple getInvCompDetailWhenFlgN(@Param("companyCode") String companyCode);
	
	@Query(value = " SELECT CMP.NAME consigneeName,\r\n"
			+ "          CMP.ADD_1 consigneeAddress1,\r\n"
			+ "          CMP.ADD_2 consigneeAddress2,\r\n"
			+ "          CMP.ADD_3 consigneeAddress3,\r\n"
			+ "          CMP.ADD_4 consigneeAddress4,\r\n"
			+ "          CMP.TEL_NO consigneeTelephoneNumber\r\n"
			+ "   FROM   TB_M_CNSG  CMP\r\n"
			+ "   WHERE  CMP.CD         = 'TMY'\r\n"
			+ "      AND CMP.COMPANY = 'OTHER'\r\n"
			+ "      AND CMP.BRANCH = '1'\r\n"
			+ "      AND    CMP.INV_ADD_FLG = 'Y'", nativeQuery = true)
	Tuple getConsigneeDetails();
	
	@Query(value = "SELECT\r\n"
			+ "        DATE_FORMAT(I.INV_DT,'%d/%m/%Y') invoiceDate,\r\n"
			+ "        NOTIFY_NM notifyName,\r\n"
			+ "        CONCAT(IFNULL(T.NAME, '') , ', ' , IFNULL(DST_NM, '')) destinationName,\r\n"
			+ "        NOTIFY_ADDR1 notifyAddress1,\r\n"
			+ "        NOTIFY_ADDR2 notifyAddress2,\r\n"
			+ "        NOTIFY_ADDR3 notifyAddress3,\r\n"
			+ "        NOTIFY_ADDR4 notifyAddress4,\r\n"
			+ "        NOTIFY_ZIP notifyZip,\r\n"
			+ "        NOTIFY_CNTRY notifyCountry,\r\n"
			+ "        DATE_FORMAT(I.ETD,'%d/%m/%Y') etd,\r\n"
			+ "        PART_NO partNumber,\r\n"
			+ "        PART_NM partName,\r\n"
			+ "        CO_CD companyCode,\r\n"
			+ "        SHIP_CO_NM companyName,\r\n"
			+ "        VSSL_NM_OCEAN vesselName,\r\n"
			+ "        VOY_NO voyageNumber,\r\n"
			+ "        SUM(UNIT_PER_BOX) sumTotalUnit\r\n"
			+ "    FROM\r\n"
			+ "        TB_R_INV_INVOICE_H I\r\n"
			+ "        LEFT OUTER JOIN \r\n"
			+ "        TB_R_HAISEN_D N ON I.ETD = N.ETD \r\n"
			+ "        AND I.BUYER = N.BUYER \r\n"
			+ "        AND I.DEP_PORT_CD = N.DEP_PORT \r\n"
			+ "        AND I.DEST_PORT_CD = N.DST_PORT \r\n"
			+ "        AND I.vessel_name_ocean = N.VSSL_NM_OCEAN \r\n"
			+ "        AND I.voyage_no_ocean = N.VOY_NO,\r\n"
			+ "        TB_R_INV_PART_D,\r\n"
			+ "        TB_M_FINAL_DESTINATION,\r\n"
			+ "        TB_M_PORT T\r\n"
			+ "    WHERE\r\n"
			+ "        I.INV_NO = :invNumber\r\n"
			+ "        AND I.INV_NO = TB_R_INV_PART_D.INV_NO\r\n"
			+ "        AND I.FINAL_DST = TB_M_FINAL_DESTINATION.DST_CD\r\n"
			+ "        AND I.DEST_PORT_CD = T.CD\r\n"
			+ "       GROUP BY\r\n"
			+ "        DATE_FORMAT(I.INV_DT,'%d/%m/%Y'),\r\n"
			+ "        NOTIFY_NM,\r\n"
			+ "        CONCAT(IFNULL(NAME, '') , ', ' , IFNULL(DST_NM, '')),\r\n"
			+ "        NOTIFY_ADDR1,\r\n"
			+ "        NOTIFY_ADDR2,\r\n"
			+ "        NOTIFY_ADDR3,\r\n"
			+ "        NOTIFY_ADDR4,\r\n"
			+ "        NOTIFY_ZIP,\r\n"
			+ "        NOTIFY_CNTRY,\r\n"
			+ "        DATE_FORMAT(I.ETD,'%d/%m/%Y'),\r\n"
			+ "        PART_NO,\r\n"
			+ "        PART_NM,\r\n"
			+ "        CO_CD,\r\n"
			+ "        SHIP_CO_NM,\r\n"
			+ "        VSSL_NM_OCEAN,\r\n"
			+ "        VOY_NO,\r\n"
			+ "        I.INV_NO\r\n"
			+ "		   order by PART_NO", nativeQuery = true)
	List<Tuple> getNotifyDetails(@Param("invNumber") String invNumber);

}
