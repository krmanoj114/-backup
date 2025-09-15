package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.OemPmntTermMstEntity;

@Repository
public interface OemPmntTermMstRepository extends JpaRepository<OemPmntTermMstEntity, String> {
	List<OemPmntTermMstEntity> findByRegular(String name);
	List<OemPmntTermMstEntity> findByCpoSpo(String name);
}
