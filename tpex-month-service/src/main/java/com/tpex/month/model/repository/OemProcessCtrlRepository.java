package com.tpex.month.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tpex.month.model.entity.OemProcessCtrlEntity;
import com.tpex.month.model.entity.OemProcessCtrlIdEntity;


public interface OemProcessCtrlRepository extends JpaRepository<OemProcessCtrlEntity, OemProcessCtrlIdEntity> {

	@Query(value = "select count(process_ctrl_id)+1 from TB_M_PROCESS_CONTROLER", nativeQuery = true)
	int getIdOfProcessControl();
}
