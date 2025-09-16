package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.OemPortMstEntity;

@Repository
public interface OemPortMstRepository extends JpaRepository<OemPortMstEntity, String> {

	List<OemPortMstEntity> findAllByCdOrderByCdAsc(String cd);
}