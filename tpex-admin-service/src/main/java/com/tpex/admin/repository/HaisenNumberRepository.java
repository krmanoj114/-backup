package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.HaisenNoEntity;
@Repository
	public interface HaisenNumberRepository extends JpaRepository<HaisenNoEntity, String>{
	
	
	@Query(value = "select HAISEN_NO from  tb_r_haisen_d where HAISEN_NO=:haisenNo", nativeQuery = true)
	 List <HaisenNoEntity>findHaisenNoById (@Param("haisenNo") String haisenNo);

}
