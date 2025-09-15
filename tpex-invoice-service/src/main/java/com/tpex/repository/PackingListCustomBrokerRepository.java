package com.tpex.repository;

import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.RddDownLocDtlEntity;



@Repository
public interface PackingListCustomBrokerRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {

	
	@Query(value = "CALL PINS002A(:invNumber,'NIITTMT') ", nativeQuery = true)  
	List<Object[]> getPINS002AData(@Param("invNumber") String invNumber);
	
	@Query(value = " SELECT INS_CNSG_NAME insCnsgName,\r\n"
			+ "          INS_CNSG_ADD1 insCnsgAdd1,\r\n"
			+ "          INS_CNSG_ADD2 insCnsgAdd2,\r\n"
			+ "          INS_CNSG_ADD3 insCnsgAdd3,\r\n"
			+ "          INS_CNSG_ADD4 insCnsgAdd4,\r\n"
			+ "          INS_INV_NO insInvNo,\r\n"
			+ "          INS_INV_DT insInvDt,\r\n"
			+ "          INS_PART_NO insPartNo,\r\n"
			+ "          INS_PART_NAME insPartName,\r\n"
			+ "          INS_SUM_TOT_UNIT insSumTotUnit,\r\n"
			+ "          INS_ICO_FLG insIcoFlag,\r\n"
			+ "          INS_PART_PRICE insPartPrice,\r\n"
			+ "          INS_UNIT_PER_BOX insUnitPerBox,\r\n"
			+ "          INS_PART_WT insPartWt,\r\n"
			+ "          INS_GROSS_WT insGrossWt,\r\n"
			+ "          INS_MEASUREMENT insMeasurement,\r\n"
			+ "          INS_SHIPMARK_4 insShipmark4,\r\n"
			+ "          INS_SHIPMARK_5 insShipmark5,\r\n"
			+ "		     SUBSTR(INS_SHIPMARK_5, 1, 2)  shipMarkGp,\r\n"
			+ "          INS_SHIPMARK_5 caseMod,\r\n"
			+ "          INS_CF_CD insCfCd,\r\n"
			+ "          INS_SRS_NAME insSrsName,\r\n"
			+ "          INS_NO_OF_CASES insNoOfCases\r\n"
			+ "      FROM TB_R_INS_PACK_LIST\r\n"
			+ "      WHERE (SUBSTR(INS_SHIPMARK_5,3,1) IS NULL OR ASCII(SUBSTR(INS_SHIPMARK_5, 3, 1)) BETWEEN 48 AND 57)\r\n"
			+ "      UNION\r\n"
			+ "      SELECT INS_CNSG_NAME insCnsgName,\r\n"
			+ "          INS_CNSG_ADD1 insCnsgAdd1,\r\n"
			+ "          INS_CNSG_ADD2 insCnsgAdd2,\r\n"
			+ "          INS_CNSG_ADD3 insCnsgAdd3,\r\n"
			+ "          INS_CNSG_ADD4 insCnsgAdd4,\r\n"
			+ "          INS_INV_NO insInvNo,\r\n"
			+ "          INS_INV_DT insInvDt,\r\n"
			+ "          INS_PART_NO insPartNo,\r\n"
			+ "          INS_PART_NAME insPartName,\r\n"
			+ "          INS_SUM_TOT_UNIT insSumTotUnit,\r\n"
			+ "          INS_ICO_FLG insIcoFlag,\r\n"
			+ "          INS_PART_PRICE insPartPrice,\r\n"
			+ "          INS_UNIT_PER_BOX insUnitPerBox,\r\n"
			+ "          INS_PART_WT insPartWt,\r\n"
			+ "          INS_GROSS_WT insGrossWt,\r\n"
			+ "          INS_MEASUREMENT insMeasurement,\r\n"
			+ "          INS_SHIPMARK_4 insShipmark4,\r\n"
			+ "          INS_SHIPMARK_5 insShipmark5 ,\r\n"
			+ "          SUBSTR(INS_SHIPMARK_5, 1, 2)  shipMarkGp,\r\n"
			+ "		     INS_SHIPMARK_5  caseMod,\r\n"
			+ "          INS_CF_CD insCfCd,\r\n"
			+ "          INS_SRS_NAME insSrsName,\r\n"
			+ "          INS_NO_OF_CASES insNoOfCases\r\n"
			+ "      FROM TB_R_INS_PACK_LIST\r\n"
			+ "      WHERE (SUBSTR(INS_SHIPMARK_5,3,1) IS NOT NULL AND ASCII(SUBSTR(INS_SHIPMARK_5, 3, 1)) NOT BETWEEN 48 AND 57)\r\n"
			+ "      ORDER BY insShipmark5; ", nativeQuery = true)  
	List<Object[]> getINSPACKLISTTEMPData();
	
}
