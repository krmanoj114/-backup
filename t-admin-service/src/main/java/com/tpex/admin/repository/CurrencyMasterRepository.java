package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.CurrencyMasterEntity;

@Repository
public interface CurrencyMasterRepository extends JpaRepository<CurrencyMasterEntity, String> {
	List<CurrencyMasterEntity> findAllBycmpCdOrderByCurrencyCodeAsc(String cmpCd);

}
