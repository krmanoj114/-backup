package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.PrivilegeTypeEntity;

@Repository
public interface PrivilegeTypeRepository extends JpaRepository<PrivilegeTypeEntity, String>{

	PrivilegeTypeEntity findByPrivilegeCode(String inpAicoFlg);
	

}
