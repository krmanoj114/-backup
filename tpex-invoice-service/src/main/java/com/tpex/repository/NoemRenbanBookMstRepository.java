package com.tpex.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.NoemRenbanBookDtlEntity;
import com.tpex.entity.NoemRenbanBookMstEntity;
import com.tpex.entity.NoemRenbanBookMstIdEntity;

@Repository
public interface NoemRenbanBookMstRepository extends JpaRepository<NoemRenbanBookMstEntity, NoemRenbanBookMstIdEntity>{

	/*@Query(value="SELECT * FROM NOEM_RENBAN_BOOK_MST " +
			" WHERE  NRBM_CONT_VAN_MTH = :contVanMnth " +
			" AND    NRBM_CONT_DST_CD  = :contDstcd " +
			" AND    NRBM_ETD1         = :etd1 " +
			" AND    NRBM_SHP_NM_1     = :shpNm1 ",nativeQuery=true)
	NoemRenbanBookMstEntity getRenbanBookMst(@Param("contVanMnth") String contVanMnth,@Param("contDstcd")  String contDstcd,@Param("etd1")  Date etd1,@Param("shpNm1")  String shpNm1);
*/

	@Query(value="SELECT *" + " FROM NOEM_RENBAN_BOOK_MST " +
	        "WHERE  NRBM_CONT_VAN_MTH = :contVanMnth " +
	        " AND    NRBM_CONT_DST_CD  = :contDstcd " +
	        " AND    NRBM_ETD1         = :etd1 " +
	        " AND    NRBM_SHP_NM_1     = :shpNm1",nativeQuery=true)
	NoemRenbanBookMstEntity getRenbanBookMst(@Param("contVanMnth") String contVanMnth,@Param("contDstcd")  String contDstcd,@Param("etd1")  Date etd1,@Param("shpNm1")  String shpNm1);


}
