package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.entity.EngVinMasterEntity;
import com.tpex.entity.EngVinMasterIdEntity;

public interface EngVinMstRepository extends JpaRepository<EngVinMasterEntity, EngVinMasterIdEntity>{

}
