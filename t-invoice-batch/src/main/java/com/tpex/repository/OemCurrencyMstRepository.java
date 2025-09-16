package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemCurrencyMstEntity;

/**
 * The Interface OemCurrencyMstRepository.
 */
@Repository
public interface OemCurrencyMstRepository extends JpaRepository<OemCurrencyMstEntity, String> {

	/**
	 * Find all by company code order by crm cd asc.
	 *
	 * @param companyCode the company code
	 * @return the list
	 */
	List<OemCurrencyMstEntity> findAllByCompanyCodeOrderByCrmCdAsc(String companyCode);
	
	/**
	 * Count by crm cd.
	 *
	 * @param currencyCode the currency code
	 * @return the long
	 */
	long countByCrmCd(String currencyCode);
	
}
