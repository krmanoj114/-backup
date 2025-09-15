package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemPortMstEntity;

@Repository
public interface OemPortMstRepository extends JpaRepository<OemPortMstEntity, String> {

	@Query(value = "SELECT * FROM TB_M_PORT WHERE CD=:disPortCode", nativeQuery = true)
	OemPortMstEntity findBydepPortCd(@Param("disPortCode") String disPortCode);

	List<OemPortMstEntity> findAllByOrderByCd();

	List<OemPortMstEntity> findAllByOrderByCdAsc();

	long countByCd(String portOfLoading);
}