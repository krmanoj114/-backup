package com.tpex.month.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.month.model.entity.RenbanBookMasterEntity;
import com.tpex.month.model.entity.RenbanBookMasterId;

public interface RenbanBookMasterRespository extends JpaRepository<RenbanBookMasterEntity, RenbanBookMasterId> {

}
