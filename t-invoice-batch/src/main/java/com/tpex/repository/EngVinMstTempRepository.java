package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.EngVinMasterIdEntity;
import com.tpex.entity.EngVinMasterTempEntity;

@Repository
public interface EngVinMstTempRepository extends JpaRepository<EngVinMasterTempEntity, EngVinMasterIdEntity>{

}
