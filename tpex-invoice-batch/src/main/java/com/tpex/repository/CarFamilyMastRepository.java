package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.CarFamilyMasterEntity;

/**
 * The Interface CarFamilyMastRepository.
 */
@Repository
public interface CarFamilyMastRepository extends JpaRepository<CarFamilyMasterEntity, String>{

	/**
	 * Find all by order by car fmly code asc.
	 *
	 * @return the list
	 */
	List<CarFamilyMasterEntity> findAllByOrderByCarFmlyCodeAsc();
	
	/**
	 * Find all by order by car fmly srs name asc.
	 *
	 * @return the list
	 */
	List<CarFamilyMasterEntity> findAllByOrderByCarFmlySrsNameAsc();

	/**
	 * Find all by company code order by car fmly code asc.
	 *
	 * @param companyCode the company code
	 * @return the list
	 */
	List<CarFamilyMasterEntity> findAllByCompanyCodeOrderByCarFmlyCodeAsc(String companyCode);
	
	/**
	 * Count by car fmly code.
	 *
	 * @param companyCode the company code
	 * @return the long
	 */
	long countByCarFmlyCode(String companyCode);
}
