package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InhouseShopMasterEntity;

@Repository
public interface InhouseShopMasterRepository extends JpaRepository<InhouseShopMasterEntity,String>{

	long countByInsShopCd(String inHouseShop);

}
