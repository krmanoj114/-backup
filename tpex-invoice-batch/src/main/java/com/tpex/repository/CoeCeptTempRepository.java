package com.tpex.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpex.entity.CoeCeptTempEntity;
import com.tpex.entity.CoeCeptId;

public interface CoeCeptTempRepository extends JpaRepository<CoeCeptTempEntity, CoeCeptId> {

	public List<CoeCeptTempEntity> findByCfCdAndSeriesAndPartNoAndImpCmpAndExpCmpAndPartEffDt(String cfCd,
			String series, String partNo, String impCmp, String expCmp, LocalDate partEffDt);

}
