package com.tpex.invoice.serviceImpl;

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

@Service
@Transactional
public class InvoiceCoverPageServiceImpl implements InvoiceCoverPageService{

	@Autowired
	InvoiceCoverPageRepository invoiceCoverPageRepository;

	@Autowired
	InvPackingListRepository invPackingListRepository;

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Override
	public Object getInvoiceCoverPageRptDownload(String invNumber, String fileTemplateName,String reportFormat)
			throws Exception {

		Object response = null;
		Map<String, Object> parameters = new HashMap<>();

		parameters.put(ConstantUtils.INVOICE_NO, invNumber);

		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY, tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue());
		config.put(ConstantUtils.REPORT_FORMAT, tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT).getValue());
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_SIZE_LIMIT).getValue());
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID,ConstantUtils.TEST_USER);
		String fileFormat = StringUtils.isNotBlank(reportFormat)
				&& ConstantUtils.XLSX.equalsIgnoreCase(reportFormat) ? reportFormat : ConstantUtils.PDF;
		String fileName = invNumber + "_COV." + fileFormat;

		List<InvoiceCoverPageResponseDto> invoiceCoverPageResponseDtoList = getInvoiceCoverPageData(invNumber);

		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY))).append("/")
				.append(fileName);

		if (ConstantUtils.XLSX.equals(fileFormat)) {
			response = jasperReportService.getJasperReportDownloadOnline(invoiceCoverPageResponseDtoList, fileFormat, fileTemplateName, fileName, parameters, config);

		}else {
			response = jasperReportService.getJasperReportDownloadOfflineV1(invoiceCoverPageResponseDtoList, fileFormat, fileTemplateName, parameters, config, 0, sb, fileName);

		}
		return response;

	}

	private List<InvoiceCoverPageResponseDto> getInvoiceCoverPageData(String invNumber) throws Exception{

		List<InvoiceCoverPageResponseDto> invoiceCoverPageResponseDtoList = new ArrayList<>();

		InvoiceCoverPageResponseDto invoiceCoverPageResponseDto= new InvoiceCoverPageResponseDto();

		List<String> orderNos = invoiceCoverPageRepository.getOrderNumbers(invNumber);

		List<String> countryCodes = invoiceCoverPageRepository.getCountryCode(invNumber);

		Tuple paramDetails = invoiceCoverPageRepository.getParameterDetails();


		String countryName = null;
		String compCd = null;
		String xdocCount = null;

		if(paramDetails!=null) {
			countryName = paramDetails.get(0,String.class);
			compCd = paramDetails.get(1,String.class);
			xdocCount = paramDetails.get(2,String.class);
		}

		String CountryOfOrigin = invoiceCoverPageRepository.getCountryOfOrigin();

		Tuple invCompDetail = null;

		String companyCode = invPackingListRepository.getCompanyCode(); 
		if (companyCode == null) {
			throw new Exception("");
		}

		String compCode = invPackingListRepository.getCompCode(invNumber);

		String tmapthInvFlg = invPackingListRepository.getTmapthInvFlg(invNumber);
		if ("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgY(companyCode, compCode);
		} else {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgN(companyCode);
		}

		String invCompDetail1 = null;
		String invCompDetail2 = null;
		String invCompDetail3 = null;
		String invCompDetail4 = null;
		String invCompDetail5 = null;

		if(invCompDetail!=null) {
			invCompDetail1 = invCompDetail.get(0, String.class);
			invCompDetail2 = invCompDetail.get(1, String.class);
			invCompDetail3 = invCompDetail.get(2, String.class);
			invCompDetail4 = invCompDetail.get(3, String.class);
			invCompDetail5 = invCompDetail.get(4, String.class);
		}

		Integer count = invoiceCoverPageRepository.getCount(invNumber);

		Tuple crossDoc = invoiceCoverPageRepository.getCrossDoc(invNumber);

		String xdocFlg = null;
		String finalDest = null;
		if(crossDoc!=null) {
			xdocFlg   = crossDoc.get(0,String.class);
			finalDest = crossDoc.get(1,String.class);
		}

		String destname = invoiceCoverPageRepository.getDestName(finalDest);

		String custName = null;

		if("Y".equalsIgnoreCase(xdocFlg))
			custName = xdocCount;
		else
			custName = destname;

		String orderNo = null;
		if(!orderNos.isEmpty()) {
			orderNo = orderNos.stream().collect(Collectors.joining(","));
		}

		String countryCode = null;
		if(!countryCodes.isEmpty()) {
			countryCode = countryCodes.stream().collect(Collectors.joining(","));
		}

		Tuple shippingMarkdetails = invoiceCoverPageRepository.getshippingMarkdetails(invNumber);

		BigInteger mark4Len = BigInteger.valueOf(0);
		BigInteger mark5Len = BigInteger.valueOf(0);
		BigInteger mark6Len = BigInteger.valueOf(0);

		if(shippingMarkdetails!=null) {
			mark4Len = shippingMarkdetails.get(0, BigInteger.class);
			mark5Len = shippingMarkdetails.get(1, BigInteger.class);
			mark6Len = shippingMarkdetails.get(2, BigInteger.class);
		}

		Tuple invoiceDetails = null;

		if((mark4Len.compareTo(BigInteger.valueOf(280)) < 0) && (mark5Len.compareTo(BigInteger.valueOf(280))) < 0 && (mark6Len.compareTo(BigInteger.valueOf(280)) < 0)){

			invoiceDetails = invoiceCoverPageRepository.getInvoiceDetails(invNumber);

		}else {

			invoiceDetails = invoiceCoverPageRepository.getInvoiceDetails2(invNumber);
		}

		invoiceCoverPageResponseDto.setIND_INV_NO(invNumber);
		invoiceCoverPageResponseDto.setL_V_CUST_NM(custName);
		invoiceCoverPageResponseDto.setORD_NO(orderNo);
		invoiceCoverPageResponseDto.setORGIN_CNTRY(countryName);
		invoiceCoverPageResponseDto.setL_V_FNL_CNTRY_NM(destname);
		invoiceCoverPageResponseDto.setCMP_NAME(invCompDetail1);
		invoiceCoverPageResponseDto.setCMP_ADD_1(invCompDetail2);
		invoiceCoverPageResponseDto.setCMP_ADD_2(invCompDetail3);
		invoiceCoverPageResponseDto.setCMP_ADD_3(invCompDetail4);
		invoiceCoverPageResponseDto.setCMP_ADD_4(invCompDetail5);
		invoiceCoverPageResponseDto.setBUYER_CNTRY_FINAL(destname);
		invoiceCoverPageResponseDto.setCNTRY_ORG(CountryOfOrigin);
		if(countryCode != null) {
			invoiceCoverPageResponseDto.setCO_CD("Country of Origin: ".concat(countryCode));
		}

		if(invoiceDetails!= null) {
			invoiceCoverPageResponseDto.setINV_DT(invoiceDetails.get(1,String.class));
			invoiceCoverPageResponseDto.setIND_CUST_NM(invoiceDetails.get(2,String.class));
			invoiceCoverPageResponseDto.setIND_CUST_ADDR1(invoiceDetails.get(3,String.class));
			invoiceCoverPageResponseDto.setIND_CUST_ADDR2(invoiceDetails.get(4,String.class));
			invoiceCoverPageResponseDto.setIND_CUST_ADDR3(invoiceDetails.get(5,String.class));
			invoiceCoverPageResponseDto.setIND_CUST_ADDR4(invoiceDetails.get(6,String.class));
			invoiceCoverPageResponseDto.setIND_CUST_ZIP(invoiceDetails.get(7,String.class));
			invoiceCoverPageResponseDto.setETD_DT(invoiceDetails.get(8,String.class));
			invoiceCoverPageResponseDto.setETA_DT(invoiceDetails.get(9,String.class));
			invoiceCoverPageResponseDto.setIND_VESSEL_NAME_OCEAN(invoiceDetails.get(10,String.class));
			invoiceCoverPageResponseDto.setIND_VOYAGE_NO_OCEAN(invoiceDetails.get(11,String.class));
			invoiceCoverPageResponseDto.setIND_VESSEL_NAME_FEEDER(invoiceDetails.get(12,String.class));
			invoiceCoverPageResponseDto.setIND_VOYAGE_NO_FEEDER(invoiceDetails.get(13,String.class));
			invoiceCoverPageResponseDto.setPM_NAME(invoiceDetails.get(14,String.class));
			invoiceCoverPageResponseDto.setBUYER_CNTRY(invoiceDetails.get(15,String.class));
			invoiceCoverPageResponseDto.setPT_DESC(invoiceDetails.get(16,String.class));
			invoiceCoverPageResponseDto.setIND_GOODS_DESC1(invoiceDetails.get(17,String.class));
			invoiceCoverPageResponseDto.setIND_GOODS_DESC2(invoiceDetails.get(18,String.class));
			invoiceCoverPageResponseDto.setIND_GOODS_DESC3(invoiceDetails.get(19,String.class));
			invoiceCoverPageResponseDto.setIND_TRADE_TRM(invoiceDetails.get(20,String.class));
			invoiceCoverPageResponseDto.setPM_NAME2(invoiceDetails.get(21,String.class));
			invoiceCoverPageResponseDto.setIND_INV_AMT(invoiceDetails.get(22,BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIND_INV_QTY(invoiceDetails.get(23,BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIND_GROSS_WT(invoiceDetails.get(24,BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIND_NET_WT(invoiceDetails.get(25,BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIND_MEASUREMENT(invoiceDetails.get(26,BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIND_NO_OF_CASES(invoiceDetails.get(27,BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIND_FREIGHT(invoiceDetails.get(28,BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIND_INSURANCE(invoiceDetails.get(29,BigDecimal.class).doubleValue());
			invoiceCoverPageResponseDto.setIND_MARK1(invoiceDetails.get(30,String.class));
			invoiceCoverPageResponseDto.setIND_MARK2(invoiceDetails.get(31,String.class));
			invoiceCoverPageResponseDto.setIND_MARK3(invoiceDetails.get(32,String.class));
			invoiceCoverPageResponseDto.setIND_MARK4_1(invoiceDetails.get(33,String.class));
			invoiceCoverPageResponseDto.setIND_MARK4_2(invoiceDetails.get(34,String.class));
			invoiceCoverPageResponseDto.setIND_MARK4_3(invoiceDetails.get(35,String.class));
			invoiceCoverPageResponseDto.setIND_MARK4_4(invoiceDetails.get(36,String.class));
			invoiceCoverPageResponseDto.setIND_MARK5_1(invoiceDetails.get(37,String.class));
			invoiceCoverPageResponseDto.setIND_MARK5_2(invoiceDetails.get(38,String.class));
			invoiceCoverPageResponseDto.setIND_MARK5_3(invoiceDetails.get(39,String.class));
			invoiceCoverPageResponseDto.setIND_MARK5_4(invoiceDetails.get(40,String.class));
			invoiceCoverPageResponseDto.setIND_MARK6_1(invoiceDetails.get(41,String.class));
			invoiceCoverPageResponseDto.setIND_MARK6_2(invoiceDetails.get(42,String.class));
			invoiceCoverPageResponseDto.setIND_MARK6_3(invoiceDetails.get(43,String.class));
			invoiceCoverPageResponseDto.setIND_MARK6_4(invoiceDetails.get(44,String.class));
			invoiceCoverPageResponseDto.setIND_MARK7(invoiceDetails.get(45,String.class));
			invoiceCoverPageResponseDto.setIND_MARK8(invoiceDetails.get(46,String.class));
			invoiceCoverPageResponseDto.setIND_BUYER_NM(invoiceDetails.get(47,String.class));
			invoiceCoverPageResponseDto.setIND_BUYER_ADDR1(invoiceDetails.get(48,String.class));
			invoiceCoverPageResponseDto.setIND_BUYER_ADDR2(invoiceDetails.get(49,String.class));
			invoiceCoverPageResponseDto.setIND_BUYER_ADDR3(invoiceDetails.get(50,String.class));
			invoiceCoverPageResponseDto.setIND_BUYER_ADDR4(invoiceDetails.get(51,String.class));
			invoiceCoverPageResponseDto.setIND_BUYER_ZIP(invoiceDetails.get(52,String.class));
			invoiceCoverPageResponseDto.setCRM_CURR_PRNT_NM(invoiceDetails.get(53,String.class));
			invoiceCoverPageResponseDto.setIND_GOODS_DESC4(invoiceDetails.get(54,String.class));
			invoiceCoverPageResponseDto.setIND_GOODS_DESC5(invoiceDetails.get(55,String.class));
			invoiceCoverPageResponseDto.setIND_GOODS_DESC6(invoiceDetails.get(56,String.class));
			invoiceCoverPageResponseDto.setIND_HAISEN_NO(invoiceDetails.get(57,String.class));
			invoiceCoverPageResponseDto.setIND_NOTIFY(invoiceDetails.get(58,String.class));
			invoiceCoverPageResponseDto.setIND_NOTIFY_ADDR1(invoiceDetails.get(59,String.class));
			invoiceCoverPageResponseDto.setIND_NOTIFY_ADDR2(invoiceDetails.get(60,String.class));
			invoiceCoverPageResponseDto.setIND_NOTIFY_ADDR3(invoiceDetails.get(61,String.class));
			invoiceCoverPageResponseDto.setIND_NOTIFY_ADDR4(invoiceDetails.get(62,String.class));
			invoiceCoverPageResponseDto.setIND_NOTIFY_CNTRY(invoiceDetails.get(63,String.class));
			invoiceCoverPageResponseDto.setIND_NOTIFY_NM(invoiceDetails.get(64,String.class));
			invoiceCoverPageResponseDto.setIND_NOTIFY_ZIP(invoiceDetails.get(65,String.class));
			invoiceCoverPageResponseDto.setXDOC_FLG(invoiceDetails.get(66,String.class));
		}

		invoiceCoverPageResponseDtoList.add(invoiceCoverPageResponseDto);

		return invoiceCoverPageResponseDtoList;
	}

}
