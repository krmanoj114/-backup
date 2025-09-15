package com.tpex.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.NoemRenbanBookDtlEntity;
import com.tpex.entity.NoemRenbanBookDtlIdEntity;

@Repository
public interface NoemRenbanBookDtlRepository extends JpaRepository<NoemRenbanBookDtlEntity, NoemRenbanBookDtlIdEntity>{
    
	@Query(value="SELECT *" + " FROM TB_R_MTH_RENBAN_BOOKING_D " +
	        "WHERE  NRBD_CONT_VAN_MTH = :contVanMnth " +
	        " AND    CONT_DST_CD  = :contDstcd " +
	        " AND    ETD1         = :etd1 " +
	        " AND    SHP_NM_1     = :shpNm1", nativeQuery = true)
	NoemRenbanBookDtlEntity getRenbanBookDtl(@Param("contVanMnth") String contVanMnth,@Param("contDstcd")  String contDstcd,@Param("etd1")  Date etd1,@Param("shpNm1")  String shpNm1);

	List<NoemRenbanBookDtlEntity> findByIdContDestCdAndIdEtd1(String destCode, Date effFrom);


}
