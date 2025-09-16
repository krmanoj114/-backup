package com.tpex.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.NoemCbMstEntity;

@Repository
public interface NoemCbMstRepository extends JpaRepository<NoemCbMstEntity, String> {

	List<NoemCbMstEntity> findAllByOrderByCbNm();

}