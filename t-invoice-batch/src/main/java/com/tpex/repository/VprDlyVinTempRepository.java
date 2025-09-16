package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.entity.VprDlyVinTempEntity;
import com.tpex.entity.VprDlyVinTempId;

public interface VprDlyVinTempRepository extends JpaRepository<VprDlyVinTempEntity, VprDlyVinTempId> {

	@Modifying
	@Transactional
	public void deleteByIntrIdAndCompCode(String intrId, String compCode);

}
