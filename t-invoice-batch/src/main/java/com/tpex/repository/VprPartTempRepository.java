package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.entity.VprPartTempEntity;
import com.tpex.entity.VprPartTempId;

public interface VprPartTempRepository extends JpaRepository<VprPartTempEntity, VprPartTempId> {

	@Modifying
	@Transactional
	public void deleteByIntrIdAndCompCode(String intrId, String compCode);

}
