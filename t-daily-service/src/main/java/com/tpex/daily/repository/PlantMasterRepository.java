package com.tpex.daily.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.daily.entity.TbMPlant;

@Repository
public interface PlantMasterRepository extends JpaRepository<TbMPlant, String> {
	
	
	@Query(value ="SELECT      \r\n"
			+ "               NVDC.ETD_1, \r\n"
			+ "                NRBM.BOOK_NO,\r\n"
			+ " NVDC.CONT_SNO,\r\n"
			+ "                NICM.ISO_CONT_NO,\r\n"
			+ "                CASE WHEN NICM.CONT_TYP = NULL THEN 'S' ELSE NICM.CONT_TYP END CONT_TYP,\r\n"
			+ "                NICM.Seal_No,\r\n"
			+ " NICM.Tare_Weight,\r\n"
			+ "                CASE WHEN NVDC.CONT_SIZE = '20' THEN  '20 Ft.' ELSE '40 Ft.' END CONT_SIZE,\r\n"
			+ "                NRBM.SHP_NM_1, \r\n"
			+ "                CASE WHEN  NVDC.ACT_VAN_DT IS NULL THEN 'Not Complete' ELSE 'Completed' END Vanning_Status\r\n"
			+ "         FROM   TB_M_ISO_CONTAINER NICM LEFT OUTER JOIN  TB_R_DLY_VPR_CONTAINER NVDC   \r\n"
			+ "         ON  NVDC.PKG_MTH           = NICM.CONT_VAN_MTH  \r\n"
			+ "         AND    NVDC.CONT_DST_CD       = NICM.CONT_DST_CD     \r\n"
			+ "         AND    NVDC.ETD_1             = NICM.ETD1    \r\n"
			+ "         AND    NVDC.CONT_SNO          = NICM.CONT_SNO,\r\n"
			+ "                TB_R_MTH_RENBAN_BOOKING_H NRBM,\r\n"
			+ "                TB_R_MTH_RENBAN_BOOKING_D NRBD,\r\n"
			+ "                TB_M_SHIP_CMP SCM             \r\n"
			+ "         WHERE \r\n"
			+ "                 NICM.CONT_VAN_MTH = :vanningMonth \r\n"
			+ "             AND NVDC.VAN_PLNT_CD = :vanningPlant \r\n"
			+ "             AND NVDC.CONT_DST_CD = :containerDestination \r\n"
			+ "             AND (:etd IS null OR NVDC.ETD_1 = :etd ) \r\n"
			+ "             AND (COALESCE(null, :continerRanbanNo) IS NULL OR LTRIM(RTRIM(NVDC.CONT_SNO)) = :continerRanbanNo )  \r\n"
			+ "               AND    (('Y' = IFNULL('N','N') AND VAN_PLNT_CD IN \r\n"
			+ "        (SELECT PM.PLANT_CD FROM TB_M_PLANT PM, TB_M_USER_MASTER UM  WHERE IFNULL(PM.PLANT_NAME,'N') = 'Y' \r\n"
			+ "         AND UM.DEPT_ID = PM.PLANT_NAME\r\n"
			+ "         AND UM.USER_ID = 'NIITTMT' \r\n"
			+ "         )) \r\n"
			+ "         OR ('Y' <> IFNULL('N','N') AND VAN_PLNT_CD IN (SELECT PM.PLANT_CD FROM TB_M_PLANT PM))) \r\n"
			+ "         AND    NVDC.PKG_MTH           = NRBD.CONT_VAN_MTH   \r\n"
			+ "         AND    NVDC.CONT_GRP_CD       = NRBD.CONT_GRP_CD  \r\n"
			+ "         AND    NVDC.ETD_1             = NRBD.ETD1  \r\n"
			+ "         AND    NVDC.CONT_DST_CD       = NRBD.CONT_DST_CD \r\n"
			+ "         AND    NVDC.SHP_NM_1          = NRBD.SHP_NM_1  \r\n"
			+ "         AND    NRBM.CONT_VAN_MTH      = NRBD.CONT_VAN_MTH  \r\n"
			+ "         AND    NRBM.CONT_DST_CD       = NRBD.CONT_DST_CD    \r\n"
			+ "         AND    NRBM.ETD1              = NRBD.ETD1 \r\n"
			+ "         AND    NRBM.SHP_NM_1          = NRBD.SHP_NM_1 \r\n"
			+ "         AND    NRBM.GROUP_ID          = NRBD.GROUP_ID \r\n"
			+ "         AND    SCM.CD                  = NRBM.SHP_NM_1 \r\n"
			+ "        AND    NVDC.PLN_VAN_END_DT IS NOT NULL \r\n"
			+ "         AND    NVDC.ETD_1 IS NOT NULL            \r\n"
			+ "         ORDER BY  NVDC.ETD_1,\r\n"
			+ "             NRBM.BOOK_NO,\r\n"
			+ "              NVDC.CONT_SNO; ", nativeQuery = true)
	List<String[]> getConstainerDetails(@Param("vanningMonth") String vanningMonth, @Param("vanningPlant") String vanningPlant,@Param("containerDestination") String containerDestination,@Param("etd") String etd, @Param("continerRanbanNo") String continerRanbanNo);
	
	

}
