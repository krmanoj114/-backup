package com.tpex.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.OemCnshIdMstEntity;
import com.tpex.entity.OemCnshMst;

@Repository
public interface OemCnshMstRepository extends JpaRepository<OemCnshMst, OemCnshIdMstEntity> {


	@Query(value = "SELECT SC_AUTH_EMP1,SC_AUTH_EMP2,SC_AUTH_EMP3,NAME,CD,ADD_1,ADD_2,ADD_3,ADD_4 FROM TB_M_CNSG \r\n"
			+ "WHERE COMPANY = (SELECT PARA_VAL FROM TB_M_PARAMETER WHERE PARA_CD = 'OTHER_CMP') AND CD =:indBuyer AND INV_ADD_FLG ='Y'", nativeQuery = true)
	List<Object[]> getAuthEmp(@Param("indBuyer") String indBuyer);


	@Query(value = "select cd,name from TB_M_CNSG where cd=:dischargePortCode", nativeQuery = true)
	List<Tuple> getCnsgCd(@Param("dischargePortCode") String dischargePortCode);

	@Query(value = "select ADD_1,ADD_2,ADD_3,ADD_4 from TB_M_CNSG where cd=:dischargePortCode", nativeQuery = true)
	Tuple getCnsgCode(@Param("dischargePortCode") String dischargePortCode);

	List<OemCnshMst> findAllByOrderByIdCmpCdAsc();

	List<OemCnshMst> findAllByOrderByIdCmpBranchAsc();

	OemCnshMst findTopByIdCmpBranch(String cmpbranch);

	List<OemCnshMst> findAllByIdCmpCdOrderByIdCmpBranchAsc(String cmpCd);

	@Query(value = "SELECT NAME,ADD_1,ADD_2, ADD_3, ADD_4,CD FROM TB_M_CNSG WHERE CD =  ( SELECT BUYER  FROM TB_R_INV_INVOICE_H WHERE INV_NO=:envNo ) ORDER BY BRANCH;", nativeQuery = true)
	List<Object[]> getCnsgData(@Param("envNo") String envNo);
	
	OemCnshMst findTopByCmpName(String name);
}
