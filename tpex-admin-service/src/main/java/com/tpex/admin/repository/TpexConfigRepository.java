package com.tpex.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.TpexConfigEntity;

@Repository
public interface TpexConfigRepository extends JpaRepository<TpexConfigEntity, Integer> {

	TpexConfigEntity findByName(String string);

}