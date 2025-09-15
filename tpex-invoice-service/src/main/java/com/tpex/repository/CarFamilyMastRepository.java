package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.CarFamilyMasterEntity;

@Repository
public interface CarFamilyMastRepository extends JpaRepository<CarFamilyMasterEntity, String>{

	List<CarFamilyMasterEntity> findAllByOrderByCarFmlyCodeAsc();
	
	List<CarFamilyMasterEntity> findAllByOrderByCarFmlySrsNameAsc();

	/**
	 * Find all by company code order by car fmly code asc.
	 *
	 * @param companyCode the company code
	 * @return the list
	 */
	List<CarFamilyMasterEntity> findAllByCompanyCodeOrderByCarFmlyCodeAsc(String companyCode);
}
