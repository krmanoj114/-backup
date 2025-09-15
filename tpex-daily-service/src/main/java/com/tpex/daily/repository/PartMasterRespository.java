package com.tpex.daily.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.daily.entity.PartMasterEntity;

/**
 * The Interface PartMasterRespository.
 */
@Repository
public interface PartMasterRespository extends JpaRepository<PartMasterEntity, String>{

	/**
	 * Count by part no.
	 *
	 * @param partNo the part no
	 * @return the long
	 */
	long countByPartNo(String partNo);
	
	/**
	 * Count by part no and part name.
	 *
	 * @param partNo the part no
	 * @param partName the part name
	 * @return the long
	 */
	long countByPartNoAndPartName(String partNo, String partName);
}
