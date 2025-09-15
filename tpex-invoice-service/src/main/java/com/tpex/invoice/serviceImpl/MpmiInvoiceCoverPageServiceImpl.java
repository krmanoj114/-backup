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
import com.tpex.invoice.dto.MpmiInvoiceCoverPageResponseDto;
import com.tpex.invoice.service.InvoiceCoverPageService;
import com.tpex.invoice.service.MpmiInvoiceCoverPageService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvPackingListRepository;
import com.tpex.repository.InvoiceCoverPageRepository;
import com.tpex.repository.MpmiInvoiceCoverPageRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

@Service
@Transactional
public class MpmiInvoiceCoverPageServiceImpl implements MpmiInvoiceCoverPageService{

	@Autowired
	MpmiInvoiceCoverPageRepository invoiceCoverPageRepository;

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
		config.put(ConstantUtils.STORE_DB, "false");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		String fileFormat = StringUtils.isNotBlank(reportFormat)
				&& "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat : "pdf";
		String fileName = invNumber + "_COV_MPMI." + fileFormat;

		List<MpmiInvoiceCoverPageResponseDto> invoiceCoverPageResponseDtoList = getMpmiInvoiceCoverPageData(invNumber);

		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get(ConstantUtils.REPORT_DIRECTORY))).append("/")
				.append(fileName);

		if ("xlsx".equals(fileFormat)) {
			response = jasperReportService.getJasperReportDownloadOnline(invoiceCoverPageResponseDtoList, fileFormat, fileTemplateName, fileName, parameters, config);

		}else {
			response = jasperReportService.getJasperReportDownloadOfflineV1(invoiceCoverPageResponseDtoList, fileFormat, fileTemplateName, parameters, config, 0, sb, fileName);

		}
		return response;

	}

	private List<MpmiInvoiceCoverPageResponseDto> getMpmiInvoiceCoverPageData(String invNumber) throws Exception{

		List<MpmiInvoiceCoverPageResponseDto> invoiceCoverPageResponseDtoList = new ArrayList<>();

		MpmiInvoiceCoverPageResponseDto invoiceCoverPageResponseDto= new MpmiInvoiceCoverPageResponseDto();

		List<String> orderNos = invoiceCoverPageRepository.getOrderNumbers(invNumber);

		List<String> countryCodes = invoiceCoverPageRepository.getCountryCode(invNumber);
		
		List<Tuple> tariffQtyAndAmount = invoiceCoverPageRepository.getTariff(invNumber);

		Tuple paramDetails = invoiceCoverPageRepository.getParameterDetails();

		String countryName = paramDetails.get(0,String.class); //comment this, unused
		String compCd = paramDetails.get(1,String.class);
		String xdocCount = paramDetails.get(2,String.class);

		String CountryOfOrigin = invoiceCoverPageRepository.getCountryOfOrigin();

		Tuple invCompDetail = null;

		String companyCode = invPackingListRepository.getCompanyCode(); 

		String compCode = invPackingListRepository.getCompCode(invNumber);

		String tmapthInvFlg = invPackingListRepository.getTmapthInvFlg(invNumber);
		if ("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgY(companyCode, compCode);
		} else {
			invCompDetail = invPackingListRepository.getInvCompDetailWhenFlgN(companyCode);
		}

		String invCompDetail1 = invCompDetail.get(0, String.class);
		String invCompDetail2 = invCompDetail.get(1, String.class);
		String invCompDetail3 = invCompDetail.get(2, String.class);
		String invCompDetail4 = invCompDetail.get(3, String.class);
		String invCompDetail5 = invCompDetail.get(4, String.class);

		Integer count = invoiceCoverPageRepository.getCount(invNumber);

		Tuple crossDoc = invoiceCoverPageRepository.getCrossDoc(invNumber);

		String xdocFlg   = crossDoc.get(0,String.class);
		String finalDest = crossDoc.get(1,String.class);

		String destname = invoiceCoverPageRepository.getDestName(finalDest);

		String custName = null;

		if("Y".equalsIgnoreCase(xdocFlg))
			custName = xdocCount;
		else
			custName = destname;

		String orderNo = orderNos.stream().collect(Collectors.joining(","));

		String countryCode = countryCodes.stream().collect(Collectors.joining(","));

		String tariffName1 = null;
		Double tariffQty1 = null ;
		Double tariffAmt1 = null ;
		String tariffName2 = null;
		Double tariffQty2= null ;
		Double tariffAmt2= null ;
		String tariffName3 = null;
		Double tariffQty3= null ;
		Double tariffAmt3= null ;
		String tariffName4 = null;
		Double tariffQty4= null ;
		Double tariffAmt4= null ;
		String tariffName5 = null;
		Double tariffQty5= null ;
		Double tariffAmt5= null ;
		String tariffName6 = null;
		Double tariffQty6= null ;
		Double tariffAmt6= null ;
		
		int counter = 0;
		for( Tuple tuple : tariffQtyAndAmount) {
			counter = counter + 1;
			if(counter == 1)
			{
				tariffName1 = tuple.get(0,String.class);
				tariffQty1 = tuple.get(2,BigDecimal.class).doubleValue();
				tariffAmt1 = tuple.get(3,BigDecimal.class).doubleValue();
				tariffName2 = ConstantUtils.GRAND_TOTAL;
				tariffQty2 = tariffQty1;
				tariffAmt2 = tariffAmt1;
			}
			else if(counter == 2)
			{
				tariffName2 = tuple.get(0,String.class);
				tariffQty2 = tuple.get(2,BigDecimal.class).doubleValue();
				tariffAmt2 = tuple.get(3,BigDecimal.class).doubleValue();
				tariffName3 = ConstantUtils.GRAND_TOTAL;
				tariffQty3 = tariffQty1 + tariffQty2;
				tariffAmt3 = tariffAmt1 + tariffAmt2;
			}
			else if(counter == 3)
			{
				tariffName3 = tuple.get(0,String.class);
				tariffQty3 = tuple.get(2,BigDecimal.class).doubleValue();
				tariffAmt3 = tuple.get(3,BigDecimal.class).doubleValue();
				tariffName4 = ConstantUtils.GRAND_TOTAL;
				tariffQty4 = tariffQty1 + tariffQty2 + tariffQty3;
				tariffAmt4 = tariffAmt1 + tariffAmt2 + tariffAmt3;
			}
			else if(counter == 4)
			{
				tariffName4 = tuple.get(0,String.class);
				tariffQty4 = tuple.get(2,BigDecimal.class).doubleValue();
				tariffAmt4 = tuple.get(3,BigDecimal.class).doubleValue();
				tariffName5 = ConstantUtils.GRAND_TOTAL;
				tariffQty5 = tariffQty1 + tariffQty2 + tariffQty3 + tariffQty4;
				tariffAmt5 = tariffAmt1 + tariffAmt2 + tariffAmt3 + tariffAmt4;
			}
			else if(counter == 5)
			{
				tariffName5 = tuple.get(0,String.class);
				tariffQty5 = tuple.get(2,BigDecimal.class).doubleValue();
				tariffAmt5 = tuple.get(3,BigDecimal.class).doubleValue();
				tariffName6 = ConstantUtils.GRAND_TOTAL;
				tariffQty6 = tariffQty1 + tariffQty2 + tariffQty3 + tariffQty4 + tariffQty5;
				tariffAmt6 = tariffAmt1 + tariffAmt2 + tariffAmt3 + tariffAmt4 + tariffAmt5;
			}
		}

		Tuple shippingMarkdetails = invoiceCoverPageRepository.getshippingMarkdetails(invNumber);
		BigInteger mark4Len = shippingMarkdetails.get(0, BigInteger.class);
		BigInteger mark5Len = shippingMarkdetails.get(1, BigInteger.class);
		BigInteger mark6Len = shippingMarkdetails.get(2, BigInteger.class);

		Tuple invoiceDetails = null;

		if(mark4Len.compareTo(BigInteger.valueOf(280)) < 0 && mark5Len.compareTo(BigInteger.valueOf(280)) < 0 && mark6Len.compareTo(BigInteger.valueOf(280)) < 0){

			invoiceDetails = invoiceCoverPageRepository.getInvoiceDetails(invNumber);

		}else {

			invoiceDetails = invoiceCoverPageRepository.getInvoiceDetails2(invNumber);
		}

		invoiceCoverPageResponseDto.setInvoiceNo(invNumber);
		if(invoiceDetails != null) {
		invoiceCoverPageResponseDto.setInvoiceDate(invoiceDetails.get(1,String.class));
		}
		invoiceCoverPageResponseDto.setCompNme(invCompDetail1);
		invoiceCoverPageResponseDto.setCompAdd1(invCompDetail2);
		invoiceCoverPageResponseDto.setCompAdd2(invCompDetail3);
		invoiceCoverPageResponseDto.setCompAdd3(invCompDetail4);
		invoiceCoverPageResponseDto.setCompAdd4(invCompDetail5);
		if(invoiceDetails != null) {
		invoiceCoverPageResponseDto.setCustName(invoiceDetails.get(2,String.class));
		invoiceCoverPageResponseDto.setCustAddr1(invoiceDetails.get(3,String.class));
		invoiceCoverPageResponseDto.setCustAddr2(invoiceDetails.get(4,String.class));
		invoiceCoverPageResponseDto.setCustAddr3(invoiceDetails.get(5,String.class));
		invoiceCoverPageResponseDto.setCustAddr4(invoiceDetails.get(6,String.class));
		invoiceCoverPageResponseDto.setCustZip(invoiceDetails.get(7,String.class));
		//invoiceCoverPageResponseDto.setL_V_CUST_NM(custName);
		invoiceCoverPageResponseDto.setOrderNo(orderNo);
		invoiceCoverPageResponseDto.setEtdDate(invoiceDetails.get(8,String.class));
		invoiceCoverPageResponseDto.setEtaDate(invoiceDetails.get(9,String.class));
		invoiceCoverPageResponseDto.setVesselNameOcean(invoiceDetails.get(10,String.class));
		invoiceCoverPageResponseDto.setVoyageNoOcean(invoiceDetails.get(11,String.class));
		invoiceCoverPageResponseDto.setVesselNameFeeder(invoiceDetails.get(12,String.class));
		invoiceCoverPageResponseDto.setVoyageNoFeeder(invoiceDetails.get(13,String.class));
		invoiceCoverPageResponseDto.setPmName(invoiceDetails.get(14,String.class));
		invoiceCoverPageResponseDto.setOriginCntry(countryName);
		//invoiceCoverPageResponseDto.setL_V_FNL_CNTRY_NM(destname);
		invoiceCoverPageResponseDto.setBuyerCntry(invoiceDetails.get(15,String.class));
		invoiceCoverPageResponseDto.setPtDesc(invoiceDetails.get(16,String.class));
		invoiceCoverPageResponseDto.setGoodsDesc1(invoiceDetails.get(17,String.class));
		invoiceCoverPageResponseDto.setGoodsDesc2(invoiceDetails.get(18,String.class));
		invoiceCoverPageResponseDto.setGoodsDesc3(invoiceDetails.get(19,String.class));
		invoiceCoverPageResponseDto.setTradeTerm(invoiceDetails.get(20,String.class));
		invoiceCoverPageResponseDto.setPmName2(invoiceDetails.get(21,String.class));
		invoiceCoverPageResponseDto.setInvoiceAmount(invoiceDetails.get(22,BigDecimal.class).doubleValue());
		invoiceCoverPageResponseDto.setInvoiceQty(invoiceDetails.get(23,BigDecimal.class).doubleValue());
		invoiceCoverPageResponseDto.setGrossWeight(invoiceDetails.get(24,BigDecimal.class).doubleValue());
		invoiceCoverPageResponseDto.setNetWeight(invoiceDetails.get(25,BigDecimal.class).doubleValue());
		invoiceCoverPageResponseDto.setMeasurement(invoiceDetails.get(26,BigDecimal.class).doubleValue());
		invoiceCoverPageResponseDto.setNoOfCases(invoiceDetails.get(27,BigDecimal.class).doubleValue());
		invoiceCoverPageResponseDto.setFreight(invoiceDetails.get(28,BigDecimal.class).doubleValue());
		invoiceCoverPageResponseDto.setInsurance(invoiceDetails.get(29,BigDecimal.class).doubleValue());
		invoiceCoverPageResponseDto.setMark1(invoiceDetails.get(30,String.class));
		invoiceCoverPageResponseDto.setMark2(invoiceDetails.get(31,String.class));
		invoiceCoverPageResponseDto.setMark3(invoiceDetails.get(32,String.class));
		invoiceCoverPageResponseDto.setMark4_1(invoiceDetails.get(33,String.class));
		invoiceCoverPageResponseDto.setMark4_2(invoiceDetails.get(34,String.class));
		invoiceCoverPageResponseDto.setMark4_3(invoiceDetails.get(35,String.class));
		invoiceCoverPageResponseDto.setMark4_4(invoiceDetails.get(36,String.class));
		invoiceCoverPageResponseDto.setMark5_1(invoiceDetails.get(37,String.class));
		invoiceCoverPageResponseDto.setMark5_2(invoiceDetails.get(38,String.class));
		invoiceCoverPageResponseDto.setMark5_3(invoiceDetails.get(39,String.class));
		invoiceCoverPageResponseDto.setMark5_4(invoiceDetails.get(40,String.class));
		invoiceCoverPageResponseDto.setMark6_1(invoiceDetails.get(41,String.class));
		invoiceCoverPageResponseDto.setMark6_2(invoiceDetails.get(42,String.class));
		invoiceCoverPageResponseDto.setMark6_3(invoiceDetails.get(43,String.class));
		invoiceCoverPageResponseDto.setMark6_4(invoiceDetails.get(44,String.class));
		invoiceCoverPageResponseDto.setMark7(invoiceDetails.get(45,String.class));
		invoiceCoverPageResponseDto.setMark8(invoiceDetails.get(46,String.class));
		invoiceCoverPageResponseDto.setBuyerName(invoiceDetails.get(47,String.class));
		invoiceCoverPageResponseDto.setBuyerAddr1(invoiceDetails.get(48,String.class));
		invoiceCoverPageResponseDto.setBuyerAddr2(invoiceDetails.get(49,String.class));
		invoiceCoverPageResponseDto.setBuyerAddr3(invoiceDetails.get(50,String.class));
		invoiceCoverPageResponseDto.setBuyerAddr4(invoiceDetails.get(51,String.class));
		invoiceCoverPageResponseDto.setBuyerZip(invoiceDetails.get(52,String.class));
		invoiceCoverPageResponseDto.setCrmCurrPrntName(invoiceDetails.get(53,String.class));
		invoiceCoverPageResponseDto.setBuyerCntryFinal(destname);
		invoiceCoverPageResponseDto.setGoodsDesc4(invoiceDetails.get(54,String.class));
		invoiceCoverPageResponseDto.setGoodsDesc5(invoiceDetails.get(55,String.class));
		invoiceCoverPageResponseDto.setGoodsDesc6(invoiceDetails.get(56,String.class));
		invoiceCoverPageResponseDto.setHaisenNo(invoiceDetails.get(57,String.class));
		invoiceCoverPageResponseDto.setNotify(invoiceDetails.get(58,String.class));
		invoiceCoverPageResponseDto.setNotifyAddr1(invoiceDetails.get(59,String.class));
		invoiceCoverPageResponseDto.setNotifyAddr2(invoiceDetails.get(60,String.class));
		invoiceCoverPageResponseDto.setNotifyAddr3(invoiceDetails.get(61,String.class));
		invoiceCoverPageResponseDto.setNotifyAddr4(invoiceDetails.get(62,String.class));
		invoiceCoverPageResponseDto.setNotifyCntry(invoiceDetails.get(63,String.class));
		invoiceCoverPageResponseDto.setNotifyName(invoiceDetails.get(64,String.class));
		invoiceCoverPageResponseDto.setNotifyZip(invoiceDetails.get(65,String.class));
		invoiceCoverPageResponseDto.setXdocFlag(invoiceDetails.get(66,String.class));
		}
		invoiceCoverPageResponseDto.setCntryOrg(CountryOfOrigin);
		invoiceCoverPageResponseDto.setCoCd("Country of Origin: ".concat(countryCode));
		
		invoiceCoverPageResponseDto.setTariffName1(tariffName1);
		invoiceCoverPageResponseDto.setTariffName2(tariffName2);
		invoiceCoverPageResponseDto.setTariffName3(tariffName3);
		invoiceCoverPageResponseDto.setTariffName4(tariffName4);
		invoiceCoverPageResponseDto.setTariffName5(tariffName5);
		invoiceCoverPageResponseDto.setTariffName6(tariffName6);
		invoiceCoverPageResponseDto.setTariffAmount1(tariffAmt1);
		invoiceCoverPageResponseDto.setTariffAmount2(tariffAmt2);
		invoiceCoverPageResponseDto.setTariffAmount3(tariffAmt3);
		invoiceCoverPageResponseDto.setTariffAmount4(tariffAmt4);
		invoiceCoverPageResponseDto.setTariffAmount5(tariffAmt5);
		invoiceCoverPageResponseDto.setTariffAmount6(tariffAmt6);
		invoiceCoverPageResponseDto.setTariffQty1(tariffQty1);
		invoiceCoverPageResponseDto.setTariffQty2(tariffQty2);
		invoiceCoverPageResponseDto.setTariffQty3(tariffQty3);
		invoiceCoverPageResponseDto.setTariffQty4(tariffQty4);
		invoiceCoverPageResponseDto.setTariffQty5(tariffQty5);
		invoiceCoverPageResponseDto.setTariffQty6(tariffQty6);
		
		invoiceCoverPageResponseDtoList.add(invoiceCoverPageResponseDto);

		return invoiceCoverPageResponseDtoList;
	}

}
