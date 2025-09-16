package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.ManualInvGenEntity;

@Repository
public interface ManualInvGenRepository extends JpaRepository<ManualInvGenEntity, String> {

	@Query(value = "SELECT ROW_NUMBER() OVER ( ORDER BY NVDC_ETD_1 , NVDC_CONT_SNO , NVDC_PLN_VAN_END_DT ) SR_NO,  \r\n"
			+ "NVDC_ETD_1, NVDC_CONT_SNO,CONT_SIZE,\r\n"
			+ "case when DG_FLG='Y' then 'YES' else 'NO' end DG_FLG,VAN_DT,INV_DATA_CAME,\r\n"
			+ "case when INV_GEN_FLG='Y' then 'YES' else 'NO' end INV_GEN_FLG,\r\n"
			+ "Van_Plant,Inv_Type\r\n"
			+ "from  \r\n"
			+ "(\r\n"
			+ "SELECT  A.ETD_1 as NVDC_ETD_1, A.CONT_SNO as NVDC_CONT_SNO,  \r\n"
			+ "A.CONT_SIZE,  \r\n"
			+ "case   when sum(DG_FLG) > 0 then 'Y' when sum(DG_FLG) = 0 then 'N'else 'N/A'end DG_FLG,  \r\n"
			+ "A.PLN_VAN_END_DT AS VAN_DT,\r\n"
			+ "case when B.SNO=NULL then 'No'else 'Yes'END AS INV_DATA_CAME, \r\n"
			+ "case when ifnull(B.INV_GEN,0)=0 then 'N' else'Yes' END AS INV_GEN_FLG,\r\n"
			+ "A.PLN_VAN_END_DT NVDC_PLN_VAN_END_DT,B.Van_Plant,B.Inv_Type  \r\n"
			+ "FROM TB_R_DLY_VPR_CONTAINER A,\r\n"
			+ "(SELECT distinct concat(c.VAN_PLNT_CD,'-',VP.plant_name) as Van_Plant,\r\n"
			+ "case when m.lot_pttrn='P' then 'PXP' else'LOT' end as Inv_Type,C.ETD_FEEDER,C.ETD_OCEAN,M.CONT_DST_CD DST_CD, M.CONT_SNO SNO,  \r\n"
			+ "C.CONT_SIZE, p.PART_NO,case when P.DNG_PART_IMO=null then \r\n"
			+ "case when P.DNG_PART_DST=NULL then '0'else'1'end else 1 end AS DG_FLG, MIN(case when M.INV_FLG =null then 0\r\n"
			+ "when M.INV_FLG ='N' then 0 else 1 end) INV_GEN  \r\n"
			+ "FROM TB_R_INV_VPR_CONTAINER C, TB_R_INV_VPR_MODULE M ,TB_R_INV_VPR_PART  P, tb_m_plant VP\r\n"
			+ "WHERE C.CONT_DST_CD = :destCode \r\n"
			+ "AND (CASE WHEN :invoiceType is null THEN\r\n"
			+ "m.lot_pttrn in ('P','L')\r\n"
			+ "ELSE  m.lot_pttrn = :invoiceType \r\n"
			+ "END)"
			+ " -- AND coalesce (C.ETD_FEEDER,C.ETD_OCEAN) = :etdDate  \r\n"
			+ "AND C.CMP_CD=:cmpCd \r\n"
			+ "AND c.VAN_PLNT_CD=VP.PLANT_CD \r\n"
			+ "AND C.CONT_DST_CD = M.CONT_DST_CD  \r\n"
			+ "AND C.CMP_CD = M.CMP_CD \r\n"
			+ "AND C.CONT_SNO                         = M.CONT_SNO  \r\n"
			+ "AND P.CONT_DST_CD                      = M.CONT_DST_CD  \r\n"
			+ "AND P.CMP_CD = M.CMP_CD \r\n"
			+ "AND P.CONT_SNO                         = M.CONT_SNO  \r\n"
			+ "AND P.MOD_DST_CD                       = M.MOD_DST_CD  \r\n"
			+ "AND P.LOT_MOD_NO                       = M.LOT_MOD_NO  \r\n"
			+ "AND P.CASE_NO                          = M.CASE_NO  \r\n"
			+ "AND P.VAN_MTH                          = M.VAN_MTH  \r\n"
			+ "GROUP BY c.VAN_PLNT_CD,case when m.lot_pttrn='P' then 'PXP' else'LOT' end,C.ETD_FEEDER,C.ETD_OCEAN,M.CONT_DST_CD,  M.CONT_SNO,  C.CONT_SIZE, P.PART_NO,\r\n"
			+ "case when P.DNG_PART_IMO=null then case when P.DNG_PART_DST=NULL then '0'else'1'end else 1 end) B \r\n"
			+ "WHERE A.CONT_DST_CD = coalesce(:destCode,A.CONT_DST_CD)  \r\n"
			+ "AND A.ETD_1        = :etdDate  \r\n"
			+ "AND A.CONT_DST_CD   = B.DST_CD\r\n"
			+ "AND A.CONT_SNO      = B.SNO\r\n"
			+ "group by A.ETD_1,A.CONT_SNO,  \r\n"
			+ "A.CONT_SIZE, A.PLN_VAN_END_DT,  \r\n"
			+ "case when B.SNO=NULL then 'No'else 'Yes'END, \r\n"
			+ "case when ifnull(B.INV_GEN,0)=0 then 'N' else'Yes' END ,   \r\n"
			+ "A.PLN_VAN_END_DT ,B.Van_Plant,B.Inv_Type  \r\n"
			+ ")as t ", nativeQuery = true)
	List<Object[]> getManualInvoiceData(@Param("etdDate") String etdDate, @Param("destCode") String destCode,
			@Param("invoiceType") String invoiceType, @Param("cmpCd") String cmpCd);
}
