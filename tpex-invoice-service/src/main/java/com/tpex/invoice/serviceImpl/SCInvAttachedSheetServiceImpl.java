package com.tpex.invoice.serviceImpl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.invoice.dto.SCInvAttachedSheetResponseDto;
import com.tpex.invoice.service.SCInvAttachedSheetService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InsInvPartsDetailsRepository;
import com.tpex.repository.TpexConfigRepository;

@Service
public class SCInvAttachedSheetServiceImpl implements SCInvAttachedSheetService {

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	InsInvPartsDetailsRepository insInvPartsDetailsRepository;

	@Autowired
	TpexConfigRepository tpexConfigRepository;




	/* pins003 Anurag Tpex 280
	 * This Method produces SC Invoice Attached Sheet Details based 
	 * on Company_code & User_ID & Invoice_no.	 * 
	 * 
	 * */

	public Object downloadPINS103forSc(String cmpCd, String invoiceNo, String userId, String reportFormat,  String templateId) throws Exception{
		//****************download report start/
		final Double insUnitPerBo = 0d ;
		final Double insPartW = 0d;
		final Double insGrossW = 0d;
		final Double insMeasurem = 0d;
		String insShipma4 = "";
		String insShipma5 = "";
		Object jasperResponse = null;

		List<SCInvAttachedSheetResponseDto> response = new ArrayList<>();

		List<Tuple> querydataforpins003forsC = insInvPartsDetailsRepository.getScQuerydataforpins003(invoiceNo);

		Tuple tmapthInvFlgobj = insInvPartsDetailsRepository.getScRemarks(invoiceNo); 

		String tmapthInvFlg = tmapthInvFlgobj == null || tmapthInvFlgobj.get(0, String.class)==null ? "": tmapthInvFlgobj.get(0, String.class);

		String scRemarks = tmapthInvFlgobj == null || tmapthInvFlgobj.get(1, String.class)==null ? "": tmapthInvFlgobj.get(1, String.class);

		Tuple InvCompDet  = null ; 


		if( "Y".equalsIgnoreCase(tmapthInvFlg))
		{
			InvCompDet = insInvPartsDetailsRepository.getScInvCompDetailsWhenFlgY(cmpCd, invoiceNo);     	 
		}
		else
		{
			InvCompDet = insInvPartsDetailsRepository.getScInvCompDetailWhenFlgN(cmpCd, invoiceNo);    	 
		}
		String invCompDetail1 = null;
		String invCompDetail2 = null;
		String invCompDetail3 = null;
		String invCompDetail4 = null;
		String invCompDetail5 = null;

		if (InvCompDet != null) {
			invCompDetail1 = InvCompDet.get(0, String.class);
			invCompDetail2 = InvCompDet.get(1, String.class);
			invCompDetail3 = InvCompDet.get(2, String.class);
			invCompDetail4 = InvCompDet.get(3, String.class);
			invCompDetail5 = InvCompDet.get(4, String.class);
		}

		final String invCompDetail6 = invCompDetail1;
		final String invCompDetail7 = invCompDetail2;
		final String invCompDetail8 = invCompDetail3;
		final String invCompDetail9 = invCompDetail4;
		final String invCompDetail10 = invCompDetail5;         

		//List<sCInvAttachedSheetResponseDto> li = null;

		response = querydataforpins003forsC.stream().map(t -> new SCInvAttachedSheetResponseDto(
				invCompDetail6,                                  //INS_CNSG_NAME
				invCompDetail7,                                  //INS_CNSG_ADD1
				invCompDetail8,                                  //INS_CNSG_ADD2
				invCompDetail9,                                  //INS_CNSG_ADD3
				invCompDetail10,                                 //INS_CNSG_ADD4
				invoiceNo,                                  //INS_INV_NO
				t.get(0, String.class),                      // INV_DT
				t.get(1, String.class),                      // PART_NO
				insUnitPerBo,                                     //INS_UNIT_PER_BOX
				t.get(2, BigDecimal.class).intValue(),       //SUM_OF_TOTAL_UNIT
				t.get(3, String.class),                      //ICO_FLG
				t.get(4, BigDecimal.class).doubleValue(),    //PRICE 
				t.get(5, String.class),                      // PRT_NAME
				insPartW,                                            //INS_PART_WT
				insGrossW,                                           // INS_GROSS_WT
				insMeasurem,                                        //INS_MEASUREMENT
				insShipma4,                                        //INS_SHIPMARK_4
				insShipma5,       		                          //INS_SHIPMARK_5
				t.get(6, String.class),                      //CF_CD
				t.get(7, String.class),                      //SERIES
				t.get(8, String.class),                      //CRM_CURR 
				t.get(9, String.class),                      //ICO_DESC
				t.get(10, String.class),                     //CO_CD
				t.get(11, String.class),                     //INP_ORG_CRITERIA
				scRemarks,                                   //SC_REMARK
				t.get(12, String.class),                     //IND_SC_AUTH_EMP
				t.get(13, BigDecimal.class).doubleValue(),   //SORT_SEQ
				t.get(14, String.class)                      //HS_CD
				)).collect(Collectors.toList());


		//Set Configuration Properties
		Map<String, Object> parameters = new HashMap<>();
		//   parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put("P_I_V_INVOICE_NO",invoiceNo);
		parameters.put("P_I_V_USER_ID",userId);
		//   parameters.put("prmSysDate",LocalDataTime.now());

		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("reportSizeLimit",
				tpexConfigRepository.findByName("invoiceGeneration.report.size.limit").getValue());
		config.put("storeInDB", "true");
		config.put("loginUserId", "TestUser");
		String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat :"pdf";
		String fileName =invoiceNo+"_SC_ATT"+"."+fileFormat;
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(fileName);

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(response, fileFormat, templateId, fileName, parameters, config);
		
		}else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(response, fileFormat, templateId, parameters, config, 0, sb, fileName);

		}
		return jasperResponse;
	}

}







