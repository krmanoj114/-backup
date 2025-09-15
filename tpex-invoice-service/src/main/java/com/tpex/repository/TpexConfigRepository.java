package com.tpex.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.TpexConfigEntity;

@Repository
public interface TpexConfigRepository extends JpaRepository<TpexConfigEntity, Integer> {

	TpexConfigEntity findByName(String string);

	//List<TpexConfigEntity> getValuesbyNames(List<String> asList);

}