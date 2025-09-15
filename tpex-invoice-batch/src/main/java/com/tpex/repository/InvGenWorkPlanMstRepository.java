package com.tpex.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.entity.InvGenWorkPlanMstIdEntity;
@Repository
public interface InvGenWorkPlanMstRepository extends JpaRepository<InvGenWorkPlanMstEntity, InvGenWorkPlanMstIdEntity> {


	Page<InvGenWorkPlanMstEntity> findByIssueInvoiceDateBetweenAndEtd1Between(
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2,
			Date dateFromStringDateFormateforInvoiceDate3, Date dateFromStringDateFormateforInvoiceDate4, Pageable paging);

	Page<InvGenWorkPlanMstEntity> findByIssueInvoiceDateBetween(
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2, 
			Pageable paging);

	Page<InvGenWorkPlanMstEntity> findByEtd1Between(Date dateFromStringDateFormateforInvoiceDate,
			Date dateFromStringDateFormateforInvoiceDate2, Pageable paging);

	Page<InvGenWorkPlanMstEntity> findByIssueInvoiceDateBetweenAndEtd1BetweenAndIdContDestIn(
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2,
			Date dateFromStringDateFormateforInvoiceDate3, Date dateFromStringDateFormateforInvoiceDate4,
			List<String> dstCode, Pageable paging);

	Page<InvGenWorkPlanMstEntity> findByIssueInvoiceDateBetweenAndIdContDestIn(
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2, List<String> dstCode,
			Pageable paging);

	Page<InvGenWorkPlanMstEntity> findByEtd1BetweenAndIdContDestIn(Date dateFromStringDateFormateforInvoiceDate,
			Date dateFromStringDateFormateforInvoiceDate2, List<String> dstCode, Pageable paging);



	List<InvGenWorkPlanMstEntity> findByIssueInvoiceDateBetweenAndEtd1BetweenOrderByIssueInvoiceDateAscEtd1AscIdContDestAscLinerAscBrokerAscIdRenbanCodeAsc(
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2,
			Date dateFromStringDateFormateforInvoiceDate3, Date dateFromStringDateFormateforInvoiceDate4);

	List<InvGenWorkPlanMstEntity> findByIssueInvoiceDateBetweenOrderByIssueInvoiceDateAscEtd1AscIdContDestAscLinerAscBrokerAscIdRenbanCodeAsc(
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2);


	List<InvGenWorkPlanMstEntity> findByEtd1BetweenOrderByIssueInvoiceDateAscEtd1AscIdContDestAscLinerAscBrokerAscIdRenbanCodeAsc(Date dateFromStringDateFormateforInvoiceDate,
			Date dateFromStringDateFormateforInvoiceDate2);

	List<InvGenWorkPlanMstEntity> findByIssueInvoiceDateBetweenAndEtd1BetweenAndIdContDestInOrderByIssueInvoiceDateAscEtd1AscIdContDestAscLinerAscBrokerAscIdRenbanCodeAsc(
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2,
			Date dateFromStringDateFormateforInvoiceDate3, Date dateFromStringDateFormateforInvoiceDate4,
			List<String> dstCode);

	List<InvGenWorkPlanMstEntity> findByIssueInvoiceDateBetweenAndIdContDestInOrderByIssueInvoiceDateAscEtd1AscIdContDestAscLinerAscBrokerAscIdRenbanCodeAsc(
			Date dateFromStringDateFormateforInvoiceDate, Date dateFromStringDateFormateforInvoiceDate2,
			List<String> dstCode);

	List<InvGenWorkPlanMstEntity> findByEtd1BetweenAndIdContDestInOrderByIssueInvoiceDateAscEtd1AscIdContDestAscLinerAscBrokerAscIdRenbanCodeAsc(Date dateFromStringDateFormateforInvoiceDate,
			Date dateFromStringDateFormateforInvoiceDate2, List<String> dstCode);

	@Query(value = "SELECT count(*) FROM TB_M_INV_WORK_PLAN WHERE ISSUE_INVOICE_DATE BETWEEN :invoiceFromDate AND :invoiceToDate AND ETD1 BETWEEN :etd1fromDate AND :etd1ToDate AND CONT_DEST IN :dstCode", nativeQuery = true)
	int findCountByIssueInvoiceDateBetweenAndEtd1BetweenAndIdContDestIn(@Param("invoiceFromDate") Date invoiceFromDate, @Param("invoiceToDate") Date invoiceToDate, @Param("etd1fromDate") Date etd1fromDate, @Param("etd1ToDate") Date etd1ToDate, @Param("dstCode") List<String> dstCode);

	@Query(value = "SELECT count(*) FROM TB_M_INV_WORK_PLAN WHERE ISSUE_INVOICE_DATE BETWEEN :invoiceFromDate AND :invoiceToDate AND ETD1 BETWEEN :etd1fromDate AND :etd1ToDate", nativeQuery = true)
	int findCountByIssueInvDateAndEtd1(@Param("invoiceFromDate") Date invoiceFromDate, @Param("invoiceToDate") Date invoiceToDate, @Param("etd1fromDate") Date etd1fromDate, @Param("etd1ToDate") Date etd1ToDate);

	@Query(value = "SELECT count(*) FROM TB_M_INV_WORK_PLAN WHERE ISSUE_INVOICE_DATE BETWEEN :invoiceFromDate AND :invoiceToDate", nativeQuery = true)
	int findCountByIssueInvDate(@Param("invoiceFromDate") Date invoiceFromDate, @Param("invoiceToDate") Date invoiceToDate);

	@Query(value = "SELECT count(*) FROM TB_M_INV_WORK_PLAN WHERE ISSUE_INVOICE_DATE BETWEEN :invoiceFromDate AND :invoiceToDate AND CONT_DEST IN :dstCode", nativeQuery = true)
	int findCountByIssueInvoiceDateBetweenAndIdContDestIn(@Param("invoiceFromDate") Date invoiceFromDate, @Param("invoiceToDate") Date invoiceToDate, @Param("dstCode") List<String> dstCode);

	@Query(value = "SELECT count(*) FROM TB_M_INV_WORK_PLAN WHERE ETD1 BETWEEN :etd1fromDate AND :etd1ToDate AND CONT_DEST IN :dstCode", nativeQuery = true)
	int findCountByEtd1BetweenAndIdContDestIn(@Param("etd1fromDate") Date etd1fromDate, @Param("etd1ToDate") Date etd1ToDate, @Param("dstCode") List<String> dstCode);

	@Query(value = "SELECT count(*) FROM TB_M_INV_WORK_PLAN WHERE ETD1 BETWEEN :etd1fromDate AND :etd1ToDate", nativeQuery = true)
	int findCountByEtd1Between(@Param("etd1fromDate") Date invoiceFromDate, @Param("etd1ToDate") Date etd1ToDate);

	@Query(value="select count(*) from TB_M_INV_WORK_PLAN where ORIGINAL_ETD=:originalEtd and CONT_DEST=:contDest and RENBAN_CODE=:renbanCode",nativeQuery = true)
	long getCountUsingKey(@Param("originalEtd") Date originalEtd,@Param("contDest") String contDest,@Param("renbanCode") String renbanCode);

}