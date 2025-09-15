package com.tpex.month.model.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.month.model.entity.TpexConfigEntity;

public interface TpexConfigRepository extends JpaRepository<TpexConfigEntity, Integer> {
}