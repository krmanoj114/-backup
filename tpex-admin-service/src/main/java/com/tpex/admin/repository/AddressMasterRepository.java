package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.AddressMasterEntity;
import com.tpex.admin.entity.AddressMasterIdEntity;


@Repository
public interface AddressMasterRepository extends JpaRepository<AddressMasterEntity, AddressMasterIdEntity>{

	List<AddressMasterEntity> findByCmpCodeOrderByIdCodeAscIdBranchAscNameAscAddress1Asc(String cmpCode);
}
