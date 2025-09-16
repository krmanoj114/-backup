package com.tpex.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.tpex.entity.NoemRenbanSetupMstEntity;
import com.tpex.entity.NoemRenbanSetupMstIdEntity;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface NoemRenbanSetupMstRepository extends JpaRepository<NoemRenbanSetupMstEntity, NoemRenbanSetupMstIdEntity> {
	
	//Find by container destination code
	List<NoemRenbanSetupMstEntity> findByIdContDstCdOrderByIdContGrpCdAsc(String contDstCd);

	List<NoemRenbanSetupMstEntity> findByIdContDstCdOrderByIdGroupId(String destCode);

	
	
	@Modifying
	void deleteByIdContDstCdAndIdEffFromDtAndEffToDt(String destCode, Date effFrom, Date effTo);
	
	@Modifying
	@Query(value = "update TB_M_MTH_RENBAN_SETUP set FOLDER_NAME=:folderName, EFF_TO_DT=:effTo, UPD_BY=:userId where CONT_DST_CD=:contDstCd AND EFF_FROM_DT=:effFrom AND GROUP_ID=:groupId AND CONT_GRP_CD=:renbanGroupCode", nativeQuery = true)
	int updateRenbanCodeMaster(@Param("contDstCd") String contDstCd, @Param("renbanGroupCode") String renbanGroupCode, @Param("effFrom") Date effFrom,  @Param("effTo") Date effTo,
			@Param("groupId") String groupId, @Param("folderName") String folderName,@Param("userId") String userId);

	boolean existsByIdContDstCdAndIdEffFromDtAndEffToDt(String destCode, Date effFrom, Date effTo);

		 
}
