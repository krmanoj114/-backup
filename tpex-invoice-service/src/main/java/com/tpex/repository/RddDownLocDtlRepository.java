package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.RddDownLocDtlEntity;

@Repository
public interface RddDownLocDtlRepository extends JpaRepository<RddDownLocDtlEntity, Integer> {

	@Query(value = "SELECT DISTINCT REPORT_NAME FROM TB_R_REPORT_DOWN_D ORDER BY REPORT_NAME", nativeQuery = true)
	List<String> getReportName();

	@Query(value = "SELECT DISTINCT STATUS FROM TB_R_REPORT_DOWN_D ORDER BY STATUS", nativeQuery = true)
	List<String> getStatus();

	@Query(value = "SELECT * FROM TB_R_REPORT_DOWN_D RDD WHERE DATE(RDD.create_date) = STR_TO_DATE(:requestDate,'%d/%m/%Y') AND create_by = :createBy AND report_name =:reportName AND status =:status", nativeQuery = true)
	List<RddDownLocDtlEntity> findByStatusAndCreateDateAndReportNameAndCreateBy(@Param("status") String status,
			@Param("requestDate") String requestDate, @Param("reportName") String reportName,
			@Param("createBy") String createBy);

	@Query(value = "SELECT *  FROM TB_R_REPORT_DOWN_D RDD WHERE DATE(RDD.create_date) = STR_TO_DATE(:requestDate,'%d/%m/%Y') AND RDD.create_by = :createBy", nativeQuery = true)
	List<RddDownLocDtlEntity> findByCreateDateAndCreateBy(@Param("requestDate") String requestDate,
			@Param("createBy") String createBy);

	@Query(value = "SELECT * FROM TB_R_REPORT_DOWN_D RDD WHERE DATE(RDD.create_date) = STR_TO_DATE(:requestDate,'%d/%m/%Y')  AND create_by = :createBy AND (report_name =:reportName OR status =:status)", nativeQuery = true)
	List<RddDownLocDtlEntity> findByStatusOrReportNameAndCreateByAndCreateDate(@Param("status") String status,
			@Param("requestDate") String requestDate, @Param("reportName") String reportName,
			@Param("createBy") String createBy);

	RddDownLocDtlEntity findTopByReportNameAndCreateByOrderByReportIdDesc(String reportName, String createBy);

	RddDownLocDtlEntity findByReportId(int reportId);
}