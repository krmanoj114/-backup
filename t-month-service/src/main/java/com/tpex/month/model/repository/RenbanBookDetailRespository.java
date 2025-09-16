package com.tpex.month.model.repository;

import java.time.LocalDate;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.month.model.entity.RenbanBookDetailEntity;
import com.tpex.month.model.entity.RenbanBookDetailId;

public interface RenbanBookDetailRespository extends JpaRepository<RenbanBookDetailEntity, RenbanBookDetailId> {

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM TB_R_MTH_RENBAN_BOOKING_D "
			+ " WHERE CONT_VAN_MTH = ?1 "
			+ " AND CONT_DST_CD = ?2 "
			+ " AND ETD1 = ?3 "
			+ " AND SHP_NM_1 = ?4 "
			+ " AND GROUP_ID = ?5 "
			, nativeQuery = true)
	public void deleteAllUnderGroupId(String vanningMonth, String destinationCode, LocalDate etd1, String shippingCompany,
			String groupId);
	
}
