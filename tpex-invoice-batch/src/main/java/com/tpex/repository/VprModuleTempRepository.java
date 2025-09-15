package com.tpex.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.tpex.entity.VprModuleTempEntity;
import com.tpex.entity.VprModuleTempId;

public interface VprModuleTempRepository extends JpaRepository<VprModuleTempEntity, VprModuleTempId> {

	@Modifying
	@Transactional
	public void deleteByIntrIdAndCompCode(String intrId, String compCode);

}
