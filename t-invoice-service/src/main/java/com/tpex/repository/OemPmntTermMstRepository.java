package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemPmntTermMstEntity;

@Repository
public interface OemPmntTermMstRepository extends JpaRepository<OemPmntTermMstEntity, String> {

}
