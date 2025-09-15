package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tpex.entity.PrivilegeTypeEntity;

public interface PrivilegeTypeEntityRepository extends JpaRepository<PrivilegeTypeEntity, String> {

	@Query(value = "SELECT DISTINCT pte.privilegeCode FROM PrivilegeTypeEntity pte WHERE pte.setInvCd=:invoiceType")
	String findByPrivilegeCode(@Param("invoiceType") String invoiceType);
}
