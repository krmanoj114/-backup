package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.SeperateInvDetailsEntity;
import com.tpex.entity.SeperateInvDetailsIdEntity;

@Repository
public interface SeperateInvDetailsRepository
		extends JpaRepository<SeperateInvDetailsEntity, SeperateInvDetailsIdEntity> {

	@Query(value = "select IMP_CD,PART_NO,CF_CD,SERIES,AICO_FLG,PRIV_NM from TB_R_INV_PART_D JOIN TB_M_PRIVILEGE_TYPE ON AICO_FLG=PRIV_CD\r\n"
			+ "where INV_NO=:invoiceNo AND (:carFamily IS NULL OR CF_CD = :carFamily) AND (:partNo IS NULL OR PART_NO = :partNo)", nativeQuery = true)
	List<Object[]> getinvpartdata(@Param("invoiceNo") String invoiceNo, @Param("carFamily") String carFamily,
			@Param("partNo") String partNo);

	List<SeperateInvDetailsEntity> findByInvNoEquals(String invNo);
}
