package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tpex.entity.PrivilegeMasterEntity;
import com.tpex.entity.PrivilegeMasterIdEntity;

@Repository
public interface PrivilegeMasterRepository extends JpaRepository<PrivilegeMasterEntity, PrivilegeMasterIdEntity>{

	@Query(value="select PRIV_CD,PRIV_NAME,UPD_BY,UPD_DT,CMP_CD from TB_M_PRIVILEGE order by PRIV_NAME,PRIV_CD ASC",nativeQuery = true)
	List<PrivilegeMasterEntity> findPriorities();
}
