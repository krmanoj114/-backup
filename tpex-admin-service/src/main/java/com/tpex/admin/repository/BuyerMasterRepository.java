package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.BuyerMasterEntity;

@Repository
public interface BuyerMasterRepository extends JpaRepository<BuyerMasterEntity, String> {
	List<BuyerMasterEntity> findAllBycmpCdOrderByBuyerCodeAsc(String cmpCd);
}
