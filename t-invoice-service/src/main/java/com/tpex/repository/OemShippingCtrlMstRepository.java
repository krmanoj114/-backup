package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemShippingCtrlMstEntity;
import com.tpex.entity.OemShippingCtrlMstIdEntity;

@Repository
public interface OemShippingCtrlMstRepository extends JpaRepository<OemShippingCtrlMstEntity, OemShippingCtrlMstIdEntity>{
	
	List<OemShippingCtrlMstEntity> findAllByOrderByIdShCtrlImpDstCdAscIdShCtrlCfCdAsc();

}
