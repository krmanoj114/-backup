package com.tpex.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.entity.OemFnlDstMstEntity;

/**
 * The Interface OemFnlDstMstRepository.
 */
public interface OemFnlDstMstRepository extends JpaRepository<OemFnlDstMstEntity, String>{

	/**
	 * Find all by order by fd dst cd asc.
	 *
	 * @return the list
	 */
	List<OemFnlDstMstEntity> findAllByOrderByFdDstCdAsc();

	/**
	 * Find all by company code order by fd dst cd asc.
	 *
	 * @param companyCode the company code
	 * @return the list
	 */
	List<OemFnlDstMstEntity> findAllByCompanyCodeOrderByFdDstCdAsc(String companyCode);
	
	/**
	 * Count by fd dst cd.
	 *
	 * @param destCode the dest code
	 * @return the long
	 */
	long countByFdDstCd(String destCode);
	
}
