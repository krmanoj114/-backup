package com.tpex.invoice.service;

import java.io.FileNotFoundException;
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
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.OemPortMstEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.ShippingContSearchInputDTO;

import net.sf.jasperreports.engine.JRException;

@SuppressWarnings("squid:S107")
public interface InvGenWorkPlanMstService {

	boolean saveInvoice(List<InvGenWorkPlanMstDTO> listInvGenWorkPlanMstDTO);

	Page<InvGenWorkPlanMstEntity> fetchInvoiceDetails(int pageNo, int pageSize, String invoiceFromDate,
			String invoiceToDate, String etd1fromDate, String etd1ToDate, List<String> dstCode)
			throws InvalidInputParametersException, ParseException;

	List<OemFnlDstMstEntity> destinationCodeList();

	List<NoemCbMstEntity> getBrokerDetails();

	List<OemPortMstEntity> getPortOfLoadingAndDischargeDetails();

	List<NoemRenbanSetupMstDTO> fetchRenbanCodesByContDstCd(String contDstCode);

	List<ShippingContResultDTO> shippingResults(ShippingContSearchInputDTO shippingContSearchInputDTO);

	Map<String, Object> generateHaisenNo(NoemHaisenDtlsEntityDTO noemHaisenDtlsEntity);

	Map<String, Integer> getCountReportDataToDownload(String invoiceFromDate, String invoiceToDate, String etd1fromDate,
			String etd1ToDate, List<String> dstCode, String createdBy) throws ParseException;

	Object downloadReportOffline(String issueInvoiceFromDate, String issueInvoiceToDate, String etd1fromDate,
			String etd1ToDate, List<String> contDest, String string, String string2, int savedReportId);

	Object downloadReportOnline(String issueInvoiceFromDate, String issueInvoiceToDate, String etd1fromDate,
			String etd1ToDate, List<String> contDest, String string, String string2) throws FileNotFoundException, JRException;

}
