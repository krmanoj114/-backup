package com.tpex.invoice.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.tpex.dto.InvGenWorkPlanMstDTO;
import com.tpex.dto.NoemHaisenDtlsEntityDTO;
import com.tpex.dto.NoemRenbanSetupMstDTO;
import com.tpex.dto.ShippingContResultDTO;
import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.entity.NoemCbMstEntity;
import com.tpex.entity.NoemHaisenDtlsIdEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.OemPortMstEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.ShippingContSearchInputDTO;

public interface InvGenWorkPlanMstService {

	boolean saveInvoice(List<InvGenWorkPlanMstDTO> listInvGenWorkPlanMstDTO) throws Exception;

	Page<InvGenWorkPlanMstEntity> fetchInvoiceDetails(int pageNo, int pageSize, String invoiceFromDate,
			String invoiceToDate, String etd1fromDate, String etd1ToDate, List<String> dstCode)
			throws InvalidInputParametersException, ParseException;

	/*Object downloadInvoiceGenPlan(String invoiceFromDate, String invoiceToDate, String etd1fromDate, String etd1ToDate,
			List<String> dstCode, String fileFormat, String loginUserId) throws Exception;*/

	List<OemFnlDstMstEntity> destinationCodeList();

	List<NoemCbMstEntity> getBrokerDetails();

	List<OemPortMstEntity> getPortOfLoadingAndDischargeDetails();


	List<NoemRenbanSetupMstDTO> fetchRenbanCodesByContDstCd(String contDstCode);

	List<ShippingContResultDTO> shippingResults(ShippingContSearchInputDTO shippingContSearchInputDTO) throws Exception;

	Map<String, Object> generateHaisenNo(NoemHaisenDtlsEntityDTO noemHaisenDtlsEntity) throws Exception;
	
	Map<String, Integer> getCountReportDataToDownload(String invoiceFromDate, String invoiceToDate, String etd1fromDate,
			String etd1ToDate, List<String> dstCode,  String createdBy) throws ParseException;
	
	Object downloadReportOffline(String issueInvoiceFromDate, String issueInvoiceToDate, String etd1fromDate,
			String etd1ToDate, List<String> contDest, String string, String string2, int savedReportId)  throws Exception;

	Object downloadReportOnline(String issueInvoiceFromDate, String issueInvoiceToDate, String etd1fromDate,
			String etd1ToDate, List<String> contDest, String string, String string2)  throws Exception;

}
