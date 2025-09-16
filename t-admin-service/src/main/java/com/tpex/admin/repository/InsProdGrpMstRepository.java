package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.InsProdGrpMstEntity;

@Repository
public interface InsProdGrpMstRepository extends JpaRepository<InsProdGrpMstEntity, String> {

	List<InsProdGrpMstEntity> findAllByOrderByIpgProdGrpCd();

}
