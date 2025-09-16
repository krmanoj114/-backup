package com.tpex.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.tpex.auth.entity.TpexConfigEntity;

//avoid conflict bean
@Repository("tpexConfigAuth")
public interface TpexConfigRepository extends JpaRepository<TpexConfigEntity, Integer> {
}