package com.tpex.admin.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.entity.OemProcessCtrlIdEntity;
@Repository
public interface OemProcessCtrlRepository extends JpaRepository<OemProcessCtrlEntity, OemProcessCtrlIdEntity> {

	@Query(value = "SELECT * FROM  (SELECT * FROM TB_M_PROCESS_CONTROLER WHERE USER_ID =:userId  ORDER BY SUBMIT_TIME DESC ) T LIMIT 5", nativeQuery = true)
	List<OemProcessCtrlEntity> findByUserId(@Param("userId") String userId);


	List<OemProcessCtrlEntity> findByIdBatchIdAndIdProcessControlId(String batchId, int processControlId);

	@Query(value = "select count(process_ctrl_id)+1 from TB_M_PROCESS_CONTROLER", nativeQuery = true)
	int getIdOfProcessControl();

	List<OemProcessCtrlEntity> findAllBySubmitTimeBetweenAndProgramId(Timestamp fromSubmitDatetime, 
			Timestamp endSubmitDatetime, String programId);

	List<OemProcessCtrlEntity> findAllBySubmitTimeBetweenAndSystemName(Timestamp fromSubmitDatetime, 
			Timestamp endSubmitDatetime, String systemName);

	List<OemProcessCtrlEntity> findAllBySubmitTimeBetweenAndProgramIdAndSystemName(Timestamp fromSubmitDatetime, Timestamp endSubmitDatetime, String programId, String systemName);


	List<OemProcessCtrlEntity> findAllBySubmitTimeBetween(Timestamp fromSubmitDatetime,
			Timestamp endSubmitDatetime);

	@Query(value = "select ERROR_FILE_PATH from  tb_m_process_controler where PROCESS_CTRL_ID=:processControlId", nativeQuery = true)
	String findErrorFilePath(@Param("processControlId") String processControlId);

	OemProcessCtrlEntity findByIdProcessControlIdAndIdBatchId(int processControlId,String batchId);

	@Query(value = "select * from  tb_m_process_controler where user_id=:userId and BATCH_ID=:batchId order by PROCESS_CTRL_ID desc LIMIT 1", nativeQuery = true)
	OemProcessCtrlEntity findProcessControl(@Param("userId") String userId,@Param("batchId") String batchId);
}