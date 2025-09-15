package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.SystemDtlsEntity;
import com.tpex.admin.entity.SystemDtlsIdEntity;

@Repository
public interface SystemDtlsRepository extends JpaRepository<SystemDtlsEntity, SystemDtlsIdEntity> {

	List<SystemDtlsEntity> findAllByIdCompanyCdOrderByIdSystemNameAsc(String companyCd);
}
