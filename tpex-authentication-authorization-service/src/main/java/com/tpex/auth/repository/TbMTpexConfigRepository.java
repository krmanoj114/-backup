package com.tpex.auth.repository;


import com.tpex.auth.entity.TbMTpexConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//avoid conflict bean
@Repository("tpexConfigAuth")
public interface TbMTpexConfigRepository extends JpaRepository<TbMTpexConfigEntity, Integer> {
}