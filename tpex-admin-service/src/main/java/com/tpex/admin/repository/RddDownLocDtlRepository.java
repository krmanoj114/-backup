package com.tpex.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.RddDownLocDtlEntity;

@Repository
public interface RddDownLocDtlRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {

	@Query(value = "SELECT DISTINCT REPORT_NAME FROM TB_R_REPORT_DOWN_D ORDER BY REPORT_NAME", nativeQuery = true)
	List<String> getReportName();

	@Query(value = "SELECT DISTINCT STATUS FROM TB_R_REPORT_DOWN_D ORDER BY STATUS", nativeQuery = true)
	List<String> getStatus();

	@Query(value = "SELECT REPORT_ID, REPORT_NAME, STATUS, DOWN_LOC, CREATE_BY, CREATE_DATE, UPDATE_BY, UPDATE_DATE, CMP_CD FROM TB_R_REPORT_DOWN_D RDD WHERE  RDD.CREATE_BY = :createBy AND RDD.REPORT_NAME =:reportName AND RDD.STATUS =:status AND convert(RDD.create_date,date) = convert(:createDate,date)", nativeQuery = true)
	List<RddDownLocDtlEntity> findByStatusAndCreateDateAndReportNameAndCreateBy(@Param("status") String status,
			@Param("createDate") String createDate, @Param("reportName") String reportName,
			@Param("createBy") String createBy);

	@Query(value = "SELECT REPORT_ID, REPORT_NAME, STATUS, DOWN_LOC, CREATE_BY, CREATE_DATE, UPDATE_BY, UPDATE_DATE, CMP_CD  FROM TB_R_REPORT_DOWN_D RDD WHERE convert(RDD.create_date,date) = convert(:createDate,date) AND create_by = :createBy", nativeQuery = true)
	List<RddDownLocDtlEntity> findByCreateDateAndCreateBy(@Param("createDate") String createDate,
			@Param("createBy") String createBy);

	@Query(value = "SELECT REPORT_ID, REPORT_NAME, STATUS, DOWN_LOC, CREATE_BY, CREATE_DATE, UPDATE_BY, UPDATE_DATE, CMP_CD FROM TB_R_REPORT_DOWN_D RDD WHERE convert(RDD.create_date,date) = convert(:createDate,date)  AND create_by = :createBy AND (report_name =:reportName OR status =:status)", nativeQuery = true)
	List<RddDownLocDtlEntity> findByStatusOrReportNameAndCreateByAndCreateDate(@Param("status") String status,
			@Param("createDate") String createDate, @Param("reportName") String reportName,
			@Param("createBy") String createBy);

	RddDownLocDtlEntity findTopByReportNameAndCreateByOrderByReportIdDesc(String reportName, String createBy);

	RddDownLocDtlEntity findByReportId(int reportId);
}