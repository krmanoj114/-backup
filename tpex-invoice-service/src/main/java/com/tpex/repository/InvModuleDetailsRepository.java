package com.tpex.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InvModuleDetailsEntity;
import com.tpex.entity.InvModuleDetailsIdEntity;

@Repository
public interface InvModuleDetailsRepository extends JpaRepository<InvModuleDetailsEntity, InvModuleDetailsIdEntity> {

	@Query(value = "select INVM.INV_NO, INVM.MOD_NO, INVM.LOT_NO, SUM(INVP.BOX_GROSS_WT) AS GROSS_WT "
			+ "from tb_r_inv_module_d as INVM "
			+ "inner join tb_r_inv_part_d as INVP on INVP.INV_NO = INVM.INV_NO and INVP.MOD_NO = INVM.MOD_NO and INVP.LOT_NO = INVM.LOT_NO "
			+ "where INVM.INV_NO = :invNo "
			+ "group by INVM.INV_NO, INVM.MOD_NO, INVM.LOT_NO;", nativeQuery = true)
	List<Map<String, Object>> getInvModuleForGrossWtUpdate(@Param("invNo") String invNo);

}
