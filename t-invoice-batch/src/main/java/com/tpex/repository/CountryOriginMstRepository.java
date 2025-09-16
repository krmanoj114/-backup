package com.tpex.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.CountryOriginMstEntity;

@Repository
public interface CountryOriginMstRepository extends JpaRepository<CountryOriginMstEntity, String>{

	List<CountryOriginMstEntity> findByPartNumberAndEffectiveFromdateAndEffectivetodate(String partNo, Date effFromDate,
			Date effToDate);

	List<CountryOriginMstEntity> findByPartNumber(String partNo);

	
}
