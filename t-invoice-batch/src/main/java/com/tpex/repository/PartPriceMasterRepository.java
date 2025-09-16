package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tpex.entity.PartPriceMasterEntity;
import com.tpex.entity.PartPriceMasterIdEntity;

import io.lettuce.core.dynamic.annotation.Param;

/**
 * The Interface PartPriceMasterRepository.
 */
@Repository
public interface PartPriceMasterRepository extends JpaRepository<PartPriceMasterEntity, PartPriceMasterIdEntity>{
	
	/**
	 * Find part price details.
	 *
	 * @param cfCode the cf code
	 * @param destCode the dest code
	 * @param effFromMonth the eff from month
	 * @param partNo the part no
	 * @param cmpCd the cmp cd
	 * @return the list
	 */
	//Using native query as EFF_FR_MTH and EFF_TO_MTH fields are string not date to comparing in JPA query is not possible
	@Query(value = "SELECT * FROM tb_m_part_price WHERE CF_CD =:cfCode AND DST_CD=:destCode AND"
			+ " (:effFromMonth IS NULL OR (EFF_FR_MTH <=:effFromMonth AND EFF_TO_MTH >=:effFromMonth))"
			+ " AND (:partNo IS NULL OR PART_NO=:partNo) AND CMP_CD=:cmpCd"
			+ " ORDER BY EFF_FR_MTH DESC, EFF_TO_MTH DESC, PART_NO ASC"
			, nativeQuery = true)
	List<PartPriceMasterEntity> findPartPriceDetails(@Param("cfCode") String cfCode, 
			@Param("destCode") String destCode, @Param("effFromMonth") String effFromMonth, 
			@Param("partNo") String partNo, @Param("cmpCd") String cmpCd);
	
	
	/**
	 * Find part price master count.
	 *
	 * @param cfCode the cf code
	 * @param destCode the dest code
	 * @param effFromMonth the eff from month
	 * @param partNo the part no
	 * @param cmpCd the cmp cd
	 * @return the int
	 */
	//Using native query as EFF_FR_MTH and EFF_TO_MTH fields are string not date to comparing in JPA query is not possible
	@Query(value = "SELECT count(*) FROM tb_m_part_price WHERE CF_CD =:cfCode AND DST_CD=:destCode AND"
			+ " (:effFromMonth IS NULL OR (EFF_FR_MTH <=:effFromMonth AND EFF_TO_MTH >=:effFromMonth))"
			+ " AND (:partNo IS NULL OR PART_NO=:partNo) AND CMP_CD=:cmpCd"
			, nativeQuery = true)
	int findPartPriceMasterCount(@Param("cfCode") String cfCode, @Param("destCode") String destCode, 
			@Param("effFromMonth") String effFromMonth, @Param("partNo") String partNo, @Param("cmpCd") String cmpCd);


	void deleteByIdCfCodeAndIdDestCodeAndCurrencyCodeAndIdPartNoAndIdEffFromMonth(String carFamilyCode,
			String impoterCode, String curreny, String partNo, String effectiveFromMonth);
	
	long countByIdCfCodeAndIdDestCodeAndIdPartNoAndIdEffFromMonthAndEffToMonth(String carFamilyCode,
			String impoterCode, String partNo, String effectiveFromMonth, String effectiveToMonth);
	
	long countByCurrencyCodeAndIdCfCodeAndIdDestCodeAndIdPartNoAndIdEffFromMonthAndEffToMonth(String currency, 
			String carFamilyCode, String impoterCode, String partNo, String effectiveFromMonth, String effectiveToMonth);
	
	long countByIdCfCodeAndIdDestCodeAndIdPartNo(String carFamilyCode, String impoterCode, String partNo);
	
	@Query(value = "SELECT * FROM tb_m_part_price WHERE CURR_CD =:currency AND CF_CD =:cfCode AND DST_CD=:destCode AND"
			+ " PART_NO=:partNo ORDER BY EFF_TO_MTH DESC LIMIT 1", nativeQuery = true)
	PartPriceMasterEntity findMaxControlRecordByCurrencyAndCfcAndImpAndPartNo(@Param("currency") String currency, 
			@Param("cfCode") String cfCode, @Param("destCode") String destCode, @Param("partNo") String partNo);
	
	@Query(value = "SELECT * FROM tb_m_part_price WHERE CURR_CD =:currency AND CF_CD =:cfCode AND DST_CD=:destCode AND"
			+ " PART_NO=:partNo AND EFF_TO_MTH >=:effFromMonth", nativeQuery = true)
	List<PartPriceMasterEntity> findOldTimeControlRecords(@Param("currency") String currency, @Param("cfCode") String cfCode, 
			@Param("destCode") String destCode, @Param("partNo") String partNo, @Param("effFromMonth") String effFromMonth);
	
}
