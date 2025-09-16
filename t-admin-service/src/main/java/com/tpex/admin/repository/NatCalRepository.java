package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.NatCalEntity;
import com.tpex.admin.entity.NatCalIdEntity;
@Repository
public interface NatCalRepository extends JpaRepository<NatCalEntity, NatCalIdEntity> {

	@Query(value ="SELECT * FROM TB_M_NATIONAL_CALANDER where year =:year ORDER BY crt_dt,month", nativeQuery = true)
	List<NatCalEntity> findByIdYear(@Param("year") int year);
	
}