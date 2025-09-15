package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.NoemSeqCtrlMstEntity;
import com.tpex.entity.NoemSeqCtrlMstIdEntity;

@Repository
public interface NoemSeqCtrlMstRepository extends JpaRepository<NoemSeqCtrlMstEntity, NoemSeqCtrlMstIdEntity> {
	
}
