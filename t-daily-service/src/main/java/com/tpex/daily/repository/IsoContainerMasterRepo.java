package com.tpex.daily.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.daily.entity.IsoContainerMasterEntity;
import com.tpex.daily.entity.IsoContainerMasterIdEntity;

public interface IsoContainerMasterRepo extends JpaRepository<IsoContainerMasterEntity, IsoContainerMasterIdEntity> {

	List<IsoContainerMasterEntity> findByIdEtd1AndIdContDestCd(Date etdDate, String dstCode);

}
