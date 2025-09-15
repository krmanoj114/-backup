package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpex.entity.CoeCeptId;
import com.tpex.entity.CoeCeptMasterEntity;

public interface CoeCeptMasterRepository extends JpaRepository<CoeCeptMasterEntity, CoeCeptId> {

}
