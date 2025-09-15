package com.tpex.invoice.serviceimpl;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.InvoiceCoverPageResponseDto;
import com.tpex.invoice.service.InvoiceCoverPageService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvPackingListRepository;
import com.tpex.repository.InvoiceCoverPageRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

import net.sf.jasperreports.engine.JRException;

@Service
@Transactional
public class InvoiceCoverPageServiceImpl implements InvoiceCoverPageService {

	@Autowired
	InvoiceCoverPageRepository invoiceCoverPageRepository;

	@Autowired
	InvPackingListRepository invPackingListRepository;

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Override
	public Object getInvoiceCoverPageRptDownload(String invNumber, String fileTemplateName, String reportFormat)
			throws FileNotFoundException, JRException, NullPointerException {

		Object response = null;
		Map<String, Object> parameters = new HashMap<>();

		parameters.put(ConstantUtils.INVOICE_NO, invNumber);

		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,
				tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue());
		config.put(ConstantUtils.REPORT_FORMAT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT).getValue());
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_SIZE_LIMIT).getValue());
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		String fileFormat = StringUtils.isNotBlank(reportFormat) && ConstantUtils.XLSX.equalsIgnoreCase(reportFormat)
				? reportFormat
				: ConstantUtils.PDF;
		String fileName = invNumber + "_COV." + fileFormat;

		List<InvoiceCoverPageResponseDto> invoiceCoverPageResponseDtoList = getInvoiceCoverPageData(invNumber);

		if (ConstantUtils.XLSX.equals(fileFormat)) {
			response = jasperReportService.getJasperReportDownloadOnline(invoiceCoverPageResponseDtoList, fileFormat,
					fileTemplateName, fileName, parameters, config);

		} else {
			response = jasperReportService.getJasperReportDownloadOfflineV1(invoiceCoverPageResponseDtoList, fileFormat,
					fileTemplateName, parameters, config, 0, fileName);

		}
		return response;

	}

	private List<InvoiceCoverPageResponseDto> getInvoiceCoverPageData(String invNumber) throws NullPointerException {

		List<InvoiceCoverPageResponseDto> invoiceCoverPageResponseDtoList = new ArrayList<>();

		InvoiceCoverPageResponseDto invoiceCoverPageResponseDto = new InvoiceCoverPageResponseDto();

		List<String> orderNos = invoiceCoverPageRepository.getOrderNumbers(invNumber);

		List<String> countryCodes = invoiceCoverPageRepository.getCountryCode(invNumber);

		Tuple paramDetails = invoiceCoverPageRepository.getParameterDetails();

		String countryName = null;

		String xdocCount = null;

		if (paramDetails != null) {
			countryName = paramDetails.get(0, String.class);

			xdocCount = paramDetails.get(2, String.class);
		}

		String countryOfOrigin = invoiceCoverPageRepository.getCountryOfOrigin();

		Tuple invCompDetail = getInvCompDetail(invNumber);

		String invCompDetail1 = null;
		String invCompDetail2 = null;
		String invCompDetail3 = null;
		String invCompDetail4 = null;
		String invCompDetail5 = null;

		if (invCompDetail != null) {
			invCompDetail1 = invCompDetail.get(0, String.class);
			invCompDetail2 = invCompDetail.get(1, String.class);
			invCompDetail3 = invCompDetail.get(2, String.class);
			invCompDetail4 = invCompDetail.get(3, String.class);
			invCompDetail5 = invCompDetail.get(4, String.class);
		}

		Tuple crossDoc = invoiceCoverPageRepository.getCrossDoc(invNumber);

		String xdocFlg = null;
		String finalDest = null;
		if (crossDoc != null) {
			xdocFlg = crossDoc.get(0, String.class);
			finalDest = crossDoc.get(1, String.class);
		}

		String destname = invoiceCoverPageRepository.getDestName(finalDest);

		String custName = null;

		if ("Y".equalsIgnoreCase(xdocFlg))
			custName = xdocCount;
		else
			custName = destname;

		String orderNo = null;
		if (!orderNos.isEmpty()) {
			orderNo = orderNos.stream().collect(Collectors.joining(","));
		}

		String countryCode = null;
		if (!countryCodes.isEmpty()) {
			countryCode = countryCodes.stream().collect(Collectors.joining(","));
		}

		Tuple shippingMarkdetails = invoiceCoverPageRepository.getshippingMarkdetails(invNumber);

		BigInteger mark4Len = BigInteger.valueOf(0);
		BigInteger mark5Len = BigInteger.valueOf(0);
		BigInteger mark6Len = BigInteger.valueOf(0);

		if (shippingMarkdetails != null) {
			mark4Len = shippingMarkdetails.get(0, BigInteger.class);
			mark5Len = shippingMarkdetails.get(1, BigInteger.class);
			mark6Len = shippingMarkdetails.get(2, BigInteger.class);
		}

		Tuple invoiceDetails = null;

		if ((mark4Len.compareTo(BigInteger.valueOf(280)) < 0) && (mark5Len.compareTo(BigInteger.valueOf(280))) < 0
				&& (mark6Len.compareTo(BigInteger.valueOf(280)) < 0)) {

			invoiceDetails = invoiceCoverPageRepository.getInvoiceDetails(invNumber);

		} else {

			invoiceDetails = invoiceCoverPageRepository.getInvoiceDetails2(invNumber);
		}

		invoiceCoverPageResponseDto.setIndInvNo(invNumber);
		invoiceCoverPageResponseDto.setLvCustNm(custName);
		invoiceCoverPageResponseDto.setOrdNo(orderNo);
		invoiceCoverPageResponseDto.setOriginCntry(countryName);
		invoiceCoverPageResponseDto.setLvFnlCntryNm(destname);
		invoiceCoverPageResponseDto.setCmpName(invCompDetail1);
		invoiceCoverPageResponseDto.setCmpAdd1(invCompDetail2);
		invoiceCoverPageResponseDto.setCmpAdd2(invCompDetail3);
		invoiceCoverPageResponseDto.setCmpAdd3(invCompDetail4);
		invoiceCoverPageResponseDto.setCmpAdd4(invCompDetail5);
		invoiceCoverPageResponseDto.setBuyerCntryFinal(destname);
		invoiceCoverPageResponseDto.setCntryOrg(countryOfOrigin);
		if (countryCode != null) {
			invoiceCoverPageResponseDto.setCoCd("Country of Origin: ".concat(countryCode));
		}

		if (invoiceDetails != null) {
			invoiceCoverPageResponseDto.setInvDt(invoiceDetails.get(1, String.class));
			invoiceCoverPageResponseDto.setIndCustNm(invoiceDetails.get(2, String.class));
			invoiceCoverPageResponseDto.setIndCustAddr1(invoiceDetails.get(3, String.class));
			invoiceCoverPageResponseDto.setIndCustAddr2(invoiceDetails.get(4, String.class));
			invoiceCoverPageResponseDto.setIndCustAddr3(invoiceDetails.get(5, String.class));
			invoiceCoverPageResponseDto.setIndCustAddr4(invoiceDetails.get(6, String.class));
			invoiceCoverPageResponseDto.setIndCustZip(invoiceDetails.get(7, String.class));
			invoiceCoverPageResponseDto.setEtdDt(invoiceDetails.get(8, String.class));
			invoiceCoverPageResponseDto.setEtaDt(invoiceDetails.get(9, String.class));
			invoiceCoverPageResponseDto.setIndVesselNameOcean(invoiceDetails.get(10, String.class));
			invoiceCoverPageResponseDto.setIndVoyageNoOcean(invoiceDetails.get(11, String.class));
			invoiceCoverPageResponseDto.setIndVesselNameFeeder(invoiceDetails.get(12, String.class));
			invoiceCoverPageResponseDto.setIndVoyageNoFeeder(invoiceDetails.get(13, String.class));
			invoiceCoverPageResponseDto.setPmName(invoiceDetails.get(14, String.class));
			invoiceCoverPageResponseDto.setBuyerCntry(invoiceDetails.get(15, String.class));
			invoiceCoverPageResponseDto.setPtDesc(invoiceDetails.get(16, String.class));
			invoiceCoverPageResponseDto.setIndGoodsDesc1(invoiceDetails.get(17, String.class));
			invoiceCoverPageResponseDto.setIndGoodsDesc2(invoiceDetails.get(18, String.class));
			invoiceCoverPageResponseDto.setIndGoodsDesc3(invoiceDetails.get(19, String.class));
			invoiceCoverPageResponseDto.setIndTradeTrm(invoiceDetails.get(20, String.class));
			invoiceCoverPageResponseDto.setPmName2(invoiceDetails.get(21, String.class));
			invoiceCoverPageResponseDto.setIndInvAmt(invoiceDetails.get(22, BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIndInvQty(invoiceDetails.get(23, BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIndGrossWt(invoiceDetails.get(24, BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIndNetWt(invoiceDetails.get(25, BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIndMeasurement(invoiceDetails.get(26, BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIndNoOfCases(invoiceDetails.get(27, BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIndFreight(invoiceDetails.get(28, BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIndInsurance(invoiceDetails.get(29, BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIndMark1(invoiceDetails.get(30, String.class));
			invoiceCoverPageResponseDto.setIndMark2(invoiceDetails.get(31, String.class));
			invoiceCoverPageResponseDto.setIndMark3(invoiceDetails.get(32, String.class));
			invoiceCoverPageResponseDto.setIndMark41(invoiceDetails.get(33, String.class));
			invoiceCoverPageResponseDto.setIndMark42(invoiceDetails.get(34, String.class));
			invoiceCoverPageResponseDto.setIndMark43(invoiceDetails.get(35, String.class));
			invoiceCoverPageResponseDto.setIndMark44(invoiceDetails.get(36, String.class));
			invoiceCoverPageResponseDto.setIndMark51(invoiceDetails.get(37, String.class));
			invoiceCoverPageResponseDto.setIndMark52(invoiceDetails.get(38, String.class));
			invoiceCoverPageResponseDto.setIndMark53(invoiceDetails.get(39, String.class));
			invoiceCoverPageResponseDto.setIndMark54(invoiceDetails.get(40, String.class));
			invoiceCoverPageResponseDto.setIndMark61(invoiceDetails.get(41, String.class));
			invoiceCoverPageResponseDto.setIndMark62(invoiceDetails.get(42, String.class));
			invoiceCoverPageResponseDto.setIndMark63(invoiceDetails.get(43, String.class));
			invoiceCoverPageResponseDto.setIndMark64(invoiceDetails.get(44, String.class));
			invoiceCoverPageResponseDto.setIndMark7(invoiceDetails.get(45, String.class));
			invoiceCoverPageResponseDto.setIndMark8(invoiceDetails.get(46, String.class));
			invoiceCoverPageResponseDto.setIndBuyerNm(invoiceDetails.get(47, String.class));
			invoiceCoverPageResponseDto.setIndBuyerAddr1(invoiceDetails.get(48, String.class));
			invoiceCoverPageResponseDto.setIndBuyerAddr2(invoiceDetails.get(49, String.class));
			invoiceCoverPageResponseDto.setIndBuyerAddr3(invoiceDetails.get(50, String.class));
			invoiceCoverPageResponseDto.setIndBuyerAddr4(invoiceDetails.get(51, String.class));
			invoiceCoverPageResponseDto.setIndBuyerZip(invoiceDetails.get(52, String.class));
			invoiceCoverPageResponseDto.setCrmCurrPrintNm(invoiceDetails.get(53, String.class));
			invoiceCoverPageResponseDto.setIndGoodsDesc4(invoiceDetails.get(54, String.class));
			invoiceCoverPageResponseDto.setIndGoodsDesc5(invoiceDetails.get(55, String.class));
			invoiceCoverPageResponseDto.setIndGoodsDesc6(invoiceDetails.get(56, String.class));
			invoiceCoverPageResponseDto.setIndHaisenNo(invoiceDetails.get(57, String.class));
			invoiceCoverPageResponseDto.setIndNotify(invoiceDetails.get(58, String.class));
			invoiceCoverPageResponseDto.setIndNotifyAddr1(invoiceDetails.get(59, String.class));
			invoiceCoverPageResponseDto.setIndNotifyAddr2(invoiceDetails.get(60, String.class));
			invoiceCoverPageResponseDto.setIndNotifyAddr3(invoiceDetails.get(61, String.class));
			invoiceCoverPageResponseDto.setIndNotifyAddr4(invoiceDetails.get(62, String.class));
			invoiceCoverPageResponseDto.setIndNotifyCntry(invoiceDetails.get(63, String.class));
			invoiceCoverPageResponseDto.setIndNotifyNm(invoiceDetails.get(64, String.class));
			invoiceCoverPageResponseDto.setIndNotifyZip(invoiceDetails.get(65, String.class));
			invoiceCoverPageResponseDto.setXdocFlg(invoiceDetails.get(66, String.class));
		}

		invoiceCoverPageResponseDtoList.add(invoiceCoverPageResponseDto);

		return invoiceCoverPageResponseDtoList;
	}

	private Tuple getInvCompDetail(String invNumber) {
		Tuple invCompDetail = null;

		String companyCode = invPackingListRepository.getCompanyCode();
		if (companyCode == null) {
			throw new NullPointerException("CompanyCode is " + companyCode);
		}

		String compCode = invPackingListRepository.getCompCode(invNumber);

		String tmapthInvFlg = invPackingListRepository.getTmapthInvFlg(invNumber);
		if ("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgY(companyCode, compCode);
		} else {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgN(companyCode);
		}
		return invCompDetail;
	}

}
