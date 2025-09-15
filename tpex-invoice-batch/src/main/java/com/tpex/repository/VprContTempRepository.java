package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.entity.VprContTempEntity;
import com.tpex.entity.VprContTempId;

public interface VprContTempRepository extends JpaRepository<VprContTempEntity, VprContTempId> {

	@Modifying
	@Transactional
	public void deleteByIntrIdAndCompCode(String intrId, String compCode);
}
