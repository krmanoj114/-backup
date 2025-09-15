package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tpex.entity.InsInvDetailsEntity;

public interface InsInvDetailsEntityRepository extends JpaRepository<InsInvDetailsEntity, String> {

	@Query(value = "SELECT iide.tmapthInvFlg FROM InsInvDetailsEntity iide WHERE iide.invoiceNo = :invNumber")
	String findByTmapthInvFlg(@Param("invNumber") String invNumber);

}
