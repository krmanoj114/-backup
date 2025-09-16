package com.tpex.repository;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.RddDownLocDtlEntity;

@Repository
@SuppressWarnings("squid:S2479")
public interface InvoiceHeaderPageRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {
	
	@Query(value = "SELECT IFNULL(PARA_VAL, 'N') FROM TB_M_PARAMETER WHERE PARA_CD ='CNTRY_ORG'", nativeQuery = true)
	String getCountryOfOrigin();
	
	
	@Query(value = "SELECT FD.DST_NM FROM TB_R_INV_INVOICE_H IND, TB_M_FINAL_DESTINATION FD WHERE IND.INV_NO = 'KR22100530' AND IND.FINAL_DST = FD.DST_CD", nativeQuery = true)
	String getFinalCountryName(@Param("invNumber") String invNumber);
	
	@Query(value = "SELECT \r\n" + 
	"           IND.MARK1                                                                            MARK1,\r\n" + 
	"           IND.MARK2                                                                            MARK2,\r\n" + 
	"           IND.MARK3                                                                            MARK3,\r\n" + 
	"           IND.MARK4                                                                            MARK4,\r\n" + 
	"           CASE WHEN RTRIM(ifnull(IND.MARK4,'')) = '' THEN '' ELSE CONCAT('LOT NO. : ' , SUBSTR(IND.MARK4,1,75)) END MARK4_1,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,76,85)  END          MARK4_2, \r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,161,85) END                       MARK4_3,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,246,85) END                       MARK4_4,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,331,85) END                       MARK4_5,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,416,85) END                       MARK4_6,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,501,85) END                       MARK4_7,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,586,85) END                       MARK4_8,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,671,85) END                       MARK4_9,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,756,85) END                       MARK4_10,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,841,85) END                       MARK4_11,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK4) = NULL THEN '' ELSE SUBSTR(IND.MARK4,926,74) END                       MARK4_12,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE CONCAT('CASE NO. : ' , SUBSTR(IND.MARK5,1,75)) END    MARK5_1,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,76,85)  END                       MARK5_2,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,161,85) END                       MARK5_3,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,246,85) END                       MARK5_4,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,331,85) END                       MARK5_5,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,416,85) END                       MARK5_6,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,501,85) END                       MARK5_7,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,586,85) END                       MARK5_8,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,671,85) END                       MARK5_9,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,756,85) END                       MARK5_10,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,841,85) END                       MARK5_11,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK5) = NULL THEN '' ELSE SUBSTR(IND.MARK5,926,74) END                       MARK5_12,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE CONCAT('Container Renban : ' , SUBSTR(IND.MARK6,1,66)) END MARK6_1,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,67,85)  END                       MARK6_2,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,152,85) END                       MARK6_3,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,237,85) END                       MARK6_4,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,322,85) END                       MARK6_5,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,407,85) END                       MARK6_6,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,492,85) END                       MARK6_7,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,577,85) END                       MARK6_8,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,662,85) END                       MARK6_9,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,747,85) END                       MARK6_10,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,832,85) END                       MARK6_11,\r\n" + 
	"           CASE WHEN RTRIM(IND.MARK6) = NULL THEN '' ELSE SUBSTR(IND.MARK6,917,83) END                       MARK6_12,\r\n" + 
	"           IND.MARK7                                                                            MARK7,\r\n" + 
	"           IND.MARK8,  \r\n" + 
	" 			date_format(str_to_date(IND.INV_DT, '%Y-%m-%d'), '%d/%m/%Y')                                         INV_DT\r\n" +
	"    FROM   TB_R_INV_INVOICE_H IND\r\n" + 
	"    WHERE\r\n" + 
	"           IND.INV_NO        =  :invNumber\r\n" , nativeQuery = true)
	Tuple getInvoiceHeaderPageDetail(@Param("invNumber") String invNumber);

}
