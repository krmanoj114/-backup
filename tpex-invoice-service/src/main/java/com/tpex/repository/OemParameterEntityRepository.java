package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.tpex.entity.OemParameterEntity;

@Repository
public interface OemParameterEntityRepository extends JpaRepository<OemParameterEntity, String> {

	@Query(value = "SELECT ope.oprParaVal  FROM OemParameterEntity ope WHERE ope.oprParaCd = 'CMP_CD'")
	String findByOprParaCd();

}
